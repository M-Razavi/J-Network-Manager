/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Aug 12, 2015-11:54:36 AM Using JRE 1.8.0
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.util.Vector;

import net.mahdirazavi.java.toolkit.io.Loggers;

// TODO: Auto-generated Javadoc
/**
 * The Class NetServerEvents which all event that my accrues in network connections.
 */
public class NetServerEvents {

  /**
   * The Enum ServerEventTypes.
   */
  enum ServerEventTypes {

    /** The on client connect. */
    onClientConnect,
    /** The on error. */
    onError
  }

  /** The changed on client connect. */
  private boolean changedOnClientConnect = false;

  /** The changed on error. */
  private boolean changedOnError = false;

  /** The observers clien connect. */
  private Vector<NetServerEventsListener> observersClienConnect;

  /** The observers error. */
  private Vector<NetServerEventsListener> observersError;

  /** The info. */
  protected String info;

  /** The sender of the action. */
  protected Object sender;
  
  /** The lock common. */
  private Object lockCommon;

  /**
   * Construct an Observable with zero Observers.
   *
   * @param info the action doer
   */

  public NetServerEvents(String info) {
    observersClienConnect = new Vector<NetServerEventsListener>();
    observersError = new Vector<NetServerEventsListener>();

    this.info = info;
    this.sender = null;
    
    lockCommon= new Object();
  }

  /**
   * Adds an observer to the set of observers for this object, provided that it is not the same as
   * some observer already in the set. The order in which notifications will be delivered to
   * multiple observers is not specified. See the class comment.
   * 
   * @param o an observer to be added.
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  private synchronized void addObserver(NetServerEventsListener o, ServerEventTypes eventTypes)
      throws Exception {
    if (o == null)
      throw new NullPointerException();
    switch (eventTypes) {
      case onClientConnect:
        if (!observersClienConnect.contains(o)) {
          observersClienConnect.addElement(o);
        }
        break;
      case onError:
        if (!observersError.contains(o)) {
          observersError.addElement(o);
        }
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * Deletes an observer from the set of observers of this object. Passing <CODE>null</CODE> to this
   * method will have no effect.
   * 
   * @param o the observer to be deleted.
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  public synchronized void deleteObserver(NetServerEventsListener o, ServerEventTypes eventTypes)
      throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        observersClienConnect.removeElement(o);
        break;
      case onError:
        observersError.removeElement(o);
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * If this object has changed, as indicated by the <code>hasChanged</code> method, then notify all
   * of its observers and then call the <code>clearChanged</code> method to indicate that this
   * object has no longer changed.
   * <p>
   * Each observer has its <code>update</code> method called with two arguments: this observable
   * object and <code>null</code>. In other words, this method is equivalent to: <blockquote><tt>
   * notifyObservers(null)</tt></blockquote>
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   * @see java.util.Observable#clearChanged()
   * @see java.util.Observable#hasChanged()
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */

  private void notifyObservers(ServerEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        notifyObserversClientConnect(null);
        break;

      case onError:
        notifyObserversError(null);
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * If this object has changed, as indicated by the <code>hasChanged</code> method, then notify all
   * of its observers and then call the <code>clearChanged</code> method to indicate that this
   * object has no longer changed.
   * <p>
   * Each observer has its <code>update</code> method called with two arguments: this observable
   * object and the <code>arg</code> argument.
   * 
   * @param arg any object.
   * @throws Exception the exception
   * @see java.util.Observable#clearChanged()
   * @see java.util.Observable#hasChanged()
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  private void notifyObserversClientConnect(Object arg) throws Exception {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedOnClientConnect)
        return;
      arrLocal = observersClienConnect.toArray();
      clearChanged(ServerEventTypes.onClientConnect);
      
      if (observersClienConnect.size() < 1) {
        Loggers.Network.warn("No NetServerEventsListener was added for ClientConnect.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetServerEventsListener) arrLocal[i]).onClientConnect(this.sender, arg);
  }

  /**
   * Notify observers error.
   * 
   * @param arg the arg
   * @throws Exception the exception
   */
  private void notifyObserversError(Object arg) throws Exception {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedOnError)
        return;
      arrLocal = observersError.toArray();
      clearChanged(ServerEventTypes.onError);
      
      if (observersError.size() < 1) {
        Loggers.Network.warn("No NetServerEventsListener was added for Error.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetServerEventsListener) arrLocal[i]).onError(this.sender, arg);
  }

  /**
   * Clears the observer list so that this object no longer has any observers.
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  public synchronized void deleteObservers(ServerEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        observersClienConnect.removeAllElements();
        break;

      case onError:
        observersError.removeAllElements();
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * Marks this <tt>Observable</tt> object as having been changed; the <tt>hasChanged</tt> method
   * will now return <tt>true</tt>.
   * 
   * @param eventTypes the new changed
   * @throws Exception the exception
   */
  protected synchronized void setChanged(ServerEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        changedOnClientConnect = true;
        break;

      case onError:
        changedOnError = true;
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * Indicates that this object has no longer changed, or that it has already notified all of its
   * observers of its most recent change, so that the <tt>hasChanged</tt> method will now return
   * <tt>false</tt>. This method is called automatically by the <code>notifyObservers</code>
   * methods.
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   * @see java.util.Observable#notifyObservers()
   * @see java.util.Observable#notifyObservers(java.lang.Object)
   */
  protected synchronized void clearChanged(ServerEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        changedOnClientConnect = false;
        break;

      case onError:
        changedOnError = false;
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * Tests if this object has changed.
   * 
   * @param eventTypes the event types
   * @return <code>true</code> if and only if the <code>setChanged</code> method has been called
   *         more recently than the <code>clearChanged</code> method on this object;
   *         <code>false</code> otherwise.
   * @throws Exception the exception
   * @see java.util.Observable#clearChanged()
   * @see java.util.Observable#setChanged()
   */
  public synchronized boolean hasChanged(ServerEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onClientConnect:
        return changedOnClientConnect;

      case onError:
        return changedOnError;

      default:
        throw new Exception("Event type is not implemented.");
    }

  }

  // /**
  // * Returns the number of observers of this <tt>Observable</tt> object.
  // *
  // * @return the number of observers of this object.
  // */
  // public synchronized int countObservers() {
  // return observersClienConnect.size();
  // }

  /**
   * Adds the on client connect.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnClientConnect(NetServerEventsListener listener) throws Exception {
    addObserver(listener, ServerEventTypes.onClientConnect);
  }

  /**
   * Adds the on error.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnError(NetServerEventsListener listener) throws Exception {
    addObserver(listener, ServerEventTypes.onError);
  }

  /**
   * Fire an event.
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  private void fire(ServerEventTypes eventTypes) throws Exception {
    setChanged(eventTypes);
    notifyObservers(eventTypes);
  }

  /**
   * Fire an event with data.
   * 
   * @param eventData the event data
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  private void fire(Object eventData, ServerEventTypes eventTypes) throws Exception {
    setChanged(eventTypes);
    switch (eventTypes) {
      case onClientConnect:
        notifyObserversClientConnect(eventData);
        break;
      case onError:
        notifyObserversError(eventData);
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }

  /**
   * On client connect fire.
   */
  public void onClientConnectFire() {
    try {
      fire(ServerEventTypes.onClientConnect);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On client connect fire.
   * 
   * @param eventData the event data
   */
  public void onClientConnectFire(Object eventData) {
    try {
      fire(eventData, ServerEventTypes.onClientConnect);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On error fire.
   */
  public void onErrorFire() {
    try {
      fire(ServerEventTypes.onError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On error fire.
   * 
   * @param eventData the event data
   */
  public void onErrorFire(Object eventData) {
    try {
      fire(eventData, ServerEventTypes.onError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Gets the info.
   *
   * @return the info
   */
  public String getInfo() {
    synchronized (lockCommon) {
      return info;
    }
  }

  /**
   * Sets the info.
   *
   * @param info the new info
   */
  public void setInfo(String info) {
    synchronized (lockCommon) {
      if (!info.isEmpty()) {
        this.info = info;
      }
    }
  }
  
  /**
   * Gets the sender.
   *
   * @return the sender
   */
  public Object getSender() {
    synchronized (lockCommon) {
      return this.sender;
    }
  }


  /**
   * Sets the sender.
   *
   * @param sender the new sender
   */
  public void setSender(Object sender) {
    synchronized (lockCommon) {
      if (sender != null) {
        this.sender = sender;
      }
    }
  }
}
