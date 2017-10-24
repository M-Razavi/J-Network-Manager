/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jun 17, 2015-9:52:23 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.net.InetAddress;

/**
 * {@link NetNode} represents a node in network with a IP:Port pair. {@link NetNode} can be used for
 * source or destination node in network.
 * 
 * @author Mahdi Razavi
 * @version 1.0
 * @created 11-May-2015 11:10:13 AM
 */
public class NetNode {

  /** The name. */
  private String name;

  /** The IP address. */
  private InetAddress IPAddress;

  /** The port. */
  private int port;

  /**
   * Instantiates a new net node.
   * 
   * @param name the name
   * @param IPAddress the IP address
   * @param port the port
   */
  public NetNode(String name, InetAddress IPAddress, int port) {
    this.name = name;
    if(IPAddress == null)
    {
      throw new ExceptionInInitializerError("IP address could not be null in NetNod.");
    }
    this.IPAddress = IPAddress;
    this.port = port;
  }

  /**
   * Gets the IP address.
   * 
   * @return the IP address
   */
  public InetAddress getIPAddress() {
    return IPAddress;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the port.
   * 
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * Sets anode IP address.
   * 
   * @param IPAddress the new IP address
   */
  public void setIPAddress(InetAddress IPAddress) {
    if(IPAddress == null)
    {
      throw new ExceptionInInitializerError("IP address could not be null in NetNod.");
    }
    this.IPAddress = IPAddress;
  }

  /**
   * Sets a node name.
   * 
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets node port.
   * 
   * @param port the new port
   */
  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof NetNode) {
      NetNode that = (NetNode) obj;

      /*
       * if ((this.IPAddress.equals(that.IPAddress)) // && (this.port == that.port) // &&
       * (this.name.equals(that.name))) { return true;
       */
      if ((this.IPAddress.equals(that.IPAddress)) //
          && (this.port == that.port)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    // return this.IPAddress.hashCode() ^ this.port ^ this.name.hashCode();
    return this.IPAddress.hashCode() ^ this.port;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Name=" + name //
        + " " + IPAddress //
        + ":" + port;
  }


}
