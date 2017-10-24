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
 * with a component using the component's <code>addNetEventsListener<code> method. When
 * the netEvents event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see NetClientEvents
 */
public interface NetServerEventsListener {

  /**
   * This method is called whenever a network client connect to the Network server. client socket as @Object
   * and sender as @Observable sent to it method.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onClientConnect(Object sender, Object data);

  /**
   * This method is called whenever an error happen in  network server.
   * 
   * @param sender the sender
   * @param data the data
   */
  void onError(Object sender, Object data);

}
