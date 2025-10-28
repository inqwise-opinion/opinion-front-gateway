package com.inqwise.opinion.front.gateway.http;

import com.inqwise.opinion.front.gateway.handler.SecureHandler;
import com.inqwise.opinion.front.gateway.security.TokenAuthHandler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Centralised router configuration for the HTTP gateway.
 */
public class GatewayRouter {

  private final Vertx vertx;
  private final JsonObject config;

  public GatewayRouter(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config == null ? new JsonObject() : config.copy();
  }

  public Router build() {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router
      .get("/health")
      .handler(ctx -> ctx.response().setStatusCode(200).end("OK"));

    String requiredToken = config.getString("auth.token", "dev-token");
    TokenAuthHandler authHandler = new TokenAuthHandler(requiredToken);

    router.route("/secure/*").handler(authHandler);
    router.get("/secure/hello").handler(new SecureHandler());

    return router;
  }
}
