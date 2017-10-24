/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jul 28, 2015-11:32:14 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

/**
 * The Class NetData is a container for data that  transport in network layer.
 * 
 * @author Mahdi Razavi
 * @version 1.0
 * @created 11-May-2015 11:10:12 AM
 */
public class NetData {

  /** The data. */
  private final byte data[];

  /** The length. */
  private final int length;

  /** The offset. */
  private final int offset;

  /** The source. */
  private final NetNode source;

  /** The destination. */
  private final NetNode destination;
  
  private final boolean isError;
  
  private final Exception errorDetails;

  /**
   * Instantiates a new netdata only with data.
   * 
   * @param data the data
   * @param offset the offset
   * @param length length
   */
  public NetData(byte[] data, int offset, int length) {
    this(data,offset,length,null,null);
  }

  /**
   * Instantiates a new netdata with data and source/destination address.
   * 
   * @param data the data
   * @param offset the offset
   * @param length the length
   * @param source the source
   * @param destination the destination
   */
  public NetData(byte[] data, int offset, int length, NetNode source, NetNode destination) {
    this.data = data;
    this.offset = offset;
    this.length = length;
    this.source = source;
    this.destination = destination;
    this.isError = false;
    this.errorDetails = null;
  }
  
  /**
   * Instantiates a new net data only with error details.
   *
   * @param source the source
   * @param destination the destination
   * @param isError the is error
   * @param errorDetails the error details
   */
  public NetData(NetNode source, NetNode destination,boolean isError,Exception errorDetails) {
    this.data = null;
    this.offset = 0;
    this.length = 0;
    
    this.source = source;
    this.destination=destination;
    
    this.isError = true;
    this.errorDetails = errorDetails;    
  }

  /**
   * Gets the data.
   * 
   * @return the data
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Gets the length.
   * 
   * @return the length
   */
  public int getLength() {
    return length;
  }

  /**
   * Gets the data offset.
   * 
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Gets the destination node.
   * 
   * @return the destination
   */
  public NetNode getDestination() {
    return destination;
  }

  /**
   * Gets the source node.
   * 
   * @return the source
   */
  public NetNode getSource() {
    return source;
  }

  /**
   * Checks if is error.
   *
   * @return the isError
   */
  public boolean isError() {
    return isError;
  }

  /**
   * Gets the error details if error occurred.
   *
   * @return the errorDetails
   */
  public Exception getErrorDetails() {
    return errorDetails;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof NetData) {
      NetData that = (NetData) obj;

      if ((this.data.equals(that.data)) //
          && (this.length == that.length) //
          && (this.offset == that.offset) //
          && (this.destination.equals(that.destination)) //
          && (this.source.equals(that.source))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ((this.length * 31 + this.offset) * 31 + this.data.hashCode())
        ^ (this.source.hashCode() * 31 + this.destination.hashCode());
  }

}
