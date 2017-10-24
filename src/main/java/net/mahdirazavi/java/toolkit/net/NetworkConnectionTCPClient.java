/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jan 17, 2015-4:05:21 PM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import net.mahdirazavi.java.toolkit.io.Loggers;
import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * The Class NetworkConnectionTCPClient, represent a TCP network connection as client.
 */
public class NetworkConnectionTCPClient extends IOConnection {

  /** The async thread. */
  protected Thread asyncThread = null;

  /** The source IP address. */
  protected Inet4Address localIpAddress = null;

  /** The destination IP address. */
  protected Inet4Address dstIpAddress = null;

  /** The source port number. */
  protected int localPortNumber = 0;

  /** The destination port number. */
  protected int dstPortNumber = 0;

  /** The socket. */
  protected Socket socket = null;

  /** The output stream. */
  protected OutputStream outputStream;

  /** The input stream. */
  protected InputStream inputStream;


  /** The receive data. */
  protected byte[] receiveData = new byte[1024];

  private Object receiveLock = new Object();

  protected boolean isClosed;

  /**
   * Instantiates a new network manager tcp client.
   * 
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionTCPClient(String localIpAddress, String localPortNumber,
      String dstIpAddress, String dstPortNumber) throws NumberFormatException, UnknownHostException {
    this(localIpAddress, localPortNumber, dstIpAddress, dstPortNumber, "NetworkConnectionTCPClient");
  }

  /**
   * Instantiates a new network manager tcp client.
   * 
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionTCPClient(String dstIpAddress, String dstPortNumber)
      throws NumberFormatException, UnknownHostException {
    this(null, "0", dstIpAddress, dstPortNumber, "NetworkConnectionTCPClient");
  }


  /**
   * Instantiates a new network manager tcp client.
   * 
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param actionDoer the action doer
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionTCPClient(String dstIpAddress, String dstPortNumber, String actionDoer)
      throws NumberFormatException, UnknownHostException {
    this(null, 0, (Inet4Address) Inet4Address.getByName(dstIpAddress), Integer
        .parseInt(dstPortNumber), actionDoer);
  }


  /**
   * Instantiates a new network connection tcp client.
   * 
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param info the action doer
   * @throws UnknownHostException
   * @throws NumberFormatException
   */
  public NetworkConnectionTCPClient(String localIpAddress, String localPortNumber,
      String dstIpAddress, String dstPortNumber, String info) throws NumberFormatException,
      UnknownHostException {
    this((Inet4Address) Inet4Address.getByName(localIpAddress), Integer.parseInt(localPortNumber),
        (Inet4Address) Inet4Address.getByName(dstIpAddress), Integer.parseInt(dstPortNumber), info);
  }


  /**
   * Instantiates a new network connection tcp client.
   * 
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param actionDoer the action doer
   */
  public NetworkConnectionTCPClient(InetAddress dstIpAddress, int dstPortNumber, String actionDoer) {
    this(null, 0, dstIpAddress, dstPortNumber, actionDoer);
  }


  /**
   * Instantiates a new network connection tcp client.
   * 
   * @param localIpAddress the local ip address
   * @param localPortNumber the local port number
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @param actionDoer the action doer
   */
  public NetworkConnectionTCPClient(InetAddress localIpAddress, int localPortNumber,
      InetAddress dstIpAddress, int dstPortNumber, String actionDoer) {
    super(actionDoer);

    this.localIpAddress = (Inet4Address) localIpAddress;
    this.dstIpAddress = (Inet4Address) dstIpAddress;

    this.localPortNumber = localPortNumber;
    this.dstPortNumber = dstPortNumber;

    this.isClosed = false;

    setSender(this);
  }

  /**
   * @param string
   */
  protected NetworkConnectionTCPClient(String actionDoer) {
    super(actionDoer);
  }

  /**
   * Sets the receive data.
   * 
   * @param receiveData the receive data
   * @param length the length
   */
  protected void setReceiveData(byte[] receiveData, int length) {
    synchronized (receiveLock) {
      this.receiveData = new byte[length];
      System.arraycopy(receiveData, 0, this.receiveData, 0, length);
      onReceiveFire(this.receiveData.clone());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#getReceiveData()
   */
  @Override
  public byte[] getReceiveData() {
    return this.receiveData.clone();
  }



  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getRemoteNode()
   */
  @Override
  public NetNode getRemoteNode() {
    return new NetNode("", this.dstIpAddress, this.dstPortNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOConnection#getLocalNode()
   */
  @Override
  public NetNode getLocalNode() {
    if (localPortNumber == 0) {
      return new NetNode("", this.localIpAddress, this.socket.getLocalPort());
    }
    return new NetNode("", this.localIpAddress, localPortNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#initialize()
   */
  @Override
  public synchronized void initialize() throws IOException {
    Loggers.Network.trace("Initialize TCP connection" + this.localIpAddress + ":"
        + this.localPortNumber + " to " + this.dstIpAddress + ":" + this.dstPortNumber);


    socket = new Socket();
    socket.setTcpNoDelay(false);
    socket.setKeepAlive(true);
    // Loggers.Network.info("client keepAlive = " + socket.getKeepAlive());
    socket.setReuseAddress(true);
    // socket.setSoTimeout(1000);

    if (localIpAddress != null) {
      // socket = new Socket(dstIpAddress, dstPortNumber, localIpAddress, localPortNumber);
      socket.bind(new InetSocketAddress(localIpAddress, localPortNumber));
    }
    socket.connect(new InetSocketAddress(dstIpAddress, dstPortNumber));


    this.isClosed = false;
    isError = false;

    outputStream = socket.getOutputStream();
    inputStream = socket.getInputStream();
    onConnectFire();
    byte [] arr = new byte[1];
    arr [0] = 0x03;
    send(arr);
    Loggers.Network.info("data for  project sended" );
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#closeIO()
   */
  @Override
  public void closeIO() throws IOException {
    Loggers.Network.trace("1-Close TCP connection to " + this.dstIpAddress + ":"
        + this.dstPortNumber);
    Loggers.Network.trace("TCP Socket isNull= " + (socket == null));
    if (socket != null) {
      socket.close();
    }
    this.isClosed = true;
    asyncThread.interrupt();
    Loggers.Network.trace("2-Close TCP connection to " + this.dstIpAddress + ":"
        + this.dstPortNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#send(byte[], int, int)
   */
  @Override
  public int send(byte[] data, int offset, int length) throws IOException {
    Loggers.Network.trace("NetTCPClient send to " + getRemoteNode() + " len=" + length);
    int result = -1;
    if (socket.isConnected() && !isClosed) {

      if (length > 0) {
        try {
          outputStream.write(data, offset, length);
          onSendFire(length);
          result = length;
        } catch (IOException e) {
          result = -1;
          Loggers.Network.error("Error in TCPClient send:",e);
          onSendErrorFire(e);
          throw e;
          
        }
      } else {
        Loggers.Network.error("Error in TCPClient send: datalength=0");
        result = 0;
      }
    } else {
      Loggers.Network.error("Error in TCPClient send: socket.isConnected()="+socket.isConnected() +" isClosed=" + isClosed);
      onSendErrorFire("Error in TCPClient send: TCPClient is not connected.");
      result = -1;
    }
    // Close must be call from who use network
    // if (result < 0) {
    // try {
    // this.closeIO();
    // } catch (IOException e) {
    // }
    // }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.io.IOManager#receive()
   */
  /**
   * This method is not useful in TCP connection, because data receive was implemented with
   * {@link NetClientEvents}. call this function does no operation.
   */
  @Override
  public void receive() {}

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    asyncThread = Thread.currentThread();
    byte[] dataReceive;
    // isError=false;
    dataReceive = new byte[1024];
    BufferedInputStream bfinput = null;

    try {
      bfinput = new BufferedInputStream(inputStream);
      while ((!Thread.currentThread().isInterrupted()) && (!isError)) {
        try {
          // int length = inputStream.read(dataReceive);
          try {
            socket.setSoTimeout(500);
            int length = bfinput.read(dataReceive);
            if (length >= 0) {
              setReceiveData(dataReceive, length);
            } else {
              throw new IOException("thread=" + asyncThread + " remote="
                  + socket.getRemoteSocketAddress() + " Cant read from network. result=" + length);
            }
          } catch (SocketTimeoutException e) {
            Thread.sleep(150);
          }
        } catch (Exception exp) {
          if (!(exp instanceof SocketTimeoutException)) {
            isError = true;
            exp.printStackTrace();
            Loggers.Network.trace("Error in TCPClient receive:",exp);
            onReceiveErrorFire(exp);
            Loggers.Network.trace("interrupting current thread");
            Thread.currentThread().interrupt();
            Loggers.Network.trace("interrupted.Threadname= " + Thread.currentThread().getName());
          }
        }

      }
      Loggers.Network.trace("Threadname= " + Thread.currentThread().getName()
          + " is interupted or isError =true");
    } finally {
      Loggers.Network.trace("TCPClient after receiveErrorFire()");
      // Close must be call from who use network
       try {
         bfinput.close();
         socket.close();//add by iman

         // // socket.close();
      // //this.closeIO();
       } catch (IOException e) {
       Loggers.Network.error("Error in close client", e);
       }
    }
  }
}
