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
package org.jclouds.digitalocean2.features;

import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.DigitalOcean2Constants;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Network;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.http.BaseMockWebServerTest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * Mock tests for the {@link DropletApi} class.
 */
@Test(groups = "unit", testName = "ElasticStackMockTest")
public class DropletApiMockTest extends BaseMockWebServerTest {

   public void testListDroplets() throws IOException, InterruptedException {
      MockWebServer server = mockWebServer(new MockResponse()
            .setBody(payloadFromResource("/droplets_list.json")));
      DigitalOcean2Api api = api(server.getUrl("/").toString());

      try {
         DropletApi dropletApi = api.getDropletApi();
         Set<Droplet> droplets = dropletApi.listDroplets();
         assertEquals(droplets.size(), 1);

         Droplet droplet = droplets.iterator().next();
         NodeMetadataBuilder builder = new NodeMetadataBuilder();

         if (!droplet.getPublicAddresses().isEmpty()) {
            builder.publicAddresses(FluentIterable
                        .from(droplet.getPublicAddresses())
                        .transform(new Function<Network.Address, String>() {
                           @Override
                           public String apply(final Network.Address input) {
                              return input.getIp();
                           }
                        })
            );
         }

         RecordedRequest request = server.takeRequest();
         assertAuthentication(request);
         assertEquals(request.getRequestLine(),
               String.format("GET /droplets HTTP/1.1", server.getUrl("/").toString()));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testCreateDroplets() throws IOException, InterruptedException {
      MockWebServer server = mockWebServer(new MockResponse()
            .setBody(payloadFromResource("/droplet_create_res.json")));
      DigitalOcean2Api api = api(server.getUrl("/").toString());

      try {
         CreateDropletOptions.Builder options = CreateDropletOptions.builder();
         options.addSshKeyIds(ImmutableSet.of(421192));
         options.privateNetworking(false);
         options.backupsEnabled(false);

         // Find the location where the Droplet has to be created
         String region = "sfo1";

         //      DropletCreation dropletCreation = api.getDropletApi().create(name,
         DropletCreate dropletCreated = api.getDropletApi().create("digitalocean2-s-d5e",
               region,
               "512mb",
               "6374124",
               options.build());

         assertEquals(dropletCreated.droplet().name(), "digitalocean2-s-d5e");

         RecordedRequest request = server.takeRequest();
         assertAuthentication(request);
         assertEquals(request.getRequestLine(),
               String.format("POST /droplets HTTP/1.1", server.getUrl("/").toString()));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   private static void assertAuthentication(final RecordedRequest request) throws InterruptedException {
      assertEquals(request.getHeader(HttpHeaders.AUTHORIZATION), "Bearer c5401990f0c24135e8d6b5d260603fc71696d4738da9aa04a720229a01a2521d");
   }

   private byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   protected void addOverrideProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_RETRIES, "1");
      props.setProperty("digitalocean2.identity", "doesn't matter");
      props.setProperty("digitalocean2.credential", "c5401990f0c24135e8d6b5d260603fc71696d4738da9aa04a720229a01a2521d");
   }

   @Override
   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }


   protected DigitalOcean2Api api(String uri) {
      Properties overrides = new Properties();
      this.addOverrideProperties(overrides);
      return ContextBuilder.newBuilder(DigitalOcean2Constants.DIGITALOCEANV2_PROVIDER_NAME) //
            .endpoint(uri) //
            .overrides(overrides) //
            .buildApi(DigitalOcean2Api.class);
   }


}
