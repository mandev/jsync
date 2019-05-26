/**
 * Copyright (C) 1999-2018 Emmanuel Deviller
 *
 * @author Emmanuel Deviller
 */
package com.adlitteram.jsync;

import java.io.File;

public class Main {

   public static final int DEBUG = 0;

   public static final String COPYRIGHT = "Emmanuel Deviller";
   public static final String AUTHOR = "Emmanuel Deviller";
   public static final String NAME = "jSync";
   public static final String RELEASE = "2.5";
   public static final String BUILD = "30-04-2018";
   public static final String SUPPORT = "support@exurbi.com";

   public static final String HOME_DIR = System.getProperty("user.home") + File.separator;
   public static final String CNF_DIR = HOME_DIR + "." + NAME + File.separator;
   public static final String CNF_FILE = CNF_DIR + NAME + ".ini";
   public static final String USER_PROPS = "com.adlitteram.jsync.messages";

   public static void main(String[] args) {

      // Create user's directories
      File dir = new File(CNF_DIR);
      if (!dir.exists()) {
         boolean status = dir.mkdir();
         if (!status) {
            throw new RuntimeException("Unable to create " + dir);
         }
      }

      // Display MainFrame
      MainFrame.createAndShow();
   }
}
