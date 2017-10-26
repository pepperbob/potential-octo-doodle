
package de.byoc.three;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ReadFileHandler implements Handler<RoutingContext> {

  private final Vertx vertx;

  public ReadFileHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(RoutingContext e) {
    String ref = e.request().getParam("hash");
    vertx.fileSystem().readFile(ref, r -> {
      JsonObject json = new JsonObject(r.result());
      final HttpServerResponse response = e.response();

      response.headers().add("Content-Type", json.getJsonObject("meta").getString("Content-Type"));
      response.setChunked(true).write(Buffer.buffer(json.getBinary("payload"))).end();
    });
  }

}
