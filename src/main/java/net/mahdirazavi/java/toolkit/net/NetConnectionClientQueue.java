/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jun 17, 2015-8:34:45 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.mahdirazavi.java.toolkit.io.IOConnection;
import net.mahdirazavi.java.toolkit.io.Loggers;

/**
 * The Class NetConnectionClientQueue extends {@link NetConnection} and uses two {@link BlockingQueue} as
 * input/output way and works as link between client and network layer.
 * 
 * @author Mahdi Razavi
 * @version 1.0
 * @created 11-May-2015 11:10:13 AM
 */
public final class NetConnectionClientQueue extends NetConnection implements
    NetClientEventsListener, Runnable {

  /** The queue receive. */
  private BlockingQueue<NetData> queueReceive;

  /** The queue send. */
  private BlockingQueue<NetData> queueSend;

  /** The sender thread. */
  private Thread senderThread;

  /** The receiver thread. */
  private Thread receiverThread;

  /** The reconnect thread. */
  // private Thread reconnectThread;

  private boolean isCloseRequested;

  // private AtomicBoolean isConnected;

  // private TryReconnect tryReconnect;

  Object lockReconnect = new Object();

  /**
   * Create Network connection and it's queues.
   * 
   * @param networkIO the network io
   * @param name the name
   * @param src the src
   * @param dst the dst
   */
  public NetConnectionClientQueue(IOConnection networkIO, String name, NetNode src, NetNode dst) {
    this(networkIO, name, src, dst, new LinkedBlockingQueue<NetData>());
  }

  public NetConnectionClientQueue(IOConnection networkIO, String name, NetNode src, NetNode dst,
      BlockingQueue<NetData> queueReceive) {
    super(networkIO, name, src, dst);

    this.isCloseRequested = false;
    // this.isConnected = new AtomicBoolean();

    Loggers.Network.trace("Init netConnection:" + src + " " + dst);

    setHandlerforEvents(networkIO);

    queueSend = new LinkedBlockingQueue<>();
    if (queueReceive == null) {
      queueReceive = new LinkedBlockingQueue<>();
    }
    this.queueReceive = queueReceive;
    // tryReconnect = new TryReconnect();

    senderThread = new Thread(this, "NetSender");
    receiverThread = new Thread(this.networkIO, "NetReceiever");
    // reconnectThread = new Thread(tryReconnect, "NetReconnect");

    startThread();
  }

  /**
   * @param networkIO
   */
  private void setHandlerforEvents(IOConnection networkIO) {
    try {
      networkIO.addOnReceive(this);
      networkIO.addOnSend(this);
      networkIO.addOnReceiveError(this);
      networkIO.addOnSendError(this);
    } catch (Exception e) {
      Loggers.Network.error("Error in set handler for event", e);
    }
  }

  /**
   * Start sender and receiver thread.
   */
  private void startThread() {
    senderThread.start();
    receiverThread.start();
    // reconnectThread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        if (this.isCloseRequested) {
          Loggers.Network.error("Error send data after close connection. "
              + networkIO.getLocalNode() + " to " + networkIO.getRemoteNode());
          break;
        }
        NetData netData = queueSend.take();
        networkIO.send(netData.getData());
      } catch (Exception e) {
        if (e instanceof InterruptedException && this.isCloseRequested) {
          // NOP
        } else
          Loggers.Network.error("Error in send data from " + networkIO.getLocalNode() + " to "
              + networkIO.getRemoteNode(), e);
      }
    }
  }

  /**
   * Receive data from receive queue.
   * 
   * @return the net data
   * @throws InterruptedException the interrupted exception
   */
  public NetData Receive() throws InterruptedException {
    return queueReceive.take();
  }

  /**
   * Add data to send queue.
   * 
   * @param data data
   * @throws InterruptedException the interrupted exception
   */
  public void Send(NetData data) throws InterruptedException {
    Loggers.Network.debug("net connection out in queue for send to" + networkIO.getRemoteNode());
    Loggers.Network.trace("net connection ThreadSend.state:" + senderThread.getState() + " alive:"
        + senderThread.isAlive());
    queueSend.put(data);
    Loggers.Network.trace("queue send size =" + queueSend.size() + " isEmpty="
        + queueSend.isEmpty());
  }


  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetConnection#close()
   */
  public void close() {
    Loggers.Network.trace("Closing netConnection " + this.source + " to " + this.destination);
    this.isCloseRequested = true;
    try {
      this.networkIO.closeIO();
    } catch (IOException e) {
      Loggers.Network.error("Error in close connection", e);
    }
    this.senderThread.interrupt();
    this.receiverThread.interrupt();
    // this.reconnectThread.interrupt();

    // try {
    // throw new Exception("test: get callstack");
    //
    // } catch (Exception e) {
    // Loggers.Network.error("Get call stack for close()", e);
    // }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onReceive(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onReceive(Object sender, Object data) {
    NetData netData;
    if (data instanceof NetData) {
      netData = (NetData) data;
    } else {
      byte[] bData = (byte[]) data;
      netData = new NetData(bData, 0, bData.length, this.destination, this.source);
    }
    try {
      queueReceive.put(netData);
    } catch (Exception e) {
      if (e instanceof InterruptedException && this.isCloseRequested) {
        // NOP
      } else
        Loggers.Network.error("Error in receive", e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onReceiveError(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onReceiveError(Object sender, Object data) {
    Exception e = (Exception) data;
    Loggers.Network.error("Error in receive", e);
    try {
      queueReceive.put(new NetData(this.destination, this.source, true, e));
    } catch (InterruptedException e1) {

      Loggers.Network.error("Error in sending error to upper layer.", e1);
    }

    reConnect();

    Loggers.Network.trace("receive-Reconnect() Complete");

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onSend(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSend(Object sender, Object data) {
    String doer = sender.toString();
    Loggers.Network.trace("sent from '" + doer + "' length=" + (int) data);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onSendError(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSendError(Object sender, Object data) {
    Exception e = (Exception) data;
    Loggers.Network.error("Error in send", e);
    try {
      queueReceive.put(new NetData(this.destination, this.source, true, e));
    } catch (InterruptedException e1) {

      Loggers.Network.error("Error in sending error to upper layer.", e1);
    }

    reConnect();

    // Loggers.Network.trace("send-Reconnect() Complete");


  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onConnect(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onConnect(Object sender, Object data) {
    Loggers.Network.trace("Client conncted.");
  }


  /**
   * Close and initialize network IO
   * 
   * @throws InterruptedException
   */
  private void reConnect() {
    synchronized (lockReconnect) {
      // lockReconnect.notify();
    }
  }


  @SuppressWarnings("unused")
  private class TryReconnect implements Runnable {
    int delayTime = 3000;

    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        Loggers.Network.trace("TryReconnect init.");


        // Wait until reconnect needed
        try {
          synchronized (lockReconnect) {
            lockReconnect.wait();
            Loggers.Network.trace("TryReconnect Run...");
          }
        } catch (InterruptedException e2) {
          Loggers.Network.error("Error on reconnect wait", e2);
        }

        // Wait between every try
        try {
          Thread.sleep(delayTime);
        } catch (InterruptedException e1) {
        }

        // if (isConnected.get())
        // return;
        // isConnected.set(false);
        while (!isCloseRequested) {
          try {
            networkIO.closeIO();
            networkIO.initialize();
            setHandlerforEvents(networkIO);
            receiverThread = new Thread(networkIO, "NetReceiever");
            receiverThread.start();
            Loggers.Network.trace("Reconnec successful.");
            break;
          } catch (IOException e) {
            Loggers.Network.error(e.getMessage());
          }
          try {
            Thread.sleep(delayTime);
          } catch (InterruptedException e) {
            Loggers.Network.error(e.getMessage());
          }
        }
        // isConnected.set(true);
      }
    }
  }
}
