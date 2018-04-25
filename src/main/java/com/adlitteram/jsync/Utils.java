/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jsync;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;

/**
 *
 * @author admin
 */
public class Utils {

    // Look&Feel
    public static final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
    public static final String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    public static final String MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    // GUI Utilities
    public static Dimension getScreenDimension() {
        return getMyToolkit().getScreenSize();
    }

    public static void centerComponent(Component compo) {
        compo.setLocation(new Point((getScreenDimension().width - compo.getSize().width) / 2,
                (getScreenDimension().height - compo.getSize().height) / 2));
    }

    public static void centerComponentChild(Component parent, Component child) {
        Rectangle par = parent.getBounds();
        Rectangle chi = child.getBounds();
        child.setLocation(new Point(par.x + (par.width - chi.width) / 2, par.y + (par.height - chi.height) / 2));
    }

    public static Toolkit getMyToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    // Load Image from Resource File
    public static final Image loadImage(String fileName) {
        URL url = Utils.class.getResource("/" + fileName);
        return (url == null) ? null : Toolkit.getDefaultToolkit().getImage(url);
    }

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
