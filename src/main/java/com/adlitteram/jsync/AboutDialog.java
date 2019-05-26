/**
 * AboutFrame.java
 * Copyright (C) 2001 Emmanuel Deviller
 *
 * @version 2.1
 * @author Emmanuel Deviller
 */
package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author admin
 */
public class AboutDialog extends JDialog implements ActionListener {

   private final MainFrame mainframe;
   private JButton ok;

   /**
    *
    * @param mainframe
    */
   public static void createAndShow(MainFrame mainframe) {
      AboutDialog abouFrame = new AboutDialog(mainframe);
      abouFrame.buildGui();
   }

   /**
    * @param mainframe The parent component
    */
   private AboutDialog(MainFrame mainframe) {
      super(mainframe, message("AboutjSync"), true);
      this.mainframe = mainframe;
   }

   private void buildGui() {
      JPanel pane1 = new AboutPanel();
      pane1.setBorder(new LineBorder(Color.gray));
      pane1.setPreferredSize(new Dimension(300, 200));
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(BorderLayout.CENTER, pane1);

      getRootPane().setDefaultButton(ok);
      ok = new JButton(message("OK"));
      ok.addActionListener(this);

      JPanel pane = new JPanel();
      pane.add(ok);

      getContentPane().add(BorderLayout.SOUTH, pane);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setResizable(false);
      setBounds(0, 0, 300, 200);
      pack();
      Utils.centerComponentChild(mainframe, this);
      setVisible(true);
   }

   /**
    * Listener for ok and cancel button
    *
    * @param evt action event
    */
   @Override
   public void actionPerformed(ActionEvent evt) {
      if (evt.getSource() == ok) {
         setVisible(false);
         mainframe.repaint();
         dispose();
      }
   }

   private static class AboutPanel extends JPanel {

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         g.drawString("Build : " + Main.BUILD, 50, 102);
         g.drawString("Support : " + Main.SUPPORT, 50, 134);
         g.drawString("Copyright : " + Main.AUTHOR, 50, 175);
         g.setFont(getFont().deriveFont(Font.BOLD, 32f));
         g.drawString(Main.NAME + " " + Main.RELEASE, 50, 75);
      }
   };
}
