/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.digitalocean2.config;

import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.handlers.DigitalOceanErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

@ConfiguresHttpApi
public class DigitalOcean2HttpApiModule extends HttpApiModule<DigitalOcean2Api> {

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(DigitalOceanErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(DigitalOceanErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(DigitalOceanErrorHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(OAuthScopes.class).toInstance(OAuthScopes.ReadOrWriteScopes.create(
            "read",
            "read write"
      ));

   }
}
