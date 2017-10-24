/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jul 26, 2015-9:43:40 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;


/**
 * The listener interface for receiving network events. The class that is interested in processing a
 * network event implements this interface, and the object created with that class is registered
 * with some add event in {@link NetClientEvents} such as
 * {@link NetClientEvents#addOnSend(NetClientEventsListener)}. When any network event occurs, that
 * object's appropriate method is invoked.
 * 
 * @see NetClientEvents
 */
public interface NetClientEventsListener {

  /**
   * This method is called whenever the Network object received data. Received data as @Object and
   * receiver as @IOConnection sent to it method.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onReceive(Object sender, Object data);

  /**
   * This method is called whenever the Network object got error during receiving. Exception as @Object
   * and receiver as @IOConnection sent to it method.
   * 
   * @param sender the sender
   * @param data the exception
   */
  void onReceiveError(Object sender, Object data);

  /**
   * This method is called whenever the Network object sent data. Sent data length as @Object and
   * sender as @Object sent to it method.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onSend(Object sender, Object data);

  /**
   * This method is called whenever the Network object got error during Sending. Exception as @Object
   * and sender as @IOConnection sent to it method.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onSendError(Object sender, Object data);

  /**
   * This method is called whenever the Network object connected to destination.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onConnect(Object sender, Object data);

}
