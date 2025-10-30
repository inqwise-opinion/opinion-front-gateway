package com.inqwise.opinion.front.gateway;

import com.inqwise.opinion.front.gateway.http.GatewayRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application entry point that boots the HTTP server using Vert.x.
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);
  private static final int DEFAULT_HTTP_PORT = 8080;
  private HttpServer httpServer;

  @Override
  public void start(Promise<Void> startPromise) {
    GatewayRouter routerFactory = new GatewayRouter(vertx, config());
    Router router = routerFactory.build();

    int httpPort = config().getInteger("http.port", DEFAULT_HTTP_PORT);

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(httpPort)
      .onSuccess(server -> {
        this.httpServer = server;
        LOGGER.info("HTTP server started on port {}", server.actualPort());
        startPromise.complete();
      })
      .onFailure(error -> {
        LOGGER.error("Failed to start HTTP server on port {}", httpPort, error);
        startPromise.fail(error);
      });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    HttpServer server = this.httpServer;
    if (server == null) {
      stopPromise.complete();
      return;
    }

    server
      .close()
      .onSuccess(unused -> {
        this.httpServer = null;
        stopPromise.complete();
      })
      .onFailure(stopPromise::fail);
  }
}
