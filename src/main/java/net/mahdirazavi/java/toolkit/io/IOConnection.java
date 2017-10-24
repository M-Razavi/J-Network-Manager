/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Aug 4, 2015-4:10:20 PM Using JRE 1.8.0
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.io;

import java.io.IOException;

import net.mahdirazavi.java.toolkit.net.NetClientEvents;
import net.mahdirazavi.java.toolkit.net.NetNode;

/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * LastEdit Jan 14, 2015-1:40:50 PM Using JRE 1.7.0_55
 * 
 * The Class IOConnection. This abstract class implement Runnable interface for synchronized
 * receiving, and extend Observable class for event in receive data.
 * 
 * @see
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.2
 * @created 19-May-2015 3:05:35 PM
 */
public abstract class IOConnection extends NetClientEvents implements Runnable {

  public IOConnection(String info) {
    super(info);
  }

  protected boolean isError = false;


  /**
   * Gets the receive data.
   * 
   * @return the receive data
   */
  public abstract byte[] getReceiveData();

  /**
   * Initialize the IO connection..
   * 
   * @throws IOException
   */
  public abstract void initialize() throws IOException;

  /**
   * Close the IO connection.
   * 
   * @throws IOException
   */
  public abstract void closeIO() throws IOException;

  /**
   * Send data.
   * 
   * @param data
   * @param offset
   * @param length
   * @return
   * @throws IOException 
   */
  public abstract int send(byte[] data, int offset, int length) throws IOException;

  /**
   * Send data.
   * 
   * @param data the data
   * @return the int
   * @throws IOException 
   */
  public int send(byte[] data) throws IOException {
    return send(data, 0, data.length);
  }

  /**
   * Implement receive method for receiving data use onReceive.fire() to notify all observers.
   */
  public abstract void receive();

  /**
   * check any error occurred.
   * 
   * @return the isError state
   */
  public boolean isError() {
    return isError;
  }


  /**
   * Gets the remote network node.
   * 
   * @return the remote node
   */
  public abstract NetNode getRemoteNode();

  /**
   * Gets the local network node.
   * 
   * @return the local node
   */
  public abstract NetNode getLocalNode();


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "IOConnection. local=" + getLocalNode() + " remote=" + getRemoteNode();
  }

}
