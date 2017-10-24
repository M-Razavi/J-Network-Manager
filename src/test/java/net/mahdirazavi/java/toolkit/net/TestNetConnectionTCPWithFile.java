/**
 * 
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Random;

/**
 * @author Mahdi
 * 
 */
public class TestNetConnectionTCPWithFile {

  volatile static int destinationPort=0;
  private static void runServer(String srcIP, String srcPort, String destIP,
      String destPort) throws Exception {

    final Object clientConnectLock = new Object();

    NetworkManager network = NetworkManager.getInstance();

    File sentFile = new File("sentFile");
    final File receivedFile = new File("receivedFile");
    NetNode destNode =
        new NetNode("Server", Inet4Address.getByName(destIP), Integer.parseInt(destPort));
    NetNode sourceNode =
        new NetNode("src", Inet4Address.getByName(srcIP), Integer.parseInt(srcPort));
    final NetConnection netConnectionServer =
        network.connectionFactory(sourceNode, destNode, NetworkManager.ConnectionName.MESSAGE_Server);


    System.out.println("blocking for receive data.");
    new Thread(new Runnable() {

      @SuppressWarnings("resource")
      @Override
      public void run() {
        Thread.currentThread().setName("Write_Thread");
        FileOutputStream outFileReceived = null;
        try {
          outFileReceived = new FileOutputStream(receivedFile);
        } catch (FileNotFoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        NetData data;
        while (true) {
          
          data = null;
          try {
            data = netConnectionServer.Receive();
            destinationPort = data.getSource().getPort();
            System.out.println("data received." + data.getData());
            synchronized (clientConnectLock) {              
              clientConnectLock.notify();
            }
            outFileReceived.write(data.getData(), data.getOffset(), data.getLength());

          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();



    Random rnd = new Random(System.currentTimeMillis());

    
    @SuppressWarnings("resource")
    FileOutputStream outFileSent = new FileOutputStream(sentFile);

    byte[] randomData = new byte[180];
    synchronized (clientConnectLock) {
      clientConnectLock.wait();
    }
    while (!Thread.currentThread().isInterrupted()) {
      rnd.nextBytes(randomData);

      if(destinationPort > 0)
      {
        destNode = new NetNode("client", destNode.getIPAddress(), destinationPort);
      }
      netConnectionServer
          .Send(new NetData(randomData, 0, randomData.length, sourceNode,destNode));

      outFileSent.write(randomData, 0, randomData.length);
      Thread.sleep(20);
    }


  }

  private static void runClient(String clientIP, String clientPort, String serverIP,
      String serverPort) throws Exception {
    NetworkManager network = NetworkManager.getInstance();

    NetNode serverNode =
        new NetNode("Destination", Inet4Address.getByName(serverIP), Integer.parseInt(serverPort));
    NetNode clientNode =
        new NetNode("Source", Inet4Address.getByName(clientIP), Integer.parseInt(clientPort));
    final NetConnection netConnectionClient =
        network.connectionFactory(clientNode, serverNode, NetworkManager.ConnectionName.MESSAGE);
    String testStartMessage = "test start.";
    netConnectionClient
        .Send(new NetData(testStartMessage.getBytes(), 0, testStartMessage.length()));


    // echo mode
    NetData data;
    while (true) {
      data = null;
      data = netConnectionClient.Receive();
      System.out.println("data received." + data.getData());
      NetData newData =
          new NetData(data.getData(), data.getOffset(), data.getLength(), data.getDestination(),
              data.getSource());
      netConnectionClient.Send(newData);
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      System.err.println("Input argument number is not valid\n"
          + "Please enter clientIP clientPort serverIP serverPort \"client\"/\"server\"");
      return;
    }
    String clientIP = args[0];
    String clientPort = args[1];
    String serverIP = args[2];
    String serverPort = args[3];
    String appType = args[4];

    try {
      
    final NetworkConnectionType conType =
        (appType.equals("server") ? NetworkConnectionType.Server : NetworkConnectionType.Client);

    if (conType.equals(NetworkConnectionType.Client)) {
      runClient(clientIP, clientPort, serverIP, serverPort);
    } else {
      runServer(clientIP, clientPort, serverIP, serverPort);
    }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}
