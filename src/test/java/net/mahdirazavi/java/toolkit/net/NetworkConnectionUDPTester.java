/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jun 29, 2015-11:28:57 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */

package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.UnknownHostException;

import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * The Class NetworkConnectionTCPClientTester.
 */
public class NetworkConnectionUDPTester implements NetClientEventsListener {

  /** The manager tcp client. */
  IOConnection clientUDP;
  private String srcIpAddress2;
  private String srcPortNUmber2;
  private String dstIpAddress2;
  private String dstPortNumber2;

  /**
   * Instantiates a new network manager tcp client tester.
   * 
   * @param srcIpAddress the src ip address
   * @param srcPortNUmber the src port n umber
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   */
  public NetworkConnectionUDPTester(String srcIpAddress, String srcPortNUmber, String dstIpAddress,
      String dstPortNumber) {
    srcIpAddress2 = srcIpAddress;
    srcPortNUmber2 = srcPortNUmber;
    dstIpAddress2 = dstIpAddress;
    dstPortNumber2 = dstPortNumber;
    System.out.println("NetworkConnectionUDP Started with : " + srcIpAddress2 + ":"
        + srcPortNUmber2 + " " + dstIpAddress2 + ":" + dstPortNumber2);
    try {
      clientUDP =
          new NetworkConnectionUDP(srcIpAddress2, srcPortNUmber2, dstIpAddress2, dstPortNumber2,
              NetworkConnectionType.Server);
    } catch (NumberFormatException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (UnknownHostException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    try {
      clientUDP.addOnReceive(this);
      clientUDP.addOnReceiveError(this);
      clientUDP.addOnSend(this);
      clientUDP.addOnSendError(this);

      clientUDP.initialize();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }

  /**
   * Test send receive.
   * 
   * @param testMessage the test message
   */
  public void testSendReceive(String testMessage) {
    Thread networkThread = new Thread(clientUDP);
    networkThread.start();

    try {
    clientUDP.send(testMessage.getBytes());

      networkThread.join();
      clientUDP.closeIO();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
    NetData netData = null;
    byte[] receviedData = null;
    if (data instanceof NetData) {
      netData = (NetData) data;
    } else {
      receviedData = (byte[]) data;
      // netData = new NetData(receviedData, 0, receviedData.length, new NetNode(name, IPAddress,
      // port), this.source);
    }
    @SuppressWarnings("unused")
    String doer = sender.toString();
    if (receviedData != null && receviedData.length > 0) {
      System.out.println("Data received from '" + dstIpAddress2 + "'  and sent back.");
      try {
        clientUDP.send(receviedData);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if(netData != null)
    {
      System.out.println("Data received from '" + netData.getSource() + "'  and sent back.");
      try {
        clientUDP.send(netData.getData());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
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
    System.out.println("error receive:" + ((Exception) data).getMessage());
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
    System.out.println("Data sent from '" + doer + "' length=" + (int) data);

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onSendError(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSendError(Object sender, Object data) {
    System.out.println("error send:" + ((Exception) data).getMessage());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onConnect(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onConnect(Object sender, Object data) {

    System.out.println("I'm connected.");

  }

  public static void main(String[] args) {
    String ipSrc, portSrc, ipDst, portDst;
    ipSrc = "192.168.7.22";
    portSrc = "5555";
    ipDst = "192.168.7.22";
    portDst = "7777";
    // System.out.println("srcIpAddress srcPortNUmber dstIpAddress dstPortNumber");
    // if (args.length < 4) {
    // System.err.println("Invalid argument count.\n Application will exit.");
    // }

    // Test TCP Server
    // NetworkManagerTCPServerTester managerTCPServerTester =
    // new NetworkManagerTCPServerTester(args[0].toString(), args[1].toString(),
    // args[2].toString(), args[3].toString());
    // managerTCPServerTester.testSendReceive();

    System.out.println("Client local address is" + ipSrc + ":" + portSrc + "Client connect to "
        + ipDst + ":" + portDst);

    NetworkConnectionUDPTester clientTester =
        new NetworkConnectionUDPTester(ipSrc, portSrc, ipDst, portDst);

    clientTester.testSendReceive("Im client one");

    // new NetworkManagerTCPClientMultiTester(args[0].toString(), args[1].toString(),
    // args[2].toString(),
    // args[3].toString());

  }
}
