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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

@AutoValue
public abstract class Droplet {
   public enum Status {
      NEW, ACTIVE, ARCHIVE, OFF;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }
   public abstract int id();
   public abstract String name();
   public abstract int memory();
   public abstract int vcpus();
   public abstract int disk();
   @Nullable public abstract Region region();
   @Nullable public abstract Image image();
   @Nullable public abstract Kernel kernel();
   public abstract String size();
   public abstract boolean locked();
   public abstract String created();
   public abstract Status status();
   @Nullable public abstract Network network();
   @Nullable public abstract List<Integer> backups();
   @Nullable public abstract List<Integer> snapshots();
   public abstract List<String> features();

   @SerializedNames({ "id", "name", "memory", "vcpus", "disk", "region", "image", "kernel",
         "size_slug", "locked", "created_at", "status", "networks", "backup_ids", "snapshot_ids", "features" })
   public static Droplet create(int id, String name, int memory, int vcpus, int disk, Region region, Image image,
         Kernel kernel, String size, boolean locked, String created, Status status, Network network,
         List<Integer> backups, List<Integer> snapshots, List<String> features) {
      return new AutoValue_Droplet(id, name, memory, vcpus, disk, region, image,
            kernel, size, locked, created, status, network,
            copyOf(backups),  copyOf(snapshots), copyOf(features));
   }


   public Set<Network.Address> getPublicAddresses() {
      ImmutableSet.Builder<Network.Address> addressBuilder = new ImmutableSet.Builder<Network.Address>();
      addressBuilder.addAll(network().getIpv4Networks())
            .addAll(network().getIpv6Networks());
      return FluentIterable
            .from(addressBuilder.build())
            .filter(Network.Predicates.publicNetworks())
            .toSet();
   }

   public Set<Network.Address> getPrivateAddresses() {
      ImmutableSet.Builder<Network.Address> addressBuilder = new ImmutableSet.Builder<Network.Address>();
      addressBuilder.addAll(network().getIpv4Networks())
            .addAll(network().getIpv6Networks());
      return FluentIterable
            .from(addressBuilder.build())
            .filter(Network.Predicates.privateNetworks())
            .toSet();
   }

   Droplet(){}
}
