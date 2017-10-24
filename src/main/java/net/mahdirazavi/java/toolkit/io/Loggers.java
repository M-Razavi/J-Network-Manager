package net.mahdirazavi.java.toolkit.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * The Class Loggers.
 *
 */
public class Loggers {

  // sorted alphabetically

  /** The Constant Algorithm (section based logger). */
  public static final Logger Algorithm = Logger.getLogger("alg");

  /** The Constant Application (section based logger). */
  public static final Logger Application = Logger.getLogger("app");

  /** The Constant Network (section based logger). */
  public static final Logger Network = Logger.getLogger("net");

  /** The Constant Packet (section based logger). */
  public static final Logger Packet = Logger.getLogger("pkt");

  /** The Constant PacketFlow (logic based logger). */
  public static final Logger PacketFlow = Logger.getLogger("flow");


  static {
    DateFormat dateFormat = new SimpleDateFormat("(yyyy-MM-dd)HH-mm-ss");
    Date date = new Date();
    System.setProperty("logfilename", dateFormat.format(date));
    DOMConfigurator.configure("config/loggers.xml");
  }

}
