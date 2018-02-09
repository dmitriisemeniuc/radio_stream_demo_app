package com.dev.sdv.radiostreamingdemoapp.helper;

import android.util.Log;
import com.dev.sdv.radiostreamingdemoapp.BuildConfig;

public class Logger {

  private static boolean DEBUG = BuildConfig.DEBUG;

  public static void setDebug(boolean debug){
    DEBUG = debug;
  }

  public static void d(String msg){
    if(DEBUG) Log.d("", msg);
  }

  public static void d(String tag, String msg){
    if(DEBUG) Log.d(tag, msg);
  }

  public static void d(String tag, String msg, Throwable tr){
    if(DEBUG) Log.d(tag, msg, tr);
  }

  public static void d(String tag, String msg, String arg){
    if(DEBUG) Log.d(tag, String.format("%s %s", msg, arg));
  }

  public static void d(String tag, String msg, int arg){
    if(DEBUG) Log.d(tag, String.format("%s %d", msg, arg));
  }

  public static void d(String tag, String msg, double arg){
    if(DEBUG) Log.d(tag, String.format("%s %d", msg, arg));
  }

  public static void d(String tag, String msg, long arg){
    if(DEBUG) Log.d(tag, String.format("%s %d", msg, arg));
  }

  public static void i(String msg){
    Log.i("", msg);
  }

  public static void i(String tag, String msg){
    Log.i(tag, msg);
  }

  public static void i(String tag, String msg, Throwable tr){
    Log.i(tag, msg, tr);
  }

  public static void i(String tag, String msg, String arg){
    Log.i(tag, String.format("%s %s", msg, arg));
  }

  public static void i(String tag, String msg, int arg){
    Log.i(tag, String.format("%s %d", msg, arg));
  }

  public static void i(String tag, String msg, double arg){
    Log.i(tag, String.format("%s %d", msg, arg));
  }

  public static void i(String tag, String msg, long arg){
    Log.i(tag, String.format("%s %d", msg, arg));
  }

  public static void w(String msg){
    Log.w("", msg);
  }

  public static void w(String tag, String msg){
    Log.w(tag, msg);
  }

  public static void w(String tag, String msg, Throwable tr){
    Log.w(tag, msg, tr);
  }

  public static void w(String tag, String msg, String arg){
    Log.w(tag, String.format("%s %s", msg, arg));
  }

  public static void w(String tag, String msg, int arg){
    Log.w(tag, String.format("%s %d", msg, arg));
  }

  public static void w(String tag, String msg, double arg){
    Log.w(tag, String.format("%s %d", msg, arg));
  }

  public static void w(String tag, String msg, long arg){
    Log.w(tag, String.format("%s %d", msg, arg));
  }

  public static void e(String msg){
    Log.e("", msg);
  }

  public static void e(String tag, String msg){
    Log.e(tag, msg);
  }

  public static void e(String tag, String msg, Throwable tr){
    Log.e(tag, msg, tr);
  }

  public static void e(String tag, String msg, String arg){
    Log.e(tag, String.format("%s %s", msg, arg));
  }

  public static void e(String tag, String msg, int arg){
    Log.e(tag, String.format("%s %d", msg, arg));
  }

  public static void e(String tag, String msg, double arg){
    Log.e(tag, String.format("%s %d", msg, arg));
  }

  public static void e(String tag, String msg, long arg){
    Log.e(tag, String.format("%s %d", msg, arg));
  }
}
