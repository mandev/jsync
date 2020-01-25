package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

public class PrefDialog extends JDialog {

    private final MainFrame mainframe;
    private String lookandfeel;

    public static void createAndShow(MainFrame parent) {
        PrefDialog dialog = new PrefDialog(parent);
        dialog.buildGui();
    }

    private PrefDialog(MainFrame mainframe) {
        super(mainframe, message("Preferences"), true);
        this.mainframe = mainframe;
        this.lookandfeel = mainframe.getLookAndFeel();
    }

    private void buildGui() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel(new BorderLayout());
        container.add(buildLookPanel(), BorderLayout.CENTER);
        container.add(buildButtonPanel(), BorderLayout.SOUTH);
        getContentPane().add(container);
        pack();
        Utils.centerComponent(mainframe, this);
        setVisible(true);
    }

    private JPanel buildButtonPanel() {
        JButton okButton = new JButton(message("OK"));
        okButton.addActionListener(e -> {
            if (lookandfeel != null) {
                mainframe.setLookAndFeel(lookandfeel);
            }
            PrefDialog.this.dispose();
        });

        JButton cancelButton = new JButton(message("Cancel"));
        cancelButton.addActionListener(e -> PrefDialog.this.dispose());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(Box.createRigidArea(new Dimension(100, 0)));
        panel.add(cancelButton);
        panel.add(okButton);
        return panel;
    }

    private JPanel buildLookPanel() {
        ButtonGroup group = new ButtonGroup();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            JRadioButton button = new JRadioButton(info.getName());
            button.addActionListener(e -> lookandfeel = info.getClassName());
            button.setSelected(Objects.equals(lookandfeel, info.getClassName()));
            group.add(button);
            panel.add(button);
        }
        return panel;
    }

}
