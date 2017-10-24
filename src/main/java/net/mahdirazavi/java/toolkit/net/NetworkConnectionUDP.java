/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * NetworkConnectionUDP can act as UDP transmission agent.
 * 
 * LastEdit Jan 14, 2015-10:59:36 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import net.mahdirazavi.java.toolkit.io.Loggers;
import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * The Class NetworkConnectionUDP, represent a UDP network connection.
 */
public class NetworkConnectionUDP extends IOConnection {

  /** The is debug mode. */
  private boolean isDebugMode = false;

  /** The async thread. */
  private Thread asyncThread = null;

  /** The source IP address. */
  private Inet4Address localIpAddress;

  /** The destination IP address. */
  private Inet4Address dstIpAddress;

  /** The source port number. */
  private int localPortNumber;

  /** The destination port number. */
  private int dstPortNumber;

  /** The server. */
  private DatagramSocket server = null;

  /** The datagram packet. */
  private DatagramPacket datagramPacketSend;

  /** The datagram packet receive. */
  private DatagramPacket datagramPacketReceive;

  /** The receive data. */
  private byte[] receiveData = new byte[1024];

  /** The network manager type. */
  public NetworkConnectionType networkConnectionType;


  /**
   * Instantiates a new network manager UDP.
   * 
   * @param localIpAddress the source IP address
   * @param localPortNumber the source port number
   * @param dstIpAddress the destination IP address
   * @param dstPortNumber the destination port number
   * @param type the type
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionUDP(String localIpAddress, String localPortNumber, String dstIpAddress,
      String dstPortNumber, NetworkConnectionType type) throws NumberFormatException,
      UnknownHostException {
    this((Inet4Address) Inet4Address.getByName(localIpAddress), Integer.parseInt(localPortNumber),
        (Inet4Address) Inet4Address.getByName(dstIpAddress), Integer.parseInt(dstPortNumber), type,
        "NetworkConnectionUDP");
  }

  /**
   * Instantiates a new network connection udp.
   * 
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param type the type
   * @param actionDoer
   */
  public NetworkConnectionUDP(InetAddress localIpAddress, int localPortNumber,
      InetAddress dstIpAddress, int dstPortNumber, NetworkConnectionType type, String actionDoer) {
    super("NetworkConnectionUDP");
    setSender(this);

    this.localIpAddress = (Inet4Address) localIpAddress;
    this.dstIpAddress = (Inet4Address) dstIpAddress;

    this.localPortNumber = (localPortNumber);
    this.dstPortNumber = (dstPortNumber);
    this.networkConnectionType = type;
  }

  /**
   * Instantiates a new network manager udp.
   * 
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param type the type
   * @param actionDoer the action doer
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionUDP(String localIpAddress, String localPortNumber, String dstIpAddress,
      String dstPortNumber, NetworkConnectionType type, String actionDoer)
      throws NumberFormatException, UnknownHostException {
    this((Inet4Address) Inet4Address.getByName(localIpAddress), Integer.parseInt(localPortNumber),
        (Inet4Address) Inet4Address.getByName(dstIpAddress), Integer.parseInt(dstPortNumber), type,
        actionDoer);
  }

  /**
   * Instantiates a new network manager UDP as sender or receiver.
   * 
   * @param ipAddress the IP address for local or destination
   * @param portNumber the port number for local or destination
   * @param networkConnectionType the network connection type
   */
  public NetworkConnectionUDP(String ipAddress, String portNumber,
      NetworkConnectionType networkConnectionType) {
    this(ipAddress, portNumber, networkConnectionType, "NetworkConnectionUDP");
  }

  /**
   * Instantiates a new network manager udp.
   * 
   * @param ipAddress the ip address
   * @param portNumber the port number
   * @param networkManagerType the network manager type
   * @param actionDoer the action doer
   */
  public NetworkConnectionUDP(String ipAddress, String portNumber,
      NetworkConnectionType networkManagerType, String actionDoer) {
    super(actionDoer);
    setSender(this);
    this.networkConnectionType = networkManagerType;

    if (networkManagerType == NetworkConnectionType.Client) {
      try {
        this.dstIpAddress = (Inet4Address) Inet4Address.getByName(ipAddress);
        this.dstPortNumber = Integer.parseInt(portNumber);
      } catch (UnknownHostException e) {
        Loggers.Network.error("Destination IP address is unknown", e);
        // e.printStackTrace();
      }
    } else if (networkManagerType == NetworkConnectionType.Server) {
      try {
        this.localIpAddress = (Inet4Address) Inet4Address.getByName(ipAddress);
      } catch (UnknownHostException e) {

        Loggers.Network.error("Source IP address is unknown", e);
        // e.printStackTrace();
      }
      this.localPortNumber = Integer.parseInt(portNumber);
    } else {
      // TODO:check for both senderreceiver
    }
  }


  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#getReceiveData()
   */
  public byte[] getReceiveData() {
    return receiveData.clone();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getRemoteNode()
   */
  @Override
  public NetNode getRemoteNode() {
    return new NetNode("", this.dstIpAddress, dstPortNumber);
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

  /**
   * Sets the receive data.
   * 
   * @param receiveData the receive data
   * @param length the length
   */
  private void setReceiveData(byte[] receiveData, int length) {
    this.receiveData = new byte[length];
    System.arraycopy(receiveData, 0, this.receiveData, 0, length);

    onReceiveFire(this.receiveData);
  }

  /**
   * Sets the receive data. Create a instance of NetData and fill it.
   * 
   * @param data the data
   * @param offset the offset
   * @param length the length
   * @param source the source
   * @param destination the destination
   */
  private void setReceiveData(byte[] data, int offset, int length, NetNode source,
      NetNode destination) {
    this.receiveData = new byte[length];
    System.arraycopy(data, offset, this.receiveData, 0, length);
    NetData netData = new NetData(this.receiveData, 0, length, source, destination);
    onReceiveFire(netData);
  }



  /**
   * Instantiates a new network manager UDP.
   * 
   * @throws SocketException the socket exception
   */
  // public NetworkConnectionUDP(Inet4Address srcIpAddress, int srcPortNUmber, Inet4Address
  // ,
  // int dstPortNumber, int backlog) {
  // this.localIpAddress = srcIpAddress;
  // this.localPortNumber = srcPortNUmber;
  // this.dstIpAddress = dstIpAddress;
  // this.dstPortNumber = dstPortNumber;
  // // this.backlog = backlog;
  // }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#initialize()
   */
  @Override
  public void initialize() throws SocketException {
    Loggers.Network.trace("Initialize UDP connection" + this.localIpAddress + ":"
        + this.localPortNumber + " to " + this.dstIpAddress + ":" + this.dstPortNumber);

    System.out.println("Initialize UDP connection" + this.localIpAddress + ":"
        + this.localPortNumber + " to " + this.dstIpAddress + ":" + this.dstPortNumber);
    if (this.dstPortNumber == 0) {
      throw new SocketException("Destination port can not set with 0.");
    }

    if (networkConnectionType == NetworkConnectionType.Client) {
      server = new DatagramSocket();
    } else {
      server = new DatagramSocket(null);
      server.setReuseAddress(true);
      server.bind(new InetSocketAddress(localIpAddress, localPortNumber));
    }
    server.setTrafficClass(0x10);// IPT_LOWDELAY

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#send(byte[], int, int)
   */
  @Override
  public int send(byte[] data, int offset, int length) {
    datagramPacketSend = new DatagramPacket(data, offset, length, dstIpAddress, dstPortNumber);
    try {
      server.send(datagramPacketSend);
      onSendFire(data.length);

      Loggers.Network.trace("++++++++Send data from " +localIpAddress+":"+localPortNumber+ " >>to>> " + dstIpAddress + ":" + dstPortNumber + " len="
          + data.length);

      if (isDebugMode) {
        Loggers.Network.trace("Send data to " + dstIpAddress + ":" + dstPortNumber + " len="
            + data.length);
      }
    } catch (IOException e) {

      onSendErrorFire(e);
      // e.printStackTrace();
    }
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#closeIO()
   */
  @Override
  public void closeIO() {
    asyncThread.interrupt();

    if (server != null) {
      server.close();
    }
    Loggers.Network
        .trace("Close UDP connection to " + this.dstIpAddress + ":" + this.dstPortNumber);

    System.out.println("Close UDP connection to " + this.dstIpAddress + ":" + this.dstPortNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    asyncThread = Thread.currentThread();

    receiveAsync();
  }

  /**
   * Receive asynchronism.
   */
  private void receiveAsync() {
    byte[] data = new byte[1024];
    datagramPacketReceive = new DatagramPacket(data, data.length);

    while (!Thread.currentThread().isInterrupted()) {
      try {
        if (server.isClosed())// !server.isConnected() ||
        {
          if (isDebugMode) {
            Loggers.Network.trace("UDP Connection is closed. local=" + this.localPortNumber + ":"
                + this.localIpAddress + " remote=" + this.localPortNumber + ":"
                + this.localIpAddress + " isConnected=" + server.isConnected() + " isClosed="
                + server.isClosed());
          }
          break;
        }

        // Waiting for receiving data.
        server.receive(datagramPacketReceive);
        setReceiveData(
            datagramPacketReceive.getData(),
            datagramPacketReceive.getOffset(),
            datagramPacketReceive.getLength(),
            new NetNode("Source", datagramPacketReceive.getAddress(), datagramPacketReceive
                .getPort()), new NetNode("Destination", this.localIpAddress, this.localPortNumber));

        Loggers.Network.trace("received Data Receiver: " + this.localIpAddress +":"+ this.localPortNumber + " Sender: " + datagramPacketReceive.getAddress() + ":"
            + datagramPacketReceive.getPort() + " len=: " + datagramPacketReceive.getLength());
        if (isDebugMode) {
        }

        // reset packet for reuse
        datagramPacketReceive.setLength(data.length);
      } catch (IOException e) {
        if (Thread.currentThread().isInterrupted()) {
          Loggers.Network.trace("Thread intrrupted and UDP socket closed.");
        } else {
          Loggers.Network.error("Error in receive data", e);
          onReceiveErrorFire(e);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#receive()
   */
  @Override
  public void receive() {
    byte[] data = new byte[512];
    datagramPacketReceive = new DatagramPacket(data, data.length);
    try {
      Loggers.Network.trace("Waiting for receiving data.");
      server.receive(datagramPacketReceive);
      Loggers.Network.trace("Data received.");
    } catch (IOException e) {
      // e.printStackTrace();
      onReceiveErrorFire(e);
    }
    setReceiveData(datagramPacketReceive.getData(), datagramPacketReceive.getLength());
  }
}
