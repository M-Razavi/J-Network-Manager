package net.mahdirazavi.java.toolkit.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Class Loggers.
 */
public class Loggers {

    // sorted alphabetically

    /**
     * The Constant Algorithm (section based logger).
     */
    public static final Logger Algorithm = LogManager.getLogger("alg");

    /**
     * The Constant Application (section based logger).
     */
    public static final Logger Application = LogManager.getLogger("app");

    /**
     * The Constant Network (section based logger).
     */
    public static final Logger Network = LogManager.getLogger("net");

    /**
     * The Constant Packet (section based logger).
     */
    public static final Logger Packet = LogManager.getLogger("pkt");

    /**
     * The Constant PacketFlow (logic based logger).
     */
    public static final Logger PacketFlow = LogManager.getLogger("flow");

}
