
package de.byoc.three;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ThreeStarter {

  public static void main(String[] args) {
    final DeploymentOptions config = new DeploymentOptions();
    config.setConfig(new JsonObject().put("storage-root", "/tmp/"));
    
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), config);
  }
  
}
