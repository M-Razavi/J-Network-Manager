/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * This class extend Observable class. It can be used to represent an object that the application
 * wants to have observed.
 * 
 * LastEdit Jan 14, 2015-11:31:02 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.util.Observable;

/**
 * This class extend Observable class. It can be used to represent an object that the application
 * wants to have observed.
 */
public class NetEvent extends Observable {

  /** The doer of action. */
  private String doer;

  /**
   * Instantiates a new net event.
   *
   * @param doer the doer
   */
  public NetEvent(String doer) {
    this.doer = doer;
  }

  /**
   * Fire an event.
   */
  public void fire() {
    super.setChanged();
    notifyObservers();
  }

  /**
   * Fire an event with data.
   * 
   * @param eventData the event data
   */
  public void fire(Object eventData) {
    super.setChanged();
    notifyObservers(eventData);
  }

  /**
   * Gets the doer of action.
   *
   * @return the doer
   */
  public String getDoer() {
    return doer;
  }

  /**
   * Sets the doer of action.
   *
   * @param doer the new doer
   */
  public void setDoer(String doer) {
    if (!doer.isEmpty()) {
      this.doer = doer;
    }
  }

}
