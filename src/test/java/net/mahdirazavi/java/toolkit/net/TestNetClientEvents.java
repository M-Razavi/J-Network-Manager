package net.mahdirazavi.java.toolkit.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

public class TestNetClientEvents implements NetClientEventsListener {



  ClientEventProviderTest client;
  NetData testData;
  String exceptionMessage1;
  String exceptionMessage2;
  String testConnectMessage;
  String testSendMessage;
  NetNode testSource;
  NetNode testDestination;

  @Before
  public void setUP() throws Exception {
    exceptionMessage1 = "Test error message 1";
    exceptionMessage2 = "Test error message 2";
    testConnectMessage = "I'm connected.";
    testSendMessage = "hiiiiiii";
    testSource = new NetNode("source", InetAddress.getByName("192.168.1.2"), 4050);
    testDestination = new NetNode("destination", InetAddress.getByName("192.168.1.1"), 4070);
    testData =
        new NetData(testConnectMessage.getBytes(), 0, testConnectMessage.length(), testSource,
            testDestination);
    client = new ClientEventProviderTest("test server");

    client.addOnConnect(this);
    client.addOnReceive(this);
    client.addOnReceiveError(this);
    client.addOnSend(this);
    client.addOnSendError(this);
  }

  @Test
  public void testClientConnect() throws UnknownHostException, IOException {
    client.fireClientConnect();
  }

  @Test
  public void testClientConnectWithParam() throws UnknownHostException, IOException {
    client.fireClientConnect(testConnectMessage);
  }

  @Test
  public void testClientReceive() {
    client.fireClientReceive();
  }

  @Test
  public void testClientReceiveWithParam() {
    client.fireClientReceive(testData);
  }
  
  @Test
  public void testClientReceiveError() {
    client.fireClientReceiveError();
  }
  
  @Test
  public void testClientReceiveErrorWithParam() {
    client.fireClientReceiveError(new Exception(exceptionMessage1));
  }

  @Test
  public void testClientSend() {
    client.fireClientSend();
  }

  @Test
  public void testClientSendWithParam() {
    client.fireClientSend(testSendMessage.length());
  }

  @Test
  public void testClientSendError() {
    client.fireClientSendError();
  }

  @Test
  public void testClientSendErrorWithParam() {
    client.fireClientSendError(new Exception(exceptionMessage2));
  }


  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onReceive(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onReceive(Object sender, Object data) {
    org.junit.Assert.assertSame(client, sender);
    // org.junit.Assert.assertTrue(false);
    if (data != null) {
      org.junit.Assert.assertEquals(testSource,((NetData) data).getSource()); 
      org.junit.Assert.assertEquals(testDestination,((NetData) data).getDestination()); 
      assert (data instanceof NetData);
      org.junit.Assert.assertEquals(testData, ((NetData) data));
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
    org.junit.Assert.assertSame(client, sender);
    assert client == sender;
    if (data != null) {
      assert (data instanceof Exception);
      org.junit.Assert.assertEquals("Error", exceptionMessage1, ((Exception) data).getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onSend(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSend(Object sender, Object data) {
    org.junit.Assert.assertSame(client, sender);
    // org.junit.Assert.assertTrue(false);
    if (data != null) {
      assert (data instanceof Integer);
      org.junit.Assert.assertEquals(testSendMessage.length(), ((int) data));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onSendError(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onSendError(Object sender, Object data) {
    org.junit.Assert.assertSame(client, sender);
    assert client == sender;
    if (data != null) {
      assert (data instanceof Exception);
      org.junit.Assert.assertEquals("Error", exceptionMessage2, ((Exception) data).getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.net.NetClientEventsListener#onConnect(net.mahdirazavi.java.toolkit.net.NetClientEvents,
   * java.lang.Object)
   */
  @Override
  public void onConnect(Object sender, Object data) {
    org.junit.Assert.assertSame(client, sender);
    // org.junit.Assert.assertTrue(false);
    if (data != null) {
      assert (data instanceof NetData);
      org.junit.Assert.assertEquals(testConnectMessage, ((String) data));
    }
  }


  class ClientEventProviderTest extends NetClientEvents {
    public ClientEventProviderTest(String actionDoer) {
      super(actionDoer);
      setSender(this);
    }

    public void fireClientReceive() {
      this.onReceiveFire();
    }

    public void fireClientReceive(NetData data) {
      this.onReceiveFire(data);
    }

    public void fireClientReceiveError() {
      this.onReceiveErrorFire();
    }

    public void fireClientReceiveError(Exception exp) {
      this.onReceiveErrorFire(exp);
    }

    public void fireClientSend() {
      this.onSendFire();
    }

    public void fireClientSend(int sentCount) {
      this.onSendFire(sentCount);
    }

    public void fireClientSendError() {
      this.onSendErrorFire();
    }

    public void fireClientSendError(Exception exp) {
      this.onSendErrorFire(exp);
    }

    public void fireClientConnect() {
      this.onConnectFire();
    }

    public void fireClientConnect(Object data) {
      this.onConnectFire(data);
    }

  }
}
