/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Sep 1, 2015-11:45:08 AM Using JRE 1.8.0
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */

package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.mahdirazavi.java.toolkit.io.IOConnection;
import net.mahdirazavi.java.toolkit.io.Loggers;


/**
 * The Class NetConnectionServerQueue extends {@link NetConnection} and uses two {@link BlockingQueue} as
 * input/output way and works as link between server and network layer.
 */
public class NetConnectionServerQueue extends NetConnection implements NetServerEventsListener,
    NetClientEventsListener, Runnable {

  /** The clients. */
  ConcurrentHashMap<NetNode, IOConnection> clients;

  /** The queue receive. */
  private BlockingQueue<NetData> queueReceive;

  /** The queue send. */
  private BlockingQueue<NetData> queueSend;

  /** The sender thread. */
  private Thread senderThread;

  private Thread receiverThread;

  ExecutorService threadPoolClients;

  /** The reconnect thread. */
  // private Thread reconnectThread;

  private boolean isCloseRequested;

  // private AtomicBoolean isConnected;

  // private TryReconnect tryReconnect;

  Object lockReconnect = new Object();

  int maxClient;

  private static AtomicInteger clientCounter = new AtomicInteger();



  /**
   * Instantiates a new net connection server queue.
   * 
   * @param networkIO the network IO
   * @param name the name
   * @param src the src
   * @param dst the dst
   */
  public NetConnectionServerQueue(IOConnection networkIO, String name, NetNode src, NetNode dst) {
    this(networkIO, name, src, dst, new LinkedBlockingQueue<NetData>(), 1);
  }

  /**
   * Instantiates a new net connection server queue.
   * 
   * @param networkIO the network io
   * @param name the name
   * @param src the src
   * @param dst the dst
   * @param queueReceive the queue receive
   * @param i
   */
  public NetConnectionServerQueue(IOConnection networkIO, String name, NetNode src, NetNode dst,
      BlockingQueue<NetData> queueReceive, int maxClient) {
    super(networkIO, name, src, dst);

    this.isCloseRequested = false;

    clients = new ConcurrentHashMap<NetNode, IOConnection>();

    queueSend = new LinkedBlockingQueue<>();
    if (queueReceive == null) {
      queueReceive = new LinkedBlockingQueue<>();
    }
    this.queueReceive = queueReceive;

    // tryReconnect = new TryReconnect();

    senderThread = new Thread(this, "Server-NetSender");
    receiverThread = new Thread(this.networkIO, "NetServer-Accepter");
    // reconnectThread = new Thread(tryReconnect, "NetReconnect");

    this.maxClient = maxClient;
    threadPoolClients = Executors.newFixedThreadPool(this.maxClient);

    setHandlerforEvents(networkIO);


    startThread();

    Loggers.Network.trace("Create NetConnectionServerQueue on " + src + " maxClients=" + maxClient);
  }

  /**
   * @param networkIO
   */
  private void setHandlerforEvents(IOConnection networkIO) {
    try {
      networkIO.addOnClientConnect(this);
      networkIO.addOnError(this);
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
   * @see net.mahdirazavi.java.toolkit.net.NetConnection#Receive()
   */
  @Override
  public NetData Receive() throws InterruptedException {
    return queueReceive.take();
  }


  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetConnection#Send(net.mahdirazavi.java.toolkit.net.NetData)
   */
  @Override
  public void Send(NetData data) throws Exception {
    if (data == null || data.getDestination() == null) {
      throw new Exception("Data or destination could not be null in send method.");
    } else {
      Loggers.Network.trace("Put data in queueSend. destination=" + data.getDestination());
      queueSend.put(data);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetConnection#close()
   */
  @Override
  public void close() {
    // close all clients
    // this action has done in NetworkConnectionTcpServer.closeIO()
    // for (IOConnection connection : clients.values()) {
    // try {
    // connection.closeIO();
    // } catch (IOException e) {
    // Loggers.Network.error("Error in close client connection", e);
    // }
    // }

    this.isCloseRequested = true;

    for (NetNode nod : clients.keySet()) {
      closeClient(clients.get(nod));
    }

    threadPoolClients.shutdownNow();


    try {
      this.networkIO.closeIO();
    } catch (IOException e) {
      Loggers.Network.error("Error in close connection", e);
    }

    this.senderThread.interrupt();
    this.receiverThread.interrupt();

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetServerEventsListener#onClientConnect(net.mahdirazavi.java.toolkit.net.NetServerEvents,
   * java.lang.Object)
   */
  @Override
  public void onClientConnect(Object sender, Object data) {
    IOConnection client = (IOConnection) data;
    clients.put(client.getRemoteNode(), client);
    Loggers.Network.trace("client connected." + client.getRemoteNode());

    try {
      client.addOnReceive(this);
      client.addOnReceiveError(this);
      client.addOnSend(this);
      client.addOnSendError(this);
    } catch (Exception e) {
      Loggers.Network.error("Error in handle receive event", e);
    }

    threadPoolClients.execute(client);

    NetData netData = new NetData(null, 0, 0, client.getRemoteNode(), client.getLocalNode());
    try {
      queueReceive.put((NetData) netData);
    } catch (InterruptedException e) {
      Loggers.Network.error("Error in client connect", e);
    }


  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetServerEventsListener#onError(net.mahdirazavi.java.toolkit.net.NetServerEvents,
   * java.lang.Object)
   */
  @Override
  public void onError(Object sender, Object data) {
    IOConnection client = (IOConnection) sender;
    Exception e = (Exception) data;
    Loggers.Network.error("Error in listener", e);

    reConnect();

    try {
      queueReceive.put(new NetData(client.getRemoteNode(), client.getLocalNode(), true, e));
    } catch (InterruptedException e1) {
      Loggers.Network.error("Error in sending error to upper layer.", e1);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onReceive(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onReceive(Object sender, Object data) {
    IOConnection client = (IOConnection) sender;
    byte[] bData = (byte[]) data;
    NetData netData =
        new NetData(bData, 0, bData.length, client.getRemoteNode(), client.getLocalNode());
    Loggers.Network.trace("Data received from client:src=" + client.getRemoteNode() + " dst="
        + client.getLocalNode());

    try {
      queueReceive.put((NetData) netData);
    } catch (InterruptedException e) {
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
    IOConnection client = (IOConnection) sender;
    Exception e = (Exception) data;
    Loggers.Network.error("Error in receive from client", e);
    try {
      queueReceive.put(new NetData(client.getRemoteNode(), client.getLocalNode(), true, e));
    } catch (InterruptedException e1) {
      Loggers.Network.error("Error in sending error to upper layer.", e1);
    }

    if (this.maxClient == 1) {
      IOConnection connection = (IOConnection) sender;
      closeClient(connection);
    }
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
    IOConnection client = (IOConnection) sender;
    Exception e = (Exception) data;
    Loggers.Network.error("Error in send to client", e);
    try {
      queueReceive.put(new NetData(client.getRemoteNode(), client.getLocalNode(), true, e));
    } catch (InterruptedException e1) {
      Loggers.Network.error("Error in sending error to upper layer.", e1);
    }
    if (this.maxClient == 1) {
      IOConnection connection = (IOConnection) sender;
      closeClient(connection);
    }
  }

  /**
   * Close client connection
   * 
   * @param connection
   */
  private void closeClient(IOConnection connection) {
    try {
      Loggers.Network.trace("Close client and remove from map. client="
          + connection.getRemoteNode());
      clients.remove(connection.getRemoteNode());
      connection.closeIO();
      clientCounter.decrementAndGet();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onConnect(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onConnect(Object sender, Object data) {
    // NOP

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // TODO send all data which added in queueSend to desired destination(destination specified in
    // dest filed of net data).



    while (!Thread.currentThread().isInterrupted()) {

      try {
        NetData netData = queueSend.take();

        Loggers.Network.trace("netData isNull=" + (netData == null) + " netData.getDestination() isNull="
            + (netData.getDestination() == null));


        IOConnection connection = clients.get(netData.getDestination());
        if (connection != null) {
          connection.send(netData.getData());
        } else {
          Loggers.Network.error("Data not sent because destination not found:"
              + netData.getDestination());
        }

      } catch (Exception e) {
        Loggers.Network.error("Error in send data", e);
      }
    }

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
        Loggers.Network.trace("TryReconnect class initialize.");


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
