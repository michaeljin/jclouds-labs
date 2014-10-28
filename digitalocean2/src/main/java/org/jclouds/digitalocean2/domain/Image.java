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

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Arrays;

import javax.inject.Named;

import com.google.common.base.Optional;

/**
 * A droplet.
 */
public class Image {

   private final int id;
   private final String name;
   private final String distribution;
   private final Optional<String> slug;
   @Named("public")
   private final boolean ispublic;
   private final String[] regions;
   @Named("created_at")
   private final String created_at;
   private final OperatingSystem os;

   @ConstructorProperties({ "id", "name", "distribution", "slug", "public", "regions", "created_at" })
   public Image(int id, String name, String distribution, String slug, boolean ispublic, String[] regions,
         String created_at) {
      this.id = id;
      this.name = name;
      this.distribution = distribution;
      this.slug = fromNullable(slug);
      this.ispublic = ispublic;
      this.regions = regions;
      this.created_at = created_at;
      if (name != null && distribution != null) {
         this.os = OperatingSystem.builder().from(name, checkNotNull(distribution, "distribution")).build();
      } else {
         this.os = null;
      }
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

   public boolean isPublic() {
      return ispublic;
   }

   public String[] getRegions() {
      return regions;
   }

   public String getCreated_at() {
      return created_at;
   }

   public OperatingSystem getOs() {
      return os;
   }

   @Override public String toString() {
      return "Image{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", distribution='" + distribution + '\'' +
            ", slug=" + slug +
            ", ispublic=" + ispublic +
            ", regions=" + Arrays.toString(regions) +
            ", created_at='" + created_at + '\'' +
            ", os=" + os +
            '}';
   }
}
