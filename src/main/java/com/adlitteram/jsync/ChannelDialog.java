/**
 * ChannelProperties.java
 * Copyright (C) 1999-2001 Emmanuel Deviller
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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class ChannelDialog extends JDialog {

    private final Channel channel;
    private final MainFrame mainframe;

    private static final String[] TEST_TYPE = {
        message("SIZE_OR_DATE"),
        message("DATE_TEST"),
        message("SIZE_TEST"),
        message("NO_TEST")};

    private JTextField identField, sourceField, targetField, tempoField;
    private JCheckBox recCheck, supCheck;
    private JComboBox testCombo;

    public static void createAndShow(MainFrame mainframe, Channel channel) {
        ChannelDialog channelFrame = new ChannelDialog(mainframe, channel);
        channelFrame.buildGui();
    }

    private ChannelDialog(MainFrame mainframe, Channel channel) {
        super(mainframe, message("ChannelProperties"), true);
        this.mainframe = mainframe;
        this.channel = channel;
    }

    private void buildGui() {
        channel.stop();
        getContentPane().add(buildPropertiesPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        pack();
        Utils.centerComponentChild(mainframe, this);
        setVisible(true);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton(message("Cancel"));
        cancelButton.addActionListener((ActionEvent e) -> {
            cancelPressed();
        });

        JButton okButton = new JButton(message("OK"));
        okButton.addActionListener((ActionEvent e) -> {
            oKPressed();
        });

        panel.add(cancelButton);
        panel.add(okButton);
        return panel;
    }

    private JPanel buildPropertiesPanel() {
        identField = new JTextField(channel.getIdent(), 15);
        sourceField = new JTextField(channel.getSource(), 20);
        targetField = new JTextField(channel.getTarget(), 20);
        tempoField = new JTextField(String.valueOf(channel.getTempo()), 5);
        recCheck = new JCheckBox(message("IncludeSubDir"), channel.isRec());
        supCheck = new JCheckBox(message("DeleteRemoteFiles"), channel.isSup());

        testCombo = new JComboBox(TEST_TYPE);
        testCombo.setSelectedIndex(channel.getTest());

        JButton browseButton0 = new JButton("...");
        browseButton0.setMargin(new Insets(0, 5, 0, 5));
        browseButton0.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser(channel.getSource());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showDialog(mainframe, message("Select")) == JFileChooser.APPROVE_OPTION) {
                sourceField.setText(fc.getSelectedFile().getPath());
            }
        });

        JButton browseButton1 = new JButton("...");
        browseButton1.setMargin(new Insets(0, 5, 0, 5));
        browseButton1.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser(channel.getSource());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showDialog(mainframe, message("Select")) == JFileChooser.APPROVE_OPTION) {
                targetField.setText(fc.getSelectedFile().getPath());
            }
        });

        int w0[] = {5, 0, 5, 0, 5, 0, 5};
        int h0[] = {10, 0, 0, 0, 0, 0, 0, 5, 0, 10};
        HIGLayout l0 = new HIGLayout(w0, h0);
        HIGConstraints c0 = new HIGConstraints();
        l0.setColumnWeight(4, 1);

        JPanel p0 = new JPanel(l0);
        p0.setBorder(new LineBorder(Color.gray));
        p0.add(new JLabel("Nom du canal"), c0.xy(2, 2, "r"));
        p0.add(identField, c0.xy(4, 2, "l"));
        p0.add(new JLabel(message("SrcDirectory")), c0.xy(2, 3, "r"));
        p0.add(sourceField, c0.xy(4, 3, "lr"));
        p0.add(browseButton0, c0.xy(6, 3, "l"));
        p0.add(new JLabel(message("TrgDirectory")), c0.xy(2, 4, "r"));
        p0.add(targetField, c0.xy(4, 4, "lr"));
        p0.add(browseButton1, c0.xy(6, 4, "l"));
        p0.add(new JLabel(message("Tempo")), c0.xy(2, 5, "r"));

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p1.add(tempoField);
        p1.add(new JLabel(" " + message("Minutes")));

        p0.add(p1, c0.xy(4, 5, "l"));
        p0.add(recCheck, c0.xy(4, 6, "l"));
        p0.add(supCheck, c0.xy(4, 7, "l"));

        p0.add(new JLabel(message("CopySourceFile")), c0.xy(2, 9, "l"));
        p0.add(testCombo, c0.xy(4, 9, "l"));
        return p0;
    }

    private void oKPressed() {
        if (identField.getText().length() < 2) {
            Toolkit.getDefaultToolkit().beep();
            mainframe.logMessage(message("ChannelNeed2Char"));
            return;
        }

        try {
            channel.setTempo(Double.parseDouble(tempoField.getText()));
            if (channel.getTempo() < 0.01) {
                Toolkit.getDefaultToolkit().beep();
                mainframe.logMessage(message("TempoNeed1Sec"));
                return;
            }
        }
        catch (NumberFormatException ex) {
            Toolkit.getDefaultToolkit().beep();
            mainframe.logMessage(message("BadTempo") + " - " + tempoField.getText());
            return;
        }

        channel.setIdent(identField.getText());
        channel.setSource(sourceField.getText());
        channel.setTarget(targetField.getText());
        channel.setRec(recCheck.isSelected());
        channel.setSup(supCheck.isSelected());
        channel.setTest(testCombo.getSelectedIndex());

        dispose();
    }

    private void cancelPressed() {
        dispose();
    }
}
