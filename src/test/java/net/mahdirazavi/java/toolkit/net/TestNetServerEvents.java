/**
 * 
 */
package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Mahdi
 * 
 */
public class TestNetServerEvents implements NetServerEventsListener {

  ServerEventProviderTest server;
  Socket socketTest;
  String exceptionMessage;
  String destinationIP;
  int destinationPort;

  @Before
  public void setUP() throws Exception {
    exceptionMessage = "Test error message";
    destinationIP = "127.0.0.1";
    destinationPort = 0;
    socketTest = new Socket();
    server = new ServerEventProviderTest("test server");

      server.addOnClientConnect(this);
      server.addOnError(this);
  }

  @Test
  public void testConnect() throws UnknownHostException, IOException {
    server.fireClientConnect();
  }
  
  @Test
  public void testConnectWithParam() throws UnknownHostException, IOException {
    server.fireClientConnect(socketTest);
  }

  @Test
  public void testError() {
    server.fireServerError();
  }
  
  @Test
  public void testErrorWithParam() {
    server.fireServerError(exceptionMessage);
  }

  @Override
  public void onClientConnect(Object sender, Object data) {
    org.junit.Assert.assertSame(server, sender);
//    org.junit.Assert.assertTrue(false);
//    assert server == sender;
    if (data != null) {
      assert (data instanceof Socket);
      org.junit.Assert.assertEquals(socketTest, ((Socket) data));
    }
  }

  @Override
  public void onError(Object sender, Object data) {
    org.junit.Assert.assertSame(server, sender);
    assert server == sender;
    if (data != null) {
      assert (data instanceof Exception);
      org.junit.Assert.assertEquals("Error", exceptionMessage, ((Exception) data).getMessage());
    }
  }


  
  
  
  class ServerEventProviderTest extends NetServerEvents {
    public ServerEventProviderTest(String actionDoer) {
      super(actionDoer);
      setSender(this);
    }

    public void fireClientConnect() {
      this.onClientConnectFire();
    }

    public void fireClientConnect(Socket mockSocket) {
      this.onClientConnectFire(mockSocket);
    }

    public void fireServerError() {
      this.onErrorFire();
    }

    public void fireServerError(String exceptionMessage) {
      this.onErrorFire(new Exception(exceptionMessage));
    }
  }
}
