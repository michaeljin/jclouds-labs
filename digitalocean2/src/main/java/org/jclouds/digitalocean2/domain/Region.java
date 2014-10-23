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

package org.jclouds.digitalocean2.domain;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Set;

public class Region {
   private final String slug;
   private final String name;
   private final Set<String> sizes;
   private final boolean available;
   private final String[] features;

   @ConstructorProperties({ "slug", "name", "sizes", "available", "features" })
   public Region(String slug, String name, Set<String> sizes, boolean available, String[] features) {
      this.slug = slug;
      this.name = name;
      this.sizes = sizes;
      this.available = available;
      this.features = features;
   }

   public String getSlug() {
      return slug;
   }

   public String getName() {
      return name;
   }

   public Set<String> getSizes() {
      return sizes;
   }

   public boolean isAvailable() {
      return available;
   }

   public String[] getFeatures() {
      return features;
   }

   @Override public String toString() {
      return "Region{" +
            "slug='" + slug + '\'' +
            ", name='" + name + '\'' +
            ", sizes=" + sizes +
            ", available=" + available +
            ", features=" + Arrays.toString(features) +
            '}';
   }
}
