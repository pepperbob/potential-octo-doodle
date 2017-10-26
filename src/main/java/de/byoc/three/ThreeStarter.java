
package de.byoc.three;

import io.vertx.core.Vertx;

public class ThreeStarter {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
  
}
