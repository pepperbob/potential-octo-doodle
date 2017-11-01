
package de.byoc.three;

import java.io.File;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ReadFileHandler implements Handler<RoutingContext> {

  private final Vertx vertx;
  private final String storageRoot;

  public ReadFileHandler(Vertx vertx, String storageRoot) {
    this.vertx = vertx;
    this.storageRoot = storageRoot;
  }

  @Override
  public void handle(RoutingContext e) {
    String ref = new File(storageRoot, e.request().getParam("hash")).getAbsolutePath();
    vertx.fileSystem().readFile(ref, r -> {
      final JsonObject json = new JsonObject(r.result());
      final HttpServerResponse response = e.response();
      
      json.getJsonObject("meta").stream()
              .filter(this::relevant)
              .forEach(x -> response.headers().add(x.getKey(), x.getValue().toString()));
      response.setChunked(true).write(Buffer.buffer(json.getBinary("payload"))).end();
    });
  }

  private boolean relevant(Map.Entry<String, Object> x) {
    return x.getKey().startsWith("X-") 
            || x.getKey().equalsIgnoreCase("Content-Type");
  }

}
