package com.inqwise.opinion.front.gateway.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Simple secure resource served once authentication succeeds.
 */
public class SecureHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    JsonObject payload = new JsonObject()
      .put("message", "Hello from the secure route")
      .put("path", ctx.request().path());

    ctx
      .response()
      .setStatusCode(200)
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(payload.encode());
  }
}
