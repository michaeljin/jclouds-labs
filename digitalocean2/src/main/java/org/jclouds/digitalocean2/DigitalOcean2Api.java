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

import java.io.Closeable;

import org.jclouds.digitalocean2.features.ActionApi;
import org.jclouds.digitalocean2.features.DropletApi;
import org.jclouds.digitalocean2.features.ImageApi;
import org.jclouds.digitalocean2.features.KeyApi;
import org.jclouds.digitalocean2.features.RegionApi;
import org.jclouds.digitalocean2.features.SizeApi;
import org.jclouds.rest.annotations.Delegate;
import com.google.common.annotations.Beta;


/**
 * Provides access to DigitalOcean.
 * <p/>
 *
 * @see <a href="https://developers.digitalocean.com/v2/">Digital Ocean API Docs</a>
 */
@Beta
public interface DigitalOcean2Api extends Closeable {

   /**
    * Provides access to Droplet features
    */
   @Delegate
   DropletApi getDropletApi();

   /**
    * Provides access to SSH Key features
    */
   @Delegate
   KeyApi getKeyApi();

   /**
    * Provides access to Actions
    */
   @Delegate
   ImageApi getImageApi();

   /**
    * Provides access to Actions
    */
   @Delegate
   ActionApi getActionApi();

   /**
    * Provides access to Sizes
    */
   @Delegate
   SizeApi getSizeApi();

   /**
    * Provides access to Regions
    */
   @Delegate
   RegionApi getRegionApi();

}
