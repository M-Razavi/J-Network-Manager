/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jul 27, 2015-9:32:23 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.util.Vector;

import net.mahdirazavi.java.toolkit.io.Loggers;

/**
 * The {@link ClientEventTypes} is a event provider for network events. Each class that need to
 * notify by network event, should implements {@link NetClientEventsListener} interface, and
 * register with some add event in {@link NetClientEvents} such as
 * {@link NetClientEvents#addOnSend(NetClientEventsListener)}. When any network event occurs, that
 * object's appropriate method is invoked.
 */
public class NetClientEvents extends NetServerEvents {

  /**
   * The Enum ClientEventTypes.
   */
  enum ClientEventTypes {

    /** The on send. */
    onSend,
    /** The on send error. */
    onSendError,
    /** The on receive. */
    onReceive,
    /** The on receive error. */
    onReceiveError,
    /** The on connect. */
    onConnect,
  };

  /** The changed send. */
  private boolean changedSend = false;

  /** The changed send error. */
  private boolean changedSendError = false;

  /** The changed receive. */
  private boolean changedReceive = false;

  /** The changed receive error. */
  private boolean changedReceiveError = false;

  /** The changed connect. */
  private boolean changedConnect = false;

  /** The observers send. */
  private Vector<NetClientEventsListener> observersSend;

  /** The observers send error. */
  private Vector<NetClientEventsListener> observersSendError;

  /** The observers receive. */
  private Vector<NetClientEventsListener> observersReceive;

  /** The observers receive error. */
  private Vector<NetClientEventsListener> observersReceiveError;

  /** The observers connect. */
  private Vector<NetClientEventsListener> observersConnect;

  /** The lock common. */
  private Object lockCommon;

  /** The lock send. */
  private Object lockSend;

  /** The lock send error. */
  private Object lockSendError;

  /** The lock receive. */
  private Object lockReceive;

  /** The lock receive error. */
  private Object lockReceiveError;

  /** The lock connect. */
  private Object lockConnect;

  /**
   * Construct an Observable with zero NetClientEventsListeners.
   * 
   * @param info the action doer
   */

  public NetClientEvents(String info) {
    super(info);
    observersSend = new Vector<NetClientEventsListener>();
    observersSendError = new Vector<NetClientEventsListener>();
    observersReceive = new Vector<NetClientEventsListener>();
    observersReceiveError = new Vector<NetClientEventsListener>();
    observersConnect = new Vector<NetClientEventsListener>();


    lockCommon = new Object();
    lockSend = new Object();
    lockSendError = new Object();
    lockReceive = new Object();
    lockReceiveError = new Object();
    lockConnect = new Object();
  }


  // public NetClientEvents(String eventDoer) {
  // onSend = new NetEvent(eventDoer);
  // onReceive = new NetEvent(eventDoer);
  // onSendError = new NetEvent(eventDoer);
  // onReceiveError = new NetEvent(eventDoer);
  // }

  /**
   * Adds an NetClientEventsListener to the set of NetClientEventsListeners for this object,
   * provided that it is not the same as some NetClientEventsListener already in the set. The order
   * in which notifications will be delivered to multiple NetClientEventsListeners is not specified.
   * See the class comment.
   * 
   * @param observer the observer
   * @param eventType the event type
   * @throws Exception the exception
   */
  private void addObserver(NetClientEventsListener observer, ClientEventTypes eventType)
      throws Exception {
    if (observer == null)
      throw new NullPointerException("observer is null.");

    switch (eventType) {
      case onSend:
        synchronized (lockSend) {
          if (!observersSend.contains(observer)) {
            observersSend.addElement(observer);
          }
        }
        break;
      case onSendError:
        synchronized (lockSendError) {
          if (!observersSendError.contains(observer)) {
            observersSendError.addElement(observer);
          }
        }
        break;
      case onReceive:
        synchronized (lockReceive) {
          if (!observersReceive.contains(observer)) {
            observersReceive.addElement(observer);
          }
        }
        break;
      case onReceiveError:
        synchronized (lockReceiveError) {
          if (!observersReceiveError.contains(observer)) {
            observersReceiveError.addElement(observer);
          }
        }

        break;
      case onConnect:
        synchronized (lockConnect) {
          if (!observersConnect.contains(observer)) {
            observersConnect.addElement(observer);
          }
        }
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }
  }


  /**
   * Deletes an NetClientEventsListener from the set of observers of this object. Passing
   * <CODE>null</CODE> to this method will have no effect.
   * 
   * @param observer the observer
   * @param eventType the event type
   * @throws Exception the exception
   */
  public void deleteObserver(NetClientEventsListener observer, ClientEventTypes eventType)
      throws Exception {
    switch (eventType) {
      case onSend:
        synchronized (lockSend) {
          observersSend.removeElement(observer);
        }
        break;
      case onSendError:
        synchronized (lockSendError) {
          observersSendError.removeElement(observer);
        }
        break;
      case onReceive:
        synchronized (lockReceive) {
          observersReceive.removeElement(observer);
        }
        break;
      case onReceiveError:
        synchronized (lockReceiveError) {
          observersReceiveError.removeElement(observer);
        }
        break;
      case onConnect:
        synchronized (lockConnect) {
          observersConnect.removeElement(observer);
        }
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
   * @param eventType the event type
   * @throws Exception the exception
   * @see java.util.Observable#clearChanged()
   * @see java.util.Observable#hasChanged()
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  private void notifyObservers(ClientEventTypes eventType) throws Exception {
    switch (eventType) {
      case onSend:
        notifyObserversSend(null);
        break;
      case onSendError:
        notifyObserversSendError(null);
        break;
      case onReceive:
        notifyObserversReceive(null);
        break;
      case onReceiveError:
        notifyObserversReceiveError(null);
        break;
      case onConnect:
        notifyObserversConnect(null);
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
   * @param eventTypes the event types
   * @throws Exception the exception
   * @see java.util.Observable#clearChanged()
   * @see java.util.Observable#hasChanged()
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  private void notifyObservers(Object arg, ClientEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onSend:
        notifyObserversSend(arg);
        break;
      case onSendError:
        notifyObserversSendError(arg);
        break;
      case onReceive:
        notifyObserversReceive(arg);
        break;
      case onReceiveError:
        notifyObserversReceiveError(arg);
        break;
      case onConnect:
        notifyObserversConnect(arg);
        break;

      default:
        throw new Exception("Event type is not implemented.");
    }

  }

  /**
   * Notify observers send.
   * 
   * @param arg the arg
   */
  private void notifyObserversSend(Object arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this.lockSend) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedSend)
        return;
      arrLocal = observersSend.toArray();
      try {
        clearChanged(ClientEventTypes.onSend);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      if(observersSend.size() <1)
      {
        Loggers.Network.warn("No NetClientEventsListener was added for Send.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetClientEventsListener) arrLocal[i]).onSend(this.sender, arg);
  }

  /**
   * Notify observers send error.
   * 
   * @param arg the arg
   */
  private void notifyObserversSendError(Object arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this.lockSendError) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedSendError)
        return;
      arrLocal = observersSendError.toArray();
      try {
        clearChanged(ClientEventTypes.onSendError);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      if(observersSendError.size() <1)
      {
        Loggers.Network.warn("No NetClientEventsListener was added for SendError.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetClientEventsListener) arrLocal[i]).onSendError(this.sender, arg);
  }

  /**
   * Notify observers receive.
   * 
   * @param arg the arg
   */
  private void notifyObserversReceive(Object arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this.lockReceive) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      
      if (!changedReceive)
        return;
      arrLocal = observersReceive.toArray();
      try {
        clearChanged(ClientEventTypes.onReceive);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (observersReceive.size() < 1) {
        Loggers.Network.warn("No NetClientEventsListener was added for Receieve.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetClientEventsListener) arrLocal[i]).onReceive(this.sender, arg);
  }

  /**
   * Notify observers receive error.
   * 
   * @param arg the arg
   */
  private void notifyObserversReceiveError(Object arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this.lockReceiveError) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedReceiveError)
        return;
      arrLocal = observersReceiveError.toArray();
      try {
        clearChanged(ClientEventTypes.onReceiveError);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (observersReceiveError.size() < 1) {
        Loggers.Network.warn("No NetClientEventsListener was added for ReceieveError.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetClientEventsListener) arrLocal[i]).onReceiveError(this.sender, arg);
  }

  /**
   * Notify observers connect.
   * 
   * @param arg the arg
   */
  private void notifyObserversConnect(Object arg) {
    /*
     * a temporary array buffer, used as a snapshot of the state of current Observers.
     */
    Object[] arrLocal;

    synchronized (this.lockConnect) {
      /*
       * We don't want the Observer doing callbacks into arbitrary code while holding its own
       * Monitor. The code where we extract each Observable from the Vector and store the state of
       * the Observer needs synchronization, but notifying observers does not (should not). The
       * worst result of any potential race-condition here is that: 1) a newly-added Observer will
       * miss a notification in progress 2) a recently unregistered Observer will be wrongly
       * notified when it doesn't care
       */
      if (!changedConnect)
        return;
      arrLocal = observersConnect.toArray();
      try {
        clearChanged(ClientEventTypes.onConnect);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (observersConnect.size() < 1) {
        Loggers.Network.warn("No NetClientEventsListener was added for Connect.");
      }
    }

    for (int i = arrLocal.length - 1; i >= 0; i--)
      ((NetClientEventsListener) arrLocal[i]).onConnect(this.sender, arg);
  }

  /**
   * Clears the observer list so that this object no longer has any observers.
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  public void deleteObservers(ClientEventTypes eventTypes) throws Exception {

    switch (eventTypes) {
      case onSend:
        synchronized (lockSend) {
          observersSend.removeAllElements();
        }
        break;
      case onSendError:
        synchronized (lockSendError) {
          observersSendError.removeAllElements();
        }
        break;
      case onReceive:
        synchronized (lockReceive) {
          observersReceive.removeAllElements();
        }
        break;
      case onReceiveError:
        synchronized (lockReceiveError) {
          observersReceiveError.removeAllElements();
        }
        break;
      case onConnect:
        synchronized (lockConnect) {
          observersConnect.removeAllElements();
        }
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
  protected void setChanged(ClientEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onSend:
        synchronized (lockSend) {
          changedSend = true;
        }
        break;
      case onSendError:
        synchronized (lockSendError) {
          changedSendError = true;
        }
        break;
      case onReceive:
        synchronized (lockReceive) {
          changedReceive = true;
        }
        break;
      case onReceiveError:
        synchronized (lockReceiveError) {
          changedReceiveError = true;
        }
        break;
      case onConnect:
        synchronized (lockConnect) {
          changedConnect = true;
        }
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
  protected void clearChanged(ClientEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onSend:
        synchronized (lockSend) {
          changedSend = false;
          break;
        }
      case onSendError:
        synchronized (lockSendError) {
          changedSendError = false;
        }
        break;
      case onReceive:
        synchronized (lockReceive) {
          changedReceive = false;
        }
        break;
      case onReceiveError:
        synchronized (lockReceiveError) {
          changedReceiveError = false;
        }
        break;
      case onConnect:
        synchronized (lockConnect) {
          changedConnect = false;
        }
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
  public boolean hasChanged(ClientEventTypes eventTypes) throws Exception {
    switch (eventTypes) {
      case onSend:
        synchronized (lockSend) {
          return changedSend;
        }
      case onSendError:
        synchronized (lockSendError) {
          return changedSendError;
        }
      case onReceive:
        synchronized (lockReceive) {
          return changedReceive;
        }
      case onReceiveError:
        synchronized (lockReceiveError) {
          return changedReceiveError;
        }
      case onConnect:
        synchronized (lockConnect) {
          return changedConnect;
        }

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
  // return observers.size();
  // }



  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetServerEvents#getInfo()
   */
  public String getInfo() {
    synchronized (lockCommon) {
      return info;
    }
  }


  public void setInfo(String info) {
    synchronized (lockCommon) {
      if (!info.isEmpty()) {
        this.info = info;
      }
    }
  }

  /**
   * Adds the on send.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnSend(NetClientEventsListener listener) throws Exception {
    addObserver(listener, ClientEventTypes.onSend);
  }

  /**
   * Adds the on receive.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnReceive(NetClientEventsListener listener) throws Exception {
    addObserver(listener, ClientEventTypes.onReceive);
  }

  /**
   * Adds the on send error.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnSendError(NetClientEventsListener listener) throws Exception {
    addObserver(listener, ClientEventTypes.onSendError);
  }

  /**
   * Adds the on receive error.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnReceiveError(NetClientEventsListener listener) throws Exception {
    addObserver(listener, ClientEventTypes.onReceiveError);
  }


  /**
   * Adds the on connect.
   * 
   * @param listener the listener
   * @throws Exception the exception
   */
  public void addOnConnect(NetClientEventsListener listener) throws Exception {
    addObserver(listener, ClientEventTypes.onConnect);
  }


  /**
   * Fire an event.
   * 
   * @param eventTypes the event types
   * @throws Exception the exception
   */
  private void fire(ClientEventTypes eventTypes) throws Exception {
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
  private void fire(Object eventData, ClientEventTypes eventTypes) throws Exception {
    setChanged(eventTypes);
    notifyObservers(eventData, eventTypes);
  }

  /**
   * On receive fire.
   */
  public void onReceiveFire() {
    try {
      fire(ClientEventTypes.onReceive);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On receive fire.
   * 
   * @param netData the net data
   */
  public void onReceiveFire(Object netData) {
    try {
      fire(netData, ClientEventTypes.onReceive);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On send fire.
   */
  public void onSendFire() {
    try {
      fire(ClientEventTypes.onSend);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On send fire.
   * 
   * @param sentCount the event data
   */
  public void onSendFire(Object sentCount) {
    try {
      fire(sentCount, ClientEventTypes.onSend);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On receive error fire.
   */
  public void onReceiveErrorFire() {
    try {
      fire(ClientEventTypes.onReceiveError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On receive error fire.
   * 
   * @param eventData the event data
   */
  public void onReceiveErrorFire(Object eventData) {
    try {
      fire(eventData, ClientEventTypes.onReceiveError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On send error fire.
   */
  public void onSendErrorFire() {
    try {
      fire(ClientEventTypes.onSendError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On send error fire.
   * 
   * @param eventData the event data
   */
  public void onSendErrorFire(Object eventData) {
    try {
      fire(eventData, ClientEventTypes.onSendError);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * On connect fire.
   */
  public void onConnectFire() {
    try {
      fire(ClientEventTypes.onConnect);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * On connect fire.
   * 
   * @param eventData the event data
   */
  public void onConnectFire(Object eventData) {
    try {
      fire(eventData, ClientEventTypes.onConnect);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
