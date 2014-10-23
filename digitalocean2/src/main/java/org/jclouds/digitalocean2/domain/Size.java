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
import javax.inject.Named;

public class Size {
   private final String slug;
   private final int memory;
   private final int vcpus;
   private final int disk;
   private final int transfer;
   @Named("price_monthly")
   private final float priceMonthly;
   @Named("price_hourly")
   private final float priceHourly;
   private final String[] regions;

   @ConstructorProperties({ "slug", "memory", "vcpus", "disk", "transfer", "price_monthly", "price_hourly",
         "regions" })

   public Size(String slug, int memory, int vcpus, int disk, int transfer, float priceMonthly, float priceHourly,
         String[] regions) {
      this.slug = slug;
      this.memory = memory;
      this.vcpus = vcpus;
      this.disk = disk;
      this.transfer = transfer;
      this.priceMonthly = priceMonthly;
      this.priceHourly = priceHourly;
      this.regions = regions;
   }

   public String getSlug() {
      return slug;
   }

   public int getMemory() {
      return memory;
   }

   public int getVcpus() {
      return vcpus;
   }

   public int getDisk() {
      return disk;
   }

   public int getTransfer() {
      return transfer;
   }

   public float getPriceMonthly() {
      return priceMonthly;
   }

   public float getPriceHourly() {
      return priceHourly;
   }

   public String[] getRegions() {
      return regions;
   }

   @Override public String toString() {
      return "Size{" +
            "slug='" + slug + '\'' +
            ", memory=" + memory +
            ", vcpus=" + vcpus +
            ", disk=" + disk +
            ", transfer=" + transfer +
            ", price_monthly=" + priceMonthly +
            ", price_hourly=" + priceHourly +
            ", regions=" + Arrays.toString(regions) +
            '}';
   }
}
