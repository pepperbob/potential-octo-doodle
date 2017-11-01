
package de.byoc.three;


import java.io.IOException;
import java.net.ServerSocket;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;
  private int randomPort;
  
  @Before
  public void setup(TestContext context) throws IOException {
    vertx = Vertx.vertx();
  
    ServerSocket socket = new ServerSocket(0);
    randomPort = socket.getLocalPort();
    socket.close();
    
    vertx.deployVerticle(new MainVerticle(), 
            new DeploymentOptions().setConfig(
                    new JsonObject()
                            .put("storage-root", "target")
                            .put("http-port", randomPort)), 
            context.asyncAssertSuccess());
  }
  
  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }
  
  @Test
  public void legtDateienAb(TestContext context) {
    Async async = context.async();
    vertx.createHttpClient()
            .post(randomPort, "localhost", "/api/test.txt", x -> {
      context.assertNotNull(x.headers().get("Location"));
      async.complete();
    }).end("Test Foo!");
  }
  
  @Test
  public void ruftDateienAb(TestContext context) {
    Async async = context.async();
    
    vertx.createHttpClient()
            .post(randomPort, "localhost", "/api/test.txt", 
                    x -> vertx.createHttpClient().getAbs(x.headers().get("Location"), 
                            r -> r.bodyHandler(
                                    body -> {
                                      context.assertEquals("Hello, World!", body.toString());
                                      async.complete();
                                    }))
                            .end())
            .end("Hello, World!");
  }
  
}
