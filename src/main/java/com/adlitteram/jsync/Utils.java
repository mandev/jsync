package com.adlitteram.jsync;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;

public class Utils {

    public static Dimension getScreenDimension() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static void centerComponent(Component compo) {
        compo.setLocation(new Point((getScreenDimension().width - compo.getSize().width) / 2,
                (getScreenDimension().height - compo.getSize().height) / 2));
    }

    public static void centerComponent(Component parent, Component child) {
        Rectangle par = parent.getBounds();
        Rectangle chi = child.getBounds();
        child.setLocation(new Point(par.x + (par.width - chi.width) / 2, par.y + (par.height - chi.height) / 2));
    }

    // Load Image from Resource File
    public static final Image loadImage(String fileName) {
        URL url = Utils.class.getResource("/" + fileName);
        if (url == null) {
            System.err.print("Uname to load image : " + fileName);
            return null;
        }
        return Toolkit.getDefaultToolkit().getImage(url);
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
