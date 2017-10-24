package net.mahdirazavi.java.toolkit.net;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName;

public class NetConnectionServerQueueTester {

  private static NetConnection netConnection;

  /**
   * @param args
   */
  public static void main(String[] args) {

    @SuppressWarnings("unused")
    NetNode testNode = null;
    @SuppressWarnings("unused")
    NetNode sourceNodeClient = null;
    NetNode sourceNodeServer = null;
    NetNode destinationNode = null;
    netConnection = null;
    @SuppressWarnings("unused")
    NetConnection netConnectionServer = null;

    final ConcurrentHashMap<Inet4Address, NetNode> Clients = new ConcurrentHashMap<Inet4Address, NetNode>();

    NetworkManager network = NetworkManager.getInstance();

    @SuppressWarnings("unused")
    // Initializing blocking queue in some place!
    BlockingQueue<NetData> queue = new LinkedBlockingQueue<>();

    try {
      sourceNodeClient = new NetNode("Source", Inet4Address.getByName("127.0.0.1"), 7090);
      testNode = new NetNode("testnode", Inet4Address.getByName("192.168.7.212"), 0);
      sourceNodeServer = new NetNode("ServerSource", Inet4Address.getByName("192.168.122.3"), 8090);
      destinationNode = new NetNode("Destination", Inet4Address.getByName("127.0.0.1"), 9090);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    try {
      netConnection = network.connectionFactory(sourceNodeServer, destinationNode, ConnectionName.MESSAGE_Server);
      System.out.println("Server started...");
      System.out.println("Server listen to " + sourceNodeServer);
    } catch (Exception e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }

    new Thread(new Runnable() {

      @SuppressWarnings("static-access")
      @Override
      public void run() {
        NetData data;
        try {
          data = netConnection.Receive();
          while (!Thread.currentThread().isInterrupted()) {

            System.out.println("blocking for receive data.");


            if (!data.isError()) {
              if (data.getData() != null) {
                System.out.println("data received." + data.getData());

              } else {
                // Client connected
                Clients.put((Inet4Address) data.getSource().getIPAddress(), data.getSource());
              }
            } else {
              System.err.println("Error in Network:" + data.getErrorDetails().getMessage());
            }

            for (NetNode address : Clients.values()) {
              try {
                netConnection.Send(new NetData("Hi client".getBytes(), 0, 37, netConnection.getSource(), address));
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            Thread.currentThread().sleep(1000);

          }
        } catch (InterruptedException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }).start();

    try {
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


}
