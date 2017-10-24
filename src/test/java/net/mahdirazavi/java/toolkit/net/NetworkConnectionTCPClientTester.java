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
import java.util.concurrent.atomic.AtomicInteger;

import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * The Class NetworkConnectionTCPClientTester.
 */
public class NetworkConnectionTCPClientTester implements NetClientEventsListener {

  /** The manager tcp client. */
  IOConnection clientTCP;
  
  AtomicInteger errorCounter;


  /**
   * Instantiates a new network connection tcp client tester.
   * 
   * @param dstIpAddress the dst ip address
   * @param dstPortNumber the dst port number
   * @throws UnknownHostException 
   * @throws NumberFormatException 
   */
  public NetworkConnectionTCPClientTester(String dstIpAddress, String dstPortNumber) throws NumberFormatException, UnknownHostException {
    System.out.println("NetworkConnectionTCPClient Started with : " + ":" + " " + dstIpAddress
        + ":" + dstPortNumber);
    clientTCP = new NetworkConnectionTCPClient(dstIpAddress, dstPortNumber);

    try {
      clientTCP.addOnReceive(this);
      clientTCP.addOnReceiveError(this);
      clientTCP.addOnSend(this);
      clientTCP.addOnSendError(this);
      clientTCP.addOnConnect(this);


      clientTCP.initialize();
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
    errorCounter = new AtomicInteger();
    errorCounter.set(0);
    Thread networkThread = new Thread(clientTCP);
    networkThread.start();
    long number = 0;
    while (true) {
      if(errorCounter.get()>0)
      {
        try {
          clientTCP.initialize();
          errorCounter.set(0);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      // clientTCP.send(testMessage.getBytes());
      try {
      clientTCP.send(String.valueOf((number++)).getBytes());
        Thread.sleep(1000);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // try
    // {
    // networkThread.join();
    // clientTCP.closeIO();
    // }
    // catch (InterruptedException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // catch (IOException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
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
    String doer = sender.toString();
    byte[] receviedData = (byte[]) data;
    if (receviedData != null && receviedData.length > 0) {
      System.out.println("Data received from '" + doer + "'  and sent back.");
      String.valueOf(receviedData);
      String responseMessage = "res:" + String.valueOf(receviedData);
      try {
      clientTCP.send(responseMessage.getBytes());
        System.out.println("wait for 3 sec.");
        Thread.sleep(500);
      } catch (Exception e) {
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
    errorCounter.incrementAndGet();

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.ObjectListener#onSend(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSend(Object sender, Object data) {
    String doer = sender.toString();
    System.out.println("sent from '" + doer + "' length=" + (int) data);
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
    errorCounter.incrementAndGet();
    
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

  public static void main(String[] args) throws NumberFormatException, UnknownHostException {
    String ip, port;
    ip = "127.0.0.1";
    port = "9090";
    // System.out.println("srcIpAddress srcPortNUmber dstIpAddress dstPortNumber");
    // if (args.length < 4) {
    // System.err.println("Invalid argument count.\n Application will exit.");
    // }

    // Test TCP Server
    // NetworkManagerTCPServerTester managerTCPServerTester =
    // new NetworkManagerTCPServerTester(args[0].toString(), args[1].toString(),
    // args[2].toString(), args[3].toString());
    // managerTCPServerTester.testSendReceive();

    //System.out.println("Client connect to " + ip + ":" + port);

    NetworkConnectionTCPClientTester clientTester = new NetworkConnectionTCPClientTester(ip, port);

    clientTester.testSendReceive("III ");

    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // // TODO Auto-generated method stub
    //
    // }
    // });

    // new NetworkManagerTCPClientMultiTester(args[0].toString(), args[1].toString(),
    // args[2].toString(),
    // args[3].toString());

  }
}
