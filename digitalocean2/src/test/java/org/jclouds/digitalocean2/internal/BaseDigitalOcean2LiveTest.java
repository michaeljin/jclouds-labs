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
package org.jclouds.digitalocean2.internal;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.DigitalOcean2Constants;
import com.google.inject.Injector;
import com.google.inject.Module;


public class BaseDigitalOcean2LiveTest extends BaseApiLiveTest<DigitalOcean2Api> {

   protected static final String API_URL_PREFIX = "https://api.digitalocean.com/v2";
   protected static final String DROPLETS_API_URL_SUFFIX = "/droplets/";
   protected static final String IMAGES_API_URL_SUFFIX = "/images/";
   protected static final String REGIONS_API_URL_SUFFIX = "/regions/";
   protected static final String SIZES_API_URL_SUFFIX = "/sizes/";
   protected static final String ACTIONS_URL_SUFFIX = "/actions";

   public BaseDigitalOcean2LiveTest() {
      provider = DigitalOcean2Constants.DIGITALOCEANV2_PROVIDER_NAME;
   }

   protected DigitalOcean2Api create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      return injector.getInstance(DigitalOcean2Api.class);
   }


}

