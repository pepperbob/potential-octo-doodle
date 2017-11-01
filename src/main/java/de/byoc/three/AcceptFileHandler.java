
package de.byoc.three;

import java.io.File;
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
  private final String storageRoot;
  
  public AcceptFileHandler(Vertx vertx, String storageRoot) {
    this.vertx = vertx;
    this.storageRoot = storageRoot;
  }

  @Override
  public void handle(RoutingContext e) {
    e.request().bodyHandler(b -> {
      JsonObject item = new JsonObject();
      item.put("id", UUID.randomUUID().toString());
      item.put("filename", e.request().getParam("filename"));
      item.put("payload", b.getBytes());
      item.put("meta", toMap(e.request().headers()));
      
      final String ref = new File(storageRoot, item.getString("id")).getAbsolutePath();
      final String location = String.format("%s://%s%s%s", e.request().scheme(), 
              e.request().host(), "/api/", item.getString("id"));

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
