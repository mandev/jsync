package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

public class MainFrame extends JFrame implements Logger {

    private StringBuffer logBuffer;
    private String lookAndFeel;

    private final ArrayList<Channel> channelList = new ArrayList<>();
    private ChannelTableModel channelTableModel;
    private JTable channelTable;
    private LogArea logArea;
    private int lastSelectedRow;

    public static void createAndShow() {
        MainFrame mainframe = new MainFrame();
        mainframe.buildGui();
    }

    private MainFrame() {
        super(Main.NAME + " " + Main.RELEASE);
    }

    private void buildGui() {

        logBuffer = new StringBuffer();

        // Log pane
        logArea = LogArea.create();
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Table pane
        channelTableModel = new ChannelTableModel();
        channelTable = new JTable(channelTableModel);
        channelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = channelTable.getSelectionModel();
        rowSM.addListSelectionListener((ListSelectionEvent e) -> {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.isSelectionEmpty()) {
                while (lastSelectedRow >= channelList.size()) {
                    lastSelectedRow--;
                }
                if (lastSelectedRow >= 0) {
                    lsm.setSelectionInterval(lastSelectedRow, lastSelectedRow);
                }
            }
            else {
                lastSelectedRow = lsm.getMinSelectionIndex();
            }
        });

        // Create parent tableview scrollpane
        JScrollPane tableScrollPane = new JScrollPane(channelTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 50));
        tableScrollPane.getViewport().setBackground(Color.white);

        // Put the editor pane and the text pane in a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, logScrollPane);
        setContentPane(splitPane);

        // Creation des menus
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(buildFileMenu());
        menuBar.add(buildChanMenu());
        menuBar.add(buildHelpMenu());
        setJMenuBar(menuBar);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        // Load the config after the log area is created
        logArea.addLogger(MainFrame.this);
        loadConfig();

        // Init LookAndFeel
        if (lookAndFeel == null) {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        // Top Left Icon
        setIconImage(Utils.loadImage("jsync.png"));

        // Layout the current frame
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((scr.width - 400) / 2, (scr.height - 200) / 2, 400, 100);
        pack();
        setVisible(true);
        splitPane.setDividerLocation(.5);
    }

    public LogArea getLogArea() {
        return logArea;
    }

    public void fireTableModelChanged() {
        channelTableModel.fireTableDataChanged();
    }

    // File Menu
    protected JMenu buildFileMenu() {
        JMenu menu = new JMenu(message("Files"));
        JMenuItem det = new JMenuItem(message("StartAllChannels"));
        JMenuItem art = new JMenuItem(message("StopAllChannels"));
        JMenuItem res = new JMenuItem(message("ClearLogWindow"));
        JMenuItem prf = new JMenuItem(message("Preferences") + "...");
        JMenuItem quit = new JMenuItem(message("Quit"));

        art.addActionListener(e -> channelList.forEach(channel -> channel.stop()));
        det.addActionListener(e -> channelList.forEach(channel -> channel.start()));
        res.addActionListener(e -> logArea.reset());
        prf.addActionListener(e -> PrefDialog.createAndShow(MainFrame.this));
        quit.addActionListener(e -> quit());

        menu.add(det);
        menu.add(art);
        menu.add(new JSeparator());
        menu.add(res);
        menu.add(prf);
        menu.add(new JSeparator());
        menu.add(quit);
        return menu;
    }

    // Channel Menu
    protected JMenu buildChanMenu() {
        JMenu menu = new JMenu(message("Channel"));
        JMenuItem nou = new JMenuItem(message("New") + "...");
        JMenuItem mod = new JMenuItem(message("Modify") + "...");
        JMenuItem dem = new JMenuItem(message("Start"));
        JMenuItem arr = new JMenuItem(message("Stop"));
        JMenuItem sup = new JMenuItem(message("Delete"));

        nou.addActionListener(e -> {
            Channel channel = Channel.create(MainFrame.this);
            ChannelDialog.createAndShow(MainFrame.this, channel);
            if (channel.getIdent() != null) {
                channelList.add(channel);
                channelTableModel.fireTableDataChanged();
                saveConfig();
            }
            repaint();
        });

        sup.addActionListener(e -> {
            int index = channelTable.getSelectedRow();
            if (index >= 0 && index < channelList.size()) {
                Channel ch = channelList.get(index);
                ch.stop();
                channelList.remove(index);
                channelTableModel.fireTableDataChanged();
                saveConfig();
            }
        });

        arr.addActionListener(e -> {
            int index = channelTable.getSelectedRow();
            if (index >= 0 && index < channelList.size()) {
                channelList.get(index).stop();
            }
        });

        dem.addActionListener(e -> {
            int index = channelTable.getSelectedRow();
            if (index >= 0 && index < channelList.size()) {
                channelList.get(index).start();
            }
        });

        mod.addActionListener(e -> {
            int index = channelTable.getSelectedRow();
            if (index >= 0 && index < channelList.size()) {
                ChannelDialog.createAndShow(MainFrame.this, channelList.get(index));
                channelTableModel.fireTableDataChanged();
                saveConfig();
            }
            repaint();
        });

        menu.add(nou);
        menu.add(mod);
        menu.add(new JSeparator());
        menu.add(dem);
        menu.add(arr);
        menu.add(new JSeparator());
        menu.add(sup);
        return menu;
    }

    protected JMenu buildHelpMenu() {
        JMenu help = new JMenu(message("Help"));
        JMenuItem about = new JMenuItem(message("About") + "...");
        about.addActionListener(e -> AboutDialog.createAndShow(MainFrame.this));
        help.add(about);
        return help;
    }

    public void quit() {
        saveConfig();
        System.exit(0);
    }

    public String getLookAndFeel() {
        return lookAndFeel;
    }

    public void setLookAndFeel(String lf) {
        try {
            UIManager.setLookAndFeel(lf);
            SwingUtilities.updateComponentTreeUI(MainFrame.this);
            lookAndFeel = lf;
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("MainFrame.updateLookAndFeel() : " + e);
        }
    }

    // Save the configuration file
    public void saveConfig() {
        StringBuilder buf = new StringBuilder();
        buf.append("Look:").append(getLookAndFeel());

        for (Channel ch : channelList) {
            buf.append("\nBeginChannel:")
                    .append("\nIdent:").append(ch.getIdent())
                    .append("\nSource:").append(ch.getSource())
                    .append("\nTarget:").append(ch.getTarget())
                    .append("\nTempo:").append(ch.getTempo())
                    .append("\nRecurs:").append(ch.isRec())
                    .append("\nSupres:").append(ch.isSup())
                    .append("\nTest:").append(ch.getTest())
                    .append("\nEndChannel:");
        }

        try (FileWriter fw = new FileWriter(Main.CNF_FILE)) {
            fw.write(buf.toString());
        }
        catch (IOException ex) {
            logMessage("saveConfig() : " + message("ConfigSaveError") + " - " + ex);
        }
    }

    // Load the configuration file
    public void loadConfig() {
        String input, motg, motd;
        int index, etat;
        Channel ch = null;

        try (FileReader fr = new FileReader(Main.CNF_FILE);
                BufferedReader br = new BufferedReader(fr)) {
            etat = 1;
            while ((input = br.readLine()) != null) {
                index = input.indexOf(':');
                motg = input.substring(0, index);
                motd = input.substring(index + 1);
                switch (etat) {
                    case 1:
                        if (motg.compareTo("Look") == 0) {
                            setLookAndFeel(motd);
                        }
                        else if (motg.compareTo("BeginChannel") == 0) {
                            etat = 2;
                            ch = Channel.create(MainFrame.this);
                        }
                        break;

                    case 2:
                    default:
                        if (ch != null) {
                            if (motg.compareTo("Ident") == 0) {
                                ch.setIdent(motd);
                            }
                            else if (motg.compareTo("Source") == 0) {
                                ch.setSource(motd);
                            }
                            else if (motg.compareTo("Target") == 0) {
                                ch.setTarget(motd);
                            }
                            else if (motg.compareTo("Tempo") == 0) {
                                ch.setTempo(Double.parseDouble(motd));
                            }
                            else if (motg.compareTo("Recurs") == 0) {
                                ch.setRec(Boolean.valueOf(motd));
                            }
                            else if (motg.compareTo("Supres") == 0) {
                                ch.setSup(Boolean.valueOf(motd));
                            }
                            else if (motg.compareTo("Test") == 0) {
                                ch.setTest(Integer.parseInt(motd));
                            }
                            else if (motg.compareTo("EndChannel") == 0) {
                                etat = 1;
                                channelList.add(ch);
                            }
                        }
                        break;
                }
            }
        }
        catch (IOException ex) {
            logMessage("loadConfig() : " + ex);
        }
        channelTableModel.fireTableDataChanged();
    }

    // Return the buffer to the logBuffer then reset
    @Override
    public String peekLog() {
        String str = logBuffer.toString();
        logBuffer.setLength(0);
        return str;
    }

    // Append a string to the logBuffer
    public void logMessage(String str) {
        logBuffer.append(str).append("\n");
    }

    public class ChannelTableModel extends AbstractTableModel {

        final String[] names = {message("ChannelName"),
            message("SrcDir"),
            message("TrgDir"),
            message("Status")};

        @Override
        public int getColumnCount() {
            return names.length;
        }

        @Override
        public int getRowCount() {
            return channelList.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            Channel ch = channelList.get(row);
            switch (col) {
                case 0:
                    return ch.getIdent();
                case 1:
                    return ch.getSource();
                case 2:
                    return ch.getTarget();
                default:
                    return ch.getStatus();
            }
        }

        @Override
        public String getColumnName(int column) {
            return names[column];
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
        }
    }
}
