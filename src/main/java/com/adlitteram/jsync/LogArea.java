package com.adlitteram.jsync;

import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LogArea extends JTextArea implements Runnable {

    private int maxLogSize;
    private final ArrayList<Logger> loggerList = new ArrayList<>();

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
        StringBuilder logBuffer = new StringBuilder();

        while (true) {
            for (Logger logger : loggerList) {
                if (logger != null) {
                    logBuffer.append(logger.peekLog());
                }
            }

            if (logBuffer.length() > 0) {
                SwingUtilities.invokeLater(() -> {
                    LogArea.this.append(logBuffer.toString());
                    LogArea.this.setCaretPosition(LogArea.this.getDocument().getLength());
                    logBuffer.setLength(0);
                });
            }

            Utils.sleep(1000L);
        }
    }

    public void addLogger(Logger logger) {
        loggerList.add(logger);
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
