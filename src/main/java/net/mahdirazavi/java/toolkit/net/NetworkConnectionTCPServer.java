/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 *
 *
 *
 * LastEdit Jan 17, 2015-4:05:21 PM Using JRE 1.7.0_55
 *
 *
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.5
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.mahdirazavi.java.toolkit.io.IOConnection;
import net.mahdirazavi.java.toolkit.io.Loggers;

/**
 * The Class NetworkConnectionTCPServer, represent a TCP network connection as server.
 */
public class NetworkConnectionTCPServer extends IOConnection {

  /** The async thread. */
  private Thread asyncThread = null;

  /** The source IP address. */
  private Inet4Address localIpAddress;

  /** The source port number. */
  private int localPortNumber;

  /** The maximum number of pending connections on the socket. */
  private int backLog;

  /** The maximum number of clients that server accept, and they can work concurrent. */
  private int maxClients;

  /** The server. */
  private ServerSocket server;

  /** The clients. */
  private java.util.concurrent.ConcurrentHashMap<NetNode, Socket> clients;

  /** The is close client on server close. */
  private boolean isCloseClientOnServerClose;


  /**
   * Instantiates a new network manager TCP server.
   *
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param backLog the maximum number of pending connections on the socket
   * @param maxClient the max client
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public NetworkConnectionTCPServer(String localIpAddress, String localPortNumber, int backLog,
      int maxClient) throws NumberFormatException, UnknownHostException {
    this(localIpAddress, localPortNumber, backLog, maxClient, "NetworkConnectionTCPServer");
  }

  /**
   * Instantiates a new network manager TCP server.
   *
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param backLog the maximum number of pending connections on the socket
   * @param maxClient the max client
   * @param actionDoer the action doer
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public NetworkConnectionTCPServer(String localIpAddress, String localPortNumber, int backLog,
      int maxClient, String actionDoer) throws NumberFormatException, UnknownHostException {
    this((Inet4Address) Inet4Address.getByName(localIpAddress), Integer.parseInt(localPortNumber),
        backLog, maxClient, actionDoer, true);
  }

  /**
   * Instantiates a new network manager TCP server.
   *
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param backLog the maximum number of pending connections on the socket
   * @param actionDoer the action doer
   * @param isCloseClientOnServerClose the is close client on server close
   */
  public NetworkConnectionTCPServer(InetAddress localIpAddress, int localPortNumber, int backLog,
      String actionDoer, boolean isCloseClientOnServerClose) {
    this((Inet4Address) localIpAddress, localPortNumber, backLog, 1, actionDoer, true);
  }

  /**
   * Instantiates a new network connection tcp server.
   *
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param backLog the maximum number of pending connections on the socket
   * @param maxClient the max client
   * @param actionDoer the action doer
   * @param isCloseClientOnServerClose the is close client on server close
   */
  public NetworkConnectionTCPServer(InetAddress localIpAddress, int localPortNumber, int backLog,
      int maxClient, String actionDoer, boolean isCloseClientOnServerClose) {
    super(actionDoer);

    this.localIpAddress = (Inet4Address) localIpAddress;
    this.localPortNumber = localPortNumber;
    this.backLog = backLog;
    this.maxClients = maxClient;
    this.clients = new ConcurrentHashMap<>();
    this.isCloseClientOnServerClose = isCloseClientOnServerClose;
  }


  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOManager#initialize()
   */
  @Override
  public void initialize() throws IOException {
    server = new ServerSocket(localPortNumber, backLog, localIpAddress);
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOManager#closeIO()
   */
  @Override
  public void closeIO() throws IOException {
    Loggers.Network
        .info("Close TCP  Server on " + this.localIpAddress + ":" + this.localPortNumber);

    try {
      throw new Exception(
          "Close TCP  Server on " + this.localIpAddress + ":" + this.localPortNumber);
    } catch (Exception e2) {
      Loggers.Network.info("stacktrace:", e2);
    }


    if (this.isCloseClientOnServerClose) {
      for (Socket socket : clients.values()) {
        try {
          socket.close();
        } catch (Exception e) {
          Loggers.Network.warn("Error in closing TCP listener", e);
        }


      }
    }

    server.close();
    asyncThread.interrupt();


  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    int clientCount = 0;
    asyncThread = Thread.currentThread();
    Socket client = null;
    TcpClient tcpClient;

    try {
      Loggers.Network.trace(this.getLocalNode() + " Wait for client...");
      while (!Thread.currentThread().isInterrupted()) {
        // TODO:use 'maxclient' number to control number of accept
        clientCount = checkConnectedClient();
        if ((clientCount == this.maxClients + 1)) {// (this.maxClients == 1) &&
          // Loggers.Network.warn("Maximum client count = " + this.maxClients);
          continue;
        }
        try {
          server.setSoTimeout(500);
          client = server.accept();
        } catch (SocketTimeoutException e) {
          try {
            client = null;
            Thread.sleep(1000);
          } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
          }
        }

        if (client == null) {
          continue;
        }

        Loggers.Network.trace(this.getLocalNode() + "accept connection");

        client.setTcpNoDelay(false);
        client.setKeepAlive(true);
        Loggers.Network.info("client keepAlive = " + client.getKeepAlive());

        tcpClient = new TcpClient(client);

        NetNode clientNode = new NetNode("client", client.getInetAddress(), client.getPort());

        if ((this.maxClients == 1) && (clients.size() > 0)) {
          Entry<NetNode, Socket> lastConnectCLient = clients.entrySet().iterator().next();
          if (lastConnectCLient.getValue() != null) {
            lastConnectCLient.getValue().close();
          }
        }
        clients.put(clientNode, client);
        Loggers.Network.trace("current number of connected =" + checkConnectedClient());
        Loggers.Network.trace("client connected " + client.getInetAddress().getHostAddress() + ":"
            + client.getPort());
        onClientConnectFire(tcpClient);
      }

    } catch (IOException e) {
      Loggers.Network.error("Exception in server accept clinets: " + e.getMessage() + " ClientSize="
          + clients.size());
      e.printStackTrace();
      onErrorFire(e);
    } finally {
    }
  }

  /**
   * @return
   */
  private int checkConnectedClient() {
    for (Socket client : clients.values()) {
      NetNode clientNode = new NetNode("client", client.getInetAddress(), client.getPort());
      if (client.isClosed()) {
        clients.remove(clientNode);
      }
    }
    // Loggers.Network.trace("Current size of client pool=" + clients.size());
    return clients.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getRemoteNode()
   */
  /**
   * This function couldn't return anything, cause of multiple client.
   *
   * @return the remote node
   */
  @Override
  public NetNode getRemoteNode() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getLocalNode()
   */
  @Override
  public NetNode getLocalNode() {
    return new NetNode("", this.localIpAddress, localPortNumber);
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getReceiveData()
   */
  /**
   * This function couldn't receive anything, cause of multiple client.
   *
   * @return the receive data
   */
  @Override
  public byte[] getReceiveData() {
    // NOP
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOManager#send(byte[], int, int)
   */
  /**
   * This function couldn't send anything, cause of multiple client.
   *
   * @param data the data
   * @param offset the offset
   * @param length the length
   * @return the int
   */
  @Override
  public int send(byte[] data, int offset, int length) {
    // NOP
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.io.IOManager#receive()
   */
  /**
   * This function couldn't receive anything, cause of multiple client.
   */
  @Override
  public void receive() {
    // NOP
  }

  /**
   * The Class TcpClient.
   */
  class TcpClient extends NetworkConnectionTCPClient {

    /**
     * Instantiates a new TCP client.
     *
     * @param socket the socket
     * @throws IOException Signals that an I/O exception has occurred.
     */
    TcpClient(Socket connectedSocket) throws IOException {
      super("client" + connectedSocket.getInetAddress());
      this.socket = connectedSocket;
      setSender(this);
      initialize();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.mahdirazavi.java.toolkit.io.IOConnection#initialize()
     */
    @Override
    public void initialize() throws IOException {
      socket.setTcpNoDelay(false);
      socket.setSoTimeout(1000);
      socket.setKeepAlive(true);

      outputStream = socket.getOutputStream();
      inputStream = socket.getInputStream();

      this.isClosed = false;

      this.localIpAddress = (Inet4Address) this.socket.getLocalAddress();
      this.localPortNumber = this.socket.getLocalPort();
      this.dstIpAddress = (Inet4Address) this.socket.getInetAddress();
      this.dstPortNumber = this.socket.getPort();

    }
  }
}
