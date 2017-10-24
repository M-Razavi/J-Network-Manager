package net.mahdirazavi.java.toolkit.net;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName;

public class NetworkManagerDemo {


  /**
   * @param args
   */
  public static void main(String[] args) {

    NetNode testNode = null;
    NetNode sourceNodeClient = null;
    NetNode sourceNodeServer = null;
    NetNode destinationNode = null;
    NetConnection netConnection = null;
    @SuppressWarnings("unused")
    NetConnection netConnectionServer = null;

    NetworkManager network = NetworkManager.getInstance();

    // Initializing blocking queue in some place!
    BlockingQueue<NetData> queue = new LinkedBlockingQueue<>();

    try {
      sourceNodeClient = new NetNode("Source", Inet4Address.getByName("127.0.0.1"), 7090);
      testNode = new NetNode("testnode", Inet4Address.getByName("127.0.0.1"), 5090);
      sourceNodeServer = new NetNode("Source", Inet4Address.getByName("127.0.0.1"), 8090);
      destinationNode = new NetNode("Destination", Inet4Address.getByName("127.0.0.1"), 9090);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    try {
       netConnection = network.connectionFactory(testNode,destinationNode,ConnectionName.MESSAGE);
      netConnection =
          network.connectionFactory(sourceNodeClient, destinationNode,
              NetworkManager.ConnectionName.MESSAGE, queue);
      netConnection =
          network.connectionFactory(sourceNodeServer, destinationNode,
              NetworkManager.ConnectionName.MESSAGE_Server, queue, 5);
    } catch (Exception e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }

    try {
      // System.out.println("data sent-1.");

       netConnection.Send(new NetData("salam NetworkManager".getBytes(), 0, 20));

      System.out.println("blocking for receive data.");

      for (int i = 0; i < 10; i++) {
        NetData data = netConnection.Receive();
        if(!data.isError())
        {
          System.out.println("data received." + data.getData());          
        }
        else
        {
          System.err.println("Error in Network:"+data.getErrorDetails().getMessage());
        }
      }

      // byte[] data1 = data.getData();
      // int length = data.getLength();
      // int offset = data.getOffset();
      // NetNode src = data.getSource();
      // NetNode dst = data.getDestination();


      // System.out.println("data sent-1.");
      // netConnection.Send(new NetData("Bye NetworkManager".getBytes(), 0, 20));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
