/**
 * AboutFrame.java
 * Copyright (C) 2001 Emmanuel Deviller
 *
 * @version 2.1
 * @author Emmanuel Deviller
 */
package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

public class PrefDialog extends JDialog {

    private final MainFrame mainframe;

    private JRadioButton metalButton;
    private JRadioButton windowsButton;
    private JRadioButton motifButton;

    public static void createAndShow(MainFrame parent) {
        PrefDialog dialog = new PrefDialog(parent);
        dialog.buildGui();
    }

    private PrefDialog(MainFrame mainframe) {
        super(mainframe, message("Preferences"), true);
        this.mainframe = mainframe;
    }

    private void buildGui() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel container = new JPanel(new BorderLayout());
        container.add(buildLookPanel(), BorderLayout.CENTER);
        container.add(buildButtonPanel(), BorderLayout.SOUTH);
        getContentPane().add(container);
        pack();
        Utils.centerComponentChild(mainframe, this);
        setVisible(true);
    }

    private JPanel buildButtonPanel() {
        JButton okButton = new JButton(message("OK"));
        okButton.addActionListener((ActionEvent e) -> {
            // look anfd feel
            String lf = Utils.MOTIF;
            if (metalButton.isSelected()) {
                lf = Utils.METAL;
            }
            else if (windowsButton.isSelected()) {
                lf = Utils.WINDOWS;
            }
            mainframe.setLookAndFeel(lf);
            PrefDialog.this.dispose();
        });

        JButton cancelButton = new JButton(message("Cancel"));
        cancelButton.addActionListener((ActionEvent e) -> {
            PrefDialog.this.dispose();
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(cancelButton);
        panel.add(okButton);
        return panel;
    }

    private JPanel buildLookPanel() {
        metalButton = new JRadioButton("Metal Look & Feel");
        windowsButton = new JRadioButton("Windows Look & Feel");
        motifButton = new JRadioButton("Motif Look & Feel");

        ButtonGroup group = new ButtonGroup();
        group.add(metalButton);
        group.add(windowsButton);
        group.add(motifButton);

        String lf = mainframe.getLookAndFeel();
        if (null != lf) {
            switch (lf) {
                case Utils.METAL:
                    metalButton.setSelected(true);
                    break;
                case Utils.WINDOWS:
                    windowsButton.setSelected(true);
                    break;
                case Utils.MOTIF:
                    motifButton.setSelected(true);
                    break;
                default:
                    break;
            }
        }

        int w[] = {10, 0, 10};
        int h[] = {10, 0, 0, 0, 10};
        HIGLayout l = new HIGLayout(w, h);
        HIGConstraints c = new HIGConstraints();

        JPanel panel = new JPanel(l);
        panel.setBorder(new LineBorder(Color.gray));
        panel.add(metalButton, c.xy(2, 2));
        panel.add(windowsButton, c.xy(2, 3));
        panel.add(motifButton, c.xy(2, 4));
        return panel;
    }
}
