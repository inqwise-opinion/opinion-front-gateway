package com.inqwise.opinion.front.gateway;

import com.inqwise.opinion.front.gateway.http.GatewayRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
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

    HttpServerOptions serverOptions = new HttpServerOptions().setReuseAddress(true);

    String httpHost = config().getString("http.host");
    if (httpHost != null && !httpHost.isBlank()) {
      serverOptions.setHost(httpHost);
    }

    HttpServer server =
      vertx.createHttpServer(serverOptions).requestHandler(router);
    this.httpServer = server;

    server
      .listen(httpPort)
      .onSuccess(started -> {
        String resolvedHost = serverOptions.getHost();
        if (resolvedHost == null || resolvedHost.isBlank()) {
          resolvedHost = "0.0.0.0";
        }
        LOGGER.info(
          "HTTP server started on {}:{}",
          resolvedHost,
          started.actualPort()
        );
        startPromise.complete();
      })
      .onFailure(error -> {
        this.httpServer = null;
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
