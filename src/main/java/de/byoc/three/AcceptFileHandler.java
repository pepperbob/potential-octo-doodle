
package de.byoc.three;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AcceptFileHandler implements Handler<RoutingContext> {
  
  private final Vertx vertx;
  
  public AcceptFileHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(RoutingContext e) {
    e.request().bodyHandler(b -> {
      JsonObject item = new JsonObject();
      item.put("id", UUID.randomUUID().toString());
      item.put("filename", e.request().getParam("filename"));  
      item.put("payload", b.getBytes());
      item.put("meta", toMap(e.request().headers()));
      
      final String ref = item.getString("id");
      final String location = String.format("%s://%s%s%s", e.request().scheme(), 
              e.request().host(), "/api/", ref);

      vertx.fileSystem()
              .writeFile(ref, item.toBuffer(), x -> {
                if(x.failed()) {
                  e.response().setStatusCode(500).end(x.cause().getMessage());
                  return;
                }
                e.response().headers().add("Location", location);
                e.response().setStatusCode(202).end();
              });
    });
  }

  private Map<String, String> toMap(MultiMap headers) {
    Map<String, String> map = new ConcurrentHashMap<>();
    headers.forEach(e -> map.put(e.getKey(), e.getValue()));
    return map;
  }

}
