/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 *
 * 
 * 
 * LastEdit Jun 17, 2015-8:21:54 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * The class NetConnection handle a IOConnection to communicate with network. Working easier and
 * avoid details is the main purpose of this class. source and destination should initialize in
 * constructor.
 *
 * @author Mahdi Razavi
 * @version 1.0
 * @updated 19-May-2015 2:22:49 PM
 */
public abstract class NetConnection {

  /** The destination. */
  protected final NetNode destination;

  /** The source. */
  protected final NetNode source;

  /** The name of connection. */
  protected String name;

  /** The networkIO contain a network connection which implemented the {@link IOConnection}. */
  protected IOConnection networkIO;

  /**
   * Initialize network connection filed.
   *
   * @param networkIO network TCP or UDP connection
   * @param name connection name as Identifier
   * @param source source node
   * @param destination the destination
   */
  public NetConnection(IOConnection networkIO, String name, NetNode source, NetNode destination) {
    this.networkIO = networkIO;
    this.name = name;
    this.source = source;
    this.destination = destination;
  }

  /**
   * Get connection name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get destination node.
   *
   * @return the destination
   */
  public NetNode getDestination() {
    return this.destination;
  }

  /**
   * Get source node.
   *
   * @return the source
   */
  public NetNode getSource() {
    return this.source;
  }

  /**
   * Receive data from network.
   *
   * @return NetData
   * @throws InterruptedException the interrupted exception
   */
  public abstract NetData Receive() throws InterruptedException;

  /**
   * Send data to network.
   *
   * @param data the data
   * @throws InterruptedException the interrupted exception
   * @throws Exception 
   */
  public abstract void Send(NetData data) throws InterruptedException, Exception;

  /**
   * Set connection name as Identifier.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Close network connection.
   */
  public abstract void close();

}
