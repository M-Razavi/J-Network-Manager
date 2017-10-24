/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 *
 *
 *
 * LastEdit Jun 17, 2015-10:10:39 AM Using JRE 1.7.0_55
 *
 *
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.mahdirazavi.java.toolkit.io.IOConnection;
import net.mahdirazavi.java.toolkit.io.Loggers;

/**
 * The Class {@link NetworkManager} Manages {@link NetConnection} for all needed connection in
 * application.
 *
 * @author Mahdi Razavi
 * @version 1.0
 * @created 11-May-2015 11:10:13 AM
 */
public class NetworkManager implements NetworkConnentionAbstractFactory {

  /** The net connection collection. */
  private static ConcurrentMap<String, NetConnection> netConnectionCollection;

  /** The voice encryption key. */
  // private static String voiceEncryptionKey;

  /** The voice encryption algorithm string. */
  // private static String voiceEncryptionAlgorithmString;

  /** The voice encoding enabled. */
  private static boolean voiceEncodingEnabled;

  public boolean isVoiceEncodingEnabled() {
    return voiceEncodingEnabled;
  }


  public void setVoiceEncodingEnabled(boolean voiceEncodingEnabled) {
    NetworkManager.voiceEncodingEnabled = voiceEncodingEnabled;
  }

  /**
   * The Enumeration of ConnectionName.
   */
  public enum ConnectionName {

    /** The Message. */
    MESSAGE, //
    /** The call. */
    CALL, //
    /** The CAL l_ group. */
    CALL_CONFERENCE, //
    /** The Message_ server. */
    MESSAGE_Server;
  }

  /**
   * Instantiates a new network manager.
   */
  private NetworkManager() {
    netConnectionCollection = new ConcurrentHashMap<String, NetConnection>();



  }


  /**
   * Gets the single instance of PropertiesCache.
   *
   * @return single instance of PropertiesCache
   */
  public static NetworkManager getInstance() {
    return SingletonLazyHolder.INSTANCE;
  }

  /**
   * Close connection.
   *
   * @param netConnection the net connection
   */
  public void closeConnection(NetConnection netConnection) {
    if (netConnectionCollection.containsValue(netConnection)) {
      // NetConnection tmpConnection = null;
      for (NetConnection nc : netConnectionCollection.values()) {
        if (nc.equals(netConnection)) {
          if (nc != null) {
            nc.close();
            // TODO: check for associated key is deleted:
            netConnectionCollection.values().remove(nc);
          }
        }

      }
      // for (String netConnectionName : netConnectionCollection.keySet()) {
      // tmpConnection = netConnectionCollection.get(netConnectionName);
      // if (tmpConnection.equals(netConnection)) {
      // closeConnection(netConnectionName);
      // }
      // }
    }
  }

  /**
   * Close connection.
   *
   * @param name name
   */
  public void closeConnection(String name) {

    if (netConnectionCollection.containsKey(name)) {
      netConnectionCollection.get(name).close();
      netConnectionCollection.remove(name);
    }
  }


  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.net.NetworkConnentionAbstractFactory#connectionFactory(net.
   * mahdirazavi.java.toolkit.net.NetNode, net.mahdirazavi.java.toolkit.net.NetNode,
   * net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName)
   */
  @Override
  public NetConnection connectionFactory(NetNode sourceNode, NetNode destinationNode, ConnectionName ConnectionName)
      throws Exception {
    return connectionFactory(sourceNode, destinationNode, ConnectionName, null);
  }


  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.net.NetworkConnentionAbstractFactory#connectionFactory(net.
   * mahdirazavi.java.toolkit.net.NetNode, net.mahdirazavi.java.toolkit.net.NetNode,
   * net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName,
   * java.util.concurrent.BlockingQueue)
   */
  @Override
  public NetConnection connectionFactory(NetNode sourceNode, NetNode destinationNode, ConnectionName ConnectionName,
      BlockingQueue<NetData> queue) throws Exception {
    return connectionFactory(sourceNode, destinationNode, ConnectionName, queue, 1);
  }



  /*
   * (non-Javadoc)
   *
   * @see net.mahdirazavi.java.toolkit.net.NetworkConnentionAbstractFactory#connectionFactory(net.
   * mahdirazavi.java.toolkit.net.NetNode, net.mahdirazavi.java.toolkit.net.NetNode,
   * net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName,
   * java.util.concurrent.BlockingQueue, int)
   */
  @Override
  public NetConnection connectionFactory(NetNode sourceNode, NetNode destinationNode, ConnectionName ConnectionName,
      BlockingQueue<NetData> queue, int maxClient) throws Exception {

    checkNetConnectionCollection();
    NetConnection netConnection = null;
    NetworkConnectionProtocol netProtocol = null;

    switch (ConnectionName) {


      case MESSAGE:
        netProtocol = NetworkConnectionProtocol.TCP;
        netConnection = new NetConnectionClientQueue(
            createIOConnection(sourceNode, destinationNode, netProtocol, NetworkConnectionType.Client, NetworkDataType.Data),
            "CON_NP", sourceNode, destinationNode, queue);
        break;

      case CALL:
        netProtocol = NetworkConnectionProtocol.UDP;
        netConnection = new NetConnectionClientQueue(
            createIOConnection(sourceNode, destinationNode, netProtocol, NetworkConnectionType.Server, NetworkDataType.Voice),
            "CON_CALL", sourceNode, destinationNode, queue);
        // Loggers.Network.trace("NetConnection has been built: " + netConnection.getName() + " dst:
        // "
        // + netConnection.getDestination());
        break;

      case CALL_CONFERENCE:
        netProtocol = NetworkConnectionProtocol.UDP;
        // Loggers.Network.trace("111.5555-Create CallGRoup. netConnectionCollection keys are:");
        // for (String key : netConnectionCollection.keySet()) {
        // implement with RTP
        // }
        break;

      case MESSAGE_Server:
        netProtocol = NetworkConnectionProtocol.TCP;
        netConnection = new NetConnectionServerQueue(createIOConnection(sourceNode, destinationNode, netProtocol,
            NetworkConnectionType.Server, NetworkDataType.Data, maxClient), "CON_Message_Server", sourceNode, destinationNode,
            queue, maxClient);
        break;

      default:
        throw new Exception("Requested connection is not implemented.");
    }
    String cnnName = ConnectionName + "-" + sourceNode + "-" + destinationNode + "-" + netProtocol;
    // Loggers.Network.trace("2222222222- connectionFactory: initialise connection: " + cnnName);
    netConnectionCollection.put(cnnName, netConnection);
    return netConnection;
  }

  /**
   * Check net connection collection for remove null entry.
   */
  private void checkNetConnectionCollection() {
    for (Map.Entry<String, NetConnection> connection : netConnectionCollection.entrySet()) {
      if (connection.getValue() == null) {
        // Loggers.Network.trace("checkNetConnectionCollection :" + connection.getKey() + " IS
        // null.");
        netConnectionCollection.remove(connection.getKey());
      } else {
        // Loggers.Network.trace("checkNetConnectionCollection :" + connection.getKey() +
        // " ISNT null.");
      }
    }
  }


  /**
   * Creates the {@link IOConnection} and hide details of it.
   *
   * @param sourceNode the source node
   * @param destinationNode the destination node
   * @param netProtocol the net protocol
   * @param netCnnType the net connection type
   * @param netDataType the net data type
   * @param maxClient the max client
   * @return the IO connection
   * @throws Exception not implemented connection
   */
  private static IOConnection createIOConnection(NetNode sourceNode, NetNode destinationNode,
      NetworkConnectionProtocol netProtocol, NetworkConnectionType netCnnType, NetworkDataType netDataType, int maxClient)
      throws Exception {
    IOConnection connection = null;
    Loggers.Network.trace("createIOConnection(): src=" + sourceNode + " dst=" + destinationNode);

    int connectRetry = 10;
    sucess: // Successful initialization
    for (int i = 0; i < connectRetry; i++) {

      switch (netProtocol) {
        case TCP:
          if (netCnnType.equals(NetworkConnectionType.Server)) {
            connection = new NetworkConnectionTCPServer(sourceNode.getIPAddress(), sourceNode.getPort(), 1, maxClient,
                "CON_TCPServer", true);
            try {
              connection.initialize();
              break sucess;
            } catch (IOException e) {
              Loggers.Network.error("Error in initilize TCPServer on " + sourceNode.getIPAddress() + ":" + sourceNode.getPort(),
                  e);
              throw e;
            }

          } else if (netCnnType.equals(NetworkConnectionType.Client)) {
            connection = new NetworkConnectionTCPClient(sourceNode.getIPAddress(), sourceNode.getPort(),
                destinationNode.getIPAddress(), destinationNode.getPort(), "CON_TCPClient");
            try {
              connection.initialize();
              break sucess;
            } catch (IOException e) {
              Loggers.Network.error("Error in initilize TCPClient from " + sourceNode.getIPAddress() + ":" + sourceNode.getPort()
                  + " to " + destinationNode.getIPAddress() + ":" + destinationNode.getPort(), e);
              throw e;
            }

          }
          break;
        case UDP:
          if (netDataType.equals(NetworkDataType.Data)) {// XX Connection
            // UDP Data Prtocol...
          } else if (netDataType.equals(NetworkDataType.Voice)) {
            // RTP connection
          }

          break;

        default:
          throw new Exception("Requested connection is not implemented.");
      }
      Thread.sleep(5000);
    }
    return connection;
  }

  /**
   * Creates the {@link IOConnection} and hide details of it.
   *
   * @param sourceNode the source node
   * @param destinationNode the destination node
   * @param netProtocol the net protocol
   * @param netCnnType the net connection type
   * @param netDataType the net data type
   * @return the IO connection
   * @throws Exception not implemented connection
   */
  private static IOConnection createIOConnection(NetNode sourceNode, NetNode destinationNode,
      NetworkConnectionProtocol netProtocol, NetworkConnectionType netCnnType, NetworkDataType netDataType) throws Exception {
    return createIOConnection(sourceNode, destinationNode, netProtocol, netCnnType, netDataType, 1);

  }

  /**
   * The Class LazyHolder. For singleton implementation.
   */
  private static class SingletonLazyHolder {
    /** The Constant INSTANCE. */
    private static final NetworkManager INSTANCE = new NetworkManager();
  }
}
