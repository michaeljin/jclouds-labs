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
package org.jclouds.digitalocean2;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.digitalocean2.DigitalOcean2Constants.DIGITALOCEANV2_PROVIDER_NAME;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.digitalocean2.compute.config.DigitalOceanComputeServiceContextModule;
import org.jclouds.digitalocean2.config.DigitalOcean2HttpApiModule;
import org.jclouds.digitalocean2.config.DigitalOceanParserModule;
import org.jclouds.oauth.v2.OAuthConstants;
import org.jclouds.oauth.v2.config.CredentialType;
import org.jclouds.oauth.v2.config.OAuthAuthenticationModule;
import org.jclouds.oauth.v2.config.OAuthModule;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for DigitalOcean v2 API
 */
@AutoService(ApiMetadata.class)
public class DigitalOcean2ApiMetadata extends BaseHttpApiMetadata<DigitalOcean2Api> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public DigitalOcean2ApiMetadata() {
      this(new Builder());
   }

   protected DigitalOcean2ApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.put("oauth.endpoint", "https://cloud.digitalocean.com/v1/oauth/token");
      properties.put(AUDIENCE, "https://cloud.digitalocean.com/v1/oauth/token");
      properties.put(SIGNATURE_OR_MAC_ALGORITHM, OAuthConstants.NO_ALGORITHM);
      properties.put(PROPERTY_SESSION_INTERVAL, 3600);
      properties.put(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[24]\\.04.*");
      properties.put(OAuthProperties.CREDENTIAL_TYPE, CredentialType.BEARER_TOKEN_CREDENTIALS.toString());
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<DigitalOcean2Api, Builder> {

      protected Builder() {
         id(DIGITALOCEANV2_PROVIDER_NAME)
                 .name("Digital Ocean v2 API")
                 .identityName("Not used for OAuth")
                 .credentialName("Must be oauth2 Bearer Token")
                 .documentation(URI.create("https://developers.digitalocean.com/v2/"))
                 .defaultEndpoint("https://api.digitalocean.com/v2")
                 .defaultProperties(DigitalOcean2ApiMetadata.defaultProperties())
                 .view(typeToken(ComputeServiceContext.class))
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                       .add(DigitalOcean2HttpApiModule.class)
                       .add(OAuthAuthenticationModule.class)
                       .add(OAuthModule.class)
                       .add(DigitalOceanParserModule.class)
                       .add(DigitalOceanComputeServiceContextModule.class)
                       .build());
      }

      @Override
      public DigitalOcean2ApiMetadata build() {
         return new DigitalOcean2ApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
