package com.inqwise.opinion.front.gateway.security;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.Objects;

/**
 * Very small bearer-token handler for local development and internal calls.
 */
public class TokenAuthHandler implements Handler<RoutingContext> {

  private static final String BEARER_PREFIX = "Bearer ";

  private final String expectedToken;

  public TokenAuthHandler(String expectedToken) {
    this.expectedToken = expectedToken;
  }

  @Override
  public void handle(RoutingContext ctx) {
    String header = ctx.request().getHeader("Authorization");

    if (isValid(header)) {
      ctx.next();
      return;
    }

    ctx
      .response()
      .setStatusCode(401)
      .putHeader("WWW-Authenticate", "Bearer")
      .end("Unauthorized");
  }

  private boolean isValid(String header) {
    if (expectedToken == null || expectedToken.isBlank()) {
      return false;
    }
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      return false;
    }
    String presentedToken = header.substring(BEARER_PREFIX.length()).trim();
    return Objects.equals(expectedToken, presentedToken);
  }
}
