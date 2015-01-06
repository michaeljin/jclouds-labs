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

import com.google.common.base.Optional;

public class Snapshot {
   private final int id;
   private final String name;
   private final String distribution;
   private final Optional<String> slug;
   private final boolean ispublic;
   private final String[] regions;

   @ConstructorProperties({ "id", "name", "distribution", "slug", "ispublic", "regions" })
   public Snapshot(int id, String name, String distribution, Optional<String> slug, boolean ispublic,
         String[] regions) {
      this.id = id;
      this.name = name;
      this.distribution = distribution;
      this.slug = slug;
      this.ispublic = ispublic;
      this.regions = regions;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDistribution() {
      return distribution;
   }

   public Optional<String> getSlug() {
      return slug;
   }

   public boolean isIspublic() {
      return ispublic;
   }

   public String[] getRegions() {
      return regions;
   }

   @Override public String toString() {
      return "Snapshot{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", distribution='" + distribution + '\'' +
            ", slug=" + slug +
            ", ispublic=" + ispublic +
            ", regions=" + Arrays.toString(regions) +
            '}';
   }
}
