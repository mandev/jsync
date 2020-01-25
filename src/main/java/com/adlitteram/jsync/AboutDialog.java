package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class AboutDialog extends JDialog {

    private final MainFrame mainframe;

    public static void createAndShow(MainFrame mainframe) {
        AboutDialog abouFrame = new AboutDialog(mainframe);
        abouFrame.buildGui();
    }

    private AboutDialog(MainFrame mainframe) {
        super(mainframe, message("AboutjSync"), true);
        this.mainframe = mainframe;
    }

    private void buildGui() {
        JPanel pane1 = new AboutPanel();
        pane1.setBorder(new LineBorder(Color.gray));
        pane1.setPreferredSize(new Dimension(400, 120));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, pane1);

        JButton ok = new JButton(message("OK"));
        ok.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(ok);

        JPanel pane = new JPanel();
        pane.add(ok);

        getContentPane().add(BorderLayout.SOUTH, pane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setBounds(0, 0, 300, 120);
        pack();
        Utils.centerComponent(mainframe, this);
        setVisible(true);
    }

    private static class AboutPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            RenderingHints rq = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            rq.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHints(rq);

            Image img = Utils.loadImage("jsync.png");
            if (img != null) {
                g.drawImage(img, 15, 50, null);
            }

            g.setFont(getFont().deriveFont(20f));
            g.drawString(Main.NAME + " " + Main.RELEASE, 70, 35);

            g.setFont(getFont().deriveFont(16f));
            g.drawString("Build " + Main.BUILD, 70, 60);
            g.drawString(Main.SUPPORT, 70, 80);
            g.drawString("Copyright " + Main.COPYRIGHT, 70, 100);
        }
    };
}
