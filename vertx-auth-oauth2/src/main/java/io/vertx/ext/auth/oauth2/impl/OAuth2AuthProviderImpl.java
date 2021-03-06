/*
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.vertx.ext.auth.oauth2.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.impl.flow.OAuth2Flow;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.impl.flow.AuthCodeImpl;
import io.vertx.ext.auth.oauth2.impl.flow.ClientImpl;
import io.vertx.ext.auth.oauth2.impl.flow.PasswordImpl;

/**
 * @author Paulo Lopes
 */
public class OAuth2AuthProviderImpl implements OAuth2Auth {

  private final Vertx vertx;
  private final JsonObject config;

  private final OAuth2Flow flow;

  public OAuth2AuthProviderImpl(Vertx vertx, OAuth2FlowType flow, JsonObject config) {
    this.vertx = vertx;
    this.config = new JsonObject()
        .put("authorizationPath", "/oauth/authorize")
        .put("tokenPath", "/oauth/token")
        .put("revocationPath", "/oauth/revoke")
        .put("useBasicAuthorizationHeader", true)
        .put("clientSecretParameterName", "client_secret").mergeIn(config);

    switch (flow) {
      case AUTH_CODE:
        this.flow = new AuthCodeImpl(this.vertx, this.config);
        break;
      case CLIENT:
        this.flow = new ClientImpl(this.vertx, this.config);
        break;
      case PASSWORD:
        this.flow = new PasswordImpl(this.vertx, this.config);
        break;
      default:
        throw new IllegalArgumentException("Invalid oauth2 flow type: " + flow);
    }
  }

  public JsonObject getConfig() {
    return config;
  }

  public Vertx getVertx() {
    return vertx;
  }

  @Override
  public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {

  }

  @Override
  public String authorizeURL(JsonObject params) {
    return flow.authorizeURL(params);
  }

  @Override
  public void getToken(JsonObject params, Handler<AsyncResult<AccessToken>> handler) {
    flow.getToken(params, handler);
  }

  @Override
  public OAuth2Auth api(HttpMethod method, String path, JsonObject params, Handler<AsyncResult<JsonObject>> handler) {
    OAuth2API.api(vertx, config, method, path, params, handler);
    return this;
  }
}
