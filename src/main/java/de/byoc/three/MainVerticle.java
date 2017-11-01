
package de.byoc.three;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
  
  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);
    
    router.post("/api/:filename").handler(
            new AcceptFileHandler(vertx, config().getString("storage-root")));
    router.get("/api/:hash").handler(
            new ReadFileHandler(vertx, config().getString("storage-root")));
    
    vertx.createHttpServer().requestHandler(router::accept)
            .listen(config().getInteger("http-port", 8080));
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  
  
  
}
