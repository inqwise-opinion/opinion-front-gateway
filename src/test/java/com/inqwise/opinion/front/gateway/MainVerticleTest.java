package com.inqwise.opinion.front.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class MainVerticleTest {

  @Test
  void shouldStartHttpServer(
    final Vertx vertx,
    final VertxTestContext testContext
  ) throws Exception {
    int port = nextFreePort();
    DeploymentOptions options = new DeploymentOptions().setConfig(
      new JsonObject().put("http.port", port)
    );

    vertx
      .deployVerticle(new MainVerticle(), options)
      .onComplete(deploy -> {
        if (deploy.failed()) {
          testContext.failNow(deploy.cause());
          return;
        }

        String deploymentId = deploy.result();
        WebClient client = WebClient.create(vertx);

        client
          .get(port, "localhost", "/health")
          .send()
          .onComplete(ar -> {
            client.close();

            if (ar.failed()) {
              vertx
                .undeploy(deploymentId)
                .onComplete(unused -> testContext.failNow(ar.cause()));
              return;
            }

            testContext.verify(() ->
              assertEquals(200, ar.result().statusCode())
            );

            vertx
              .undeploy(deploymentId)
              .onComplete(undeploy -> {
                if (undeploy.failed()) {
                  testContext.failNow(undeploy.cause());
                } else {
                  testContext.completeNow();
                }
              });
          });
      });
  }

  @Test
  void shouldStopHttpServer(
    final Vertx vertx,
    final VertxTestContext testContext
  ) throws Exception {
    int port = nextFreePort();
    DeploymentOptions options = new DeploymentOptions().setConfig(
      new JsonObject().put("http.port", port)
    );

    vertx
      .deployVerticle(new MainVerticle(), options)
      .compose(vertx::undeploy)
      .compose(unused ->
        vertx
          .createHttpServer()
          .requestHandler(req -> req.response().end("released"))
          .listen(port)
          .compose(HttpServer::close)
      )
      .onComplete(ar -> {
        if (ar.failed()) {
          testContext.failNow(ar.cause());
        } else {
          testContext.completeNow();
        }
      });
  }

  private static int nextFreePort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    }
  }
}
