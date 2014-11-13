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

import java.util.List;
import javax.inject.Named;

import org.jclouds.json.SerializedNames;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Size {
   public abstract String slug();
   public abstract int memory();
   public abstract int vcpus();
   public abstract int disk();
   public abstract int transfer();
   @Named("price_monthly")
   public abstract float priceMonthly();
   @Named("price_hourly")
   public abstract float priceHourly();
   public abstract List<String> regions();

   @SerializedNames({ "slug", "memory", "vcpus", "disk", "transfer", "price_monthly", "price_hourly",
         "regions" })
   public static Size create(String slug, int memory, int vcpus, int disk, int transfer, float priceMonthly, float priceHourly,
         List<String> regions) {
      return new AutoValue_Size(slug, memory, vcpus, disk, transfer, priceMonthly, priceHourly,
      regions);
   }

   Size() {}
}
