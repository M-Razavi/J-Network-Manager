/**
 *
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.mahdirazavi.java.toolkit.io.IOConnection;

/**
 * @author Mahdi
 *
 */
public class NetworkConnectionTCPServerTester implements NetServerEventsListener,
    NetClientEventsListener {

  Map<NetNode, IOConnection> clients;
  ExecutorService threadPoolClients;
  private NetworkConnectionTCPServer server;

  /**
   * @throws Exception
   *
   */
  public NetworkConnectionTCPServerTester() throws Exception {

    server = new NetworkConnectionTCPServer(InetAddress.getByName("192.168.122.210"), 5555, 1,1,
        "TCPServer", true);
    System.out.println("running server:" + server.toString());
    server.initialize();
    server.addOnClientConnect(this);
    server.addOnError(this);

    Thread serverThread = new Thread(server);
    serverThread.start();

    clients = new HashMap<NetNode, IOConnection>();

    threadPoolClients = Executors.newFixedThreadPool(3);

    testLoop();
  }

  private void testLoop() {

//    new Thread(new Runnable() {
//
//      @Override
//      public void run() {
//        while (!Thread.currentThread().isInterrupted()) {
//        }
//
//      }
//    }).start();;

  }

  /*--------------------------------------------Server Events----------------------*/

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
    // new Thread(client).start();
    threadPoolClients.execute(client);
    System.out.println("client connected." + client.getRemoteNode());

    try {
      client.addOnReceive(this);
      client.addOnReceiveError(this);
      client.addOnSend(this);
      client.addOnSendError(this);
      client.addOnConnect(this);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
    Exception exp = (Exception) data;
    System.out.println(exp.getMessage());

  }



  /*--------------------------------------------Client Events----------------------*/



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
      System.out.println("Data received from '" + doer + ".");

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
    System.out.println("Error in received." + ((Exception) data).getMessage());
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
    System.out.println("Error in received." + ((Exception) data).getMessage());
  }


  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onConnect(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onConnect(Object sender, Object data) {
    System.out.println("client connect.");

  }


  /**
   * @param args
   * @throws NumberFormatException
   * @throws IOException
   */
  public static void main(String[] args) {
    try {
      // NetworkConnectionTCPServerTester serverTester = new NetworkConnectionTCPServerTester();
      new NetworkConnectionTCPServerTester();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
