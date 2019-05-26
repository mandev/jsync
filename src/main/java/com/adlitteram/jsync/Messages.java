/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jsync;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author admin
 */
public class Messages {

   public final static ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

   public static String message(String str) {
      try {
         return BUNDLE.getString(str);
      }
      catch (Exception e) {
         return str;
      }
   }
}
