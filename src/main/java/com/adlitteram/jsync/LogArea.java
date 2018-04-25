/**
 * LogArea.java
 * Copyright (C) 2000 Emmanuel Deviller
 *
 * @version 2.0
 * @author Emmanuel Deviller
 */
package com.adlitteram.jsync;

import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LogArea extends JTextArea implements Runnable {

    private int maxLogSize;
    private final ArrayList<LogBuffer> logBufferList = new ArrayList<>();

    public static LogArea create() {
        return create(250000, "", 10, 40);
    }

    public static LogArea create(int size, String str, int row, int col) {
        LogArea logArea = new LogArea(size, str, row, col);
        new Thread(logArea).start();
        return logArea;
    }

    private LogArea(int size, String str, int row, int col) {
        super(str, row, col);
        buildGui(size);
    }

    private void buildGui(int size) {
        maxLogSize = size;
        setEditable(false);
        setDocument(new LogDocument());
    }

    public void reset() {
        setDocument(new LogDocument());
    }

    @Override
    public void run() {
        StringBuilder buffer = new StringBuilder();

        while (true) {
            for (LogBuffer logBuffer : logBufferList) {
                if (logBuffer != null) {
                    buffer.append(logBuffer.flushLogBuffer());
                }
            }

            if (buffer.length() > 0) {
                SwingUtilities.invokeLater(() -> {
                    LogArea.this.append(buffer.toString());
                    LogArea.this.setCaretPosition(LogArea.this.getDocument().getLength());
                    buffer.setLength(0);
                });
            }

            Utils.sleep(1000L);
        }
    }

    public void addWriter(LogBuffer logBuffer) {
        logBufferList.add(logBuffer);
    }

    private class LogDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offs, str, a);
            int tooMany = getLength() - maxLogSize;
            if (tooMany > 0) {
                remove(0, tooMany);
            }
        }
    }
}
