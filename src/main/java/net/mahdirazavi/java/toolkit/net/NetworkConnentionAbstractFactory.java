/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 *
 * 
 * 
 * LastEdit Jun 17, 2015-10:08:53 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */

package net.mahdirazavi.java.toolkit.net;

import java.util.concurrent.BlockingQueue;

import net.mahdirazavi.java.toolkit.net.NetworkManager.ConnectionName;


/**
 * A factory for creating {@link NetConnection} objects.
 */
public interface NetworkConnentionAbstractFactory {

  /**
   * Connection factory. Makes network connection due to project need.
   *
   * @param SourceNode the source node
   * @param DestinationNode the destination node
   * @param name the name of requested connection that is one of the {@link ConnectionName}
   * @return the created {@link NetConnection}
   * @throws Exception if requested connection was not implemented, the "Not Implemented" exception has been occurs.
   */
  public NetConnection connectionFactory(NetNode SourceNode, NetNode DestinationNode, ConnectionName name)
      throws Exception;
  
  /**
   * Connection factory which makes {@link NetConnection} for network communication.
   * 
   * @param sourceNode the source node
   * @param destinationNode the destination node
   * @param name the name of requested connection that is one of the {@link ConnectionName}
   * @param queue the common queue for requested {@link NetConnection}
   * @return the created {@link NetConnection}
   * @throws Exception if requested connection was not implemented, the "Not Implemented" exception has been occurs.
   */
  public NetConnection connectionFactory(NetNode SourceNode, NetNode DestinationNode, ConnectionName name, BlockingQueue<NetData> queue)
      throws Exception;
  
  /**
   * Connection factory which makes {@link NetConnection} for network communication.
   * 
   * @param sourceNode the source node
   * @param destinationNode the destination node
   * @param name the name of requested connection that is one of the {@link ConnectionName}
   * @param queue the common queue for requested {@link NetConnection}
   * @param maxClient max number of client could be connect and process. It is used only for server connection.
   * @return the created {@link NetConnection}
   * @throws Exception if requested connection was not implemented, the "Not Implemented" exception has been occurs.
   */
  public NetConnection connectionFactory(NetNode SourceNode, NetNode DestinationNode, ConnectionName name, BlockingQueue<NetData> queue, int maxClient)
      throws Exception;

}
