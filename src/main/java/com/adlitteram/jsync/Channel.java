package com.adlitteram.jsync;

import static com.adlitteram.jsync.Messages.message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class Channel implements Runnable, Logger {

    public static final String[] STATUS = {
        message("StoppedStatus"),
        message("StartStatus"),
        message("ActivedStatus"),
        message("FailedStatus"),
        message("SyncStatus")};

    public static final int SIZE_OR_DATE = 0;
    public static final int SIZE_TEST = 1;
    public static final int DATE_TEST = 2;
    public static final int NO_TEST = 3;
    public static final int STOP = 0;
    public static final int START = 1;
    public static final int ACTIVE = 2;
    public static final int FAILED = 3;
    public static final int RUN = 4;

    private final MainFrame mainframe;
    private final StringBuffer logBuffer;

    private String ident;
    private String source;
    private String target;
    private double tempo = 60;
    private boolean rec = false;
    private boolean sup = false;
    private int test = SIZE_OR_DATE;
    private int status = STOP;
    private int nbFile;
    private int nbSupFile;
    private int nbCopyFile;

    private Thread clockThread;

    public static Channel create(MainFrame mainframe) {
        Channel channel = new Channel(mainframe);
        channel.getFrame().getLogArea().addLogger(channel);
        return channel;
    }

    private Channel(MainFrame mainframe) {
        this.logBuffer = new StringBuffer();
        this.mainframe = mainframe;
    }

    public MainFrame getFrame() {
        return mainframe;
    }

    public String getIdent() {
        return ident;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public double getTempo() {
        return tempo;
    }

    public boolean isRec() {
        return rec;
    }

    public boolean isSup() {
        return sup;
    }

    public int getTest() {
        return test;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setRec(boolean rec) {
        this.rec = rec;
    }

    public void setSup(boolean sup) {
        this.sup = sup;
    }

    public void setTest(int test) {
        this.test = test;
    }

    // Thread handler 
    public void start() {
        if (clockThread == null) {
            clockThread = new Thread(this);
            clockThread.setPriority(Thread.NORM_PRIORITY - 1);
            clockThread.start();
        }
    }

    @Override
    public void run() {
        File fsource = new File(source);
        File ftarget = new File(target);

        Thread myThread = Thread.currentThread();
        while (clockThread == myThread) {
            try {
                status = FAILED;
                if (!fsource.exists()) {
                    logMessage("Run() - " + source + " : " + message("DirNotExist") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                }
                else if (!fsource.isDirectory()) {
                    logMessage("Run() - " + source + " : " + message("IsNotDir") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                }
                else if (!ftarget.exists()) {
                    logMessage("Run() - " + target + " : " + message("DirNotExist") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                }
                else if (!ftarget.isDirectory()) {
                    logMessage("Run() - " + target + " : " + message("IsNotDir") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                }
                else if (fsource.equals(ftarget)) {
                    logMessage("Run() - " + message("SameSrcTrg") + " : " + target + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                }
                else {
                    status = RUN;
                    nbFile = nbCopyFile = nbSupFile = 0;
                    mainframe.fireTableModelChanged();
                    logMessage(message("StartSync") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                    repSync(fsource, ftarget);

                    logMessage(message("ProcessedFiles") + " : " + nbFile + " - "
                            + message("CopiedFiles") + " : " + nbCopyFile + " - "
                            + message("DeletedFiles") + " : " + nbSupFile);

                    logMessage(message("EndSync") + " - " + DateFormat.getDateTimeInstance().format(new Date()));
                    if (status == RUN) {
                        status = ACTIVE;
                    }
                }

                mainframe.fireTableModelChanged();
                Thread.sleep((long) tempo * 1000L * 60L);
            }
            catch (InterruptedException e) {
                status = FAILED;
                logMessage("Run() - " + e + " " + DateFormat.getDateTimeInstance().format(new Date()));
                mainframe.fireTableModelChanged();
            }
        }
    }

    public void stop() {
        clockThread = null;
        status = STOP;
        mainframe.fireTableModelChanged();
    }

    public void repSync(File src, File trg) {
        String srcname;
        File ftarget;

        File[] srcfiles = src.listFiles();
        if (srcfiles == null) {
            logMessage("repSync() : IO Exception - " + src.getPath() + " is not a directory or access is denied");
            return;
        }

        String trgpath = trg.getAbsolutePath() + File.separator;
        String[] trgFiles = trg.list();
        if (trgFiles == null) {
            logMessage("repSync() : IO Exception - " + trg.getPath() + " is not a directory or access is denied");
            return;
        }

        ArrayList<String> trgFilelist = new ArrayList<>(Arrays.asList(trgFiles));
        Collections.sort(trgFilelist);

        nbFile = nbFile + srcfiles.length;

        if (Main.DEBUG == 1) {
            logMessage("syncing : " + src.getPath());
        }

        int j;
        for (File srcfile : srcfiles) {
            srcname = srcfile.getName();
            ftarget = new File(trgpath + srcname);
            if ((j = Collections.binarySearch(trgFilelist, srcname)) >= 0) {
                if (srcfile.isDirectory()) {
                    if (rec) {
                        repSync(srcfile, ftarget);
                    }
                }
                else {
                    switch (test) {
                        case SIZE_OR_DATE:
                            if ((ftarget.lastModified() < srcfile.lastModified()) || (ftarget.length() != srcfile.length())) {
                                copyFile(srcfile, ftarget);
                            }
                            break;
                        case DATE_TEST:
                            if (ftarget.lastModified() < srcfile.lastModified()) {
                                copyFile(srcfile, ftarget);
                            }
                            break;
                        case SIZE_TEST:
                            if (ftarget.length() != srcfile.length()) {
                                copyFile(srcfile, ftarget);
                            }
                            break;
                        default:
                            copyFile(srcfile, ftarget);
                            break;
                    }
                }
                trgFilelist.remove(j);
            }
            else {
                if (srcfile.isDirectory()) {
                    if (rec) {
                        if (ftarget.exists() && ftarget.isFile()) {
                            ftarget.delete();
                        }
                        if (!ftarget.exists()) {
                            ftarget.mkdir();
                        }

                        if (ftarget.exists()) {
                            repSync(srcfile, ftarget);
                        }
                    }
                }
                else {
                    copyFile(srcfile, ftarget);
                }
            }
        }

        if (sup) {
            for (int i = 0; i < trgFilelist.size(); i++) {
                ftarget = new File(trgpath + trgFilelist.get(i));
                deleteAll(ftarget);
            }
        }
    }

    // Delete file or directory recursively
    public void deleteAll(File src) {
        if (src.isDirectory()) {
            for (File srcfile : src.listFiles()) {
                deleteAll(srcfile);
            }
        }
        if (Main.DEBUG == 1) {
            logMessage("deleting : " + src.getName());
        }
        nbSupFile++;
        src.delete();
    }

    // Copy a file to a another
    public void copyFile(File in, File out) {
        try (FileOutputStream fos = new FileOutputStream(out);
                FileInputStream fis = new FileInputStream(in)) {

            int c;
            byte[] data = new byte[4096];
            if (Main.DEBUG == 1) {
                logMessage("copying : " + in.getName());
            }
            while ((c = fis.read(data)) != -1) {
                fos.write(data, 0, c);
            }
            nbCopyFile++;
        }
        catch (IOException e1) {
            logMessage("copyFile() : IO Exception" + e1);
        }
    }

    public String getStatus() {
        return STATUS[status];
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
        logBuffer.append(ident).append(" - ").append(str).append("\n");
    }
}
