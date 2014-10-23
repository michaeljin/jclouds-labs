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

import java.beans.ConstructorProperties;
import java.util.Arrays;
import javax.inject.Named;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

/**
 * A droplet.
 */
public class Droplet {
   public enum Status {
      NEW, ACTIVE, ARCHIVE, OFF;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   private final int id;
   private final String name;
   private final int memory;
   private final int vcpus;
   private final int disk;
   private final Region region;
   private final Image image;
   private final Kernel kernel;
   private final Size size;
   private final boolean locked;
   private final String created_at;
   private final Status status;
   private final Object networks;
   @Named("backup_ids")
   private final int[] backups;
   @Named("snapshot_ids")
   private final int[] snapshots;
   private final String[] features;

   @ConstructorProperties({ "id", "name", "memory", "vcpus", "disk", "region", "image", "kernel",
         "size", "locked", "created_at", "status", "networks", "backup_ids", "snapshot_ids", "features" })
   public Droplet(int id, String name, int memory, int vcpus, int disk, Region region, Image image,
         Kernel kernel, Size size, boolean locked, String created_at, Status status, Object networks,
         int[] backups,
         int[] snapshots, String[] features) {
      this.id = id;
      this.name = name;
      this.memory = memory;
      this.vcpus = vcpus;
      this.disk = disk;
      this.region = region;
      this.image = image;
      this.kernel = kernel;
      this.size = size;
      this.locked = locked;
      this.created_at = created_at;
      this.status = status;
      this.networks = networks;
      this.backups = backups;
      this.snapshots = snapshots;
      this.features = features;
   }


   public int getId() {
      return id;
   }

   public String getName() {
      return name;
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

   public Region getRegion() {
      return region;
   }

   public Image getImage() {
      return image;
   }

   public Kernel getKernel() {
      return kernel;
   }

   public Size getSize() {
      return size;
   }

   public boolean isLocked() {
      return locked;
   }

   public String getCreated_at() {
      return created_at;
   }

   public Status getStatus() {
      return status;
   }

   public Object getNetworks() {
      return networks;
   }

   public int[] getBackups() {
      return backups;
   }

   public int[] getSnapshots() {
      return snapshots;
   }

   public String[] getFeatures() {
      return features;
   }

   @Override public String toString() {
      return "Droplet{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", memory=" + memory +
            ", vcpus=" + vcpus +
            ", disk=" + disk +
            ", region=" + region +
            ", image=" + image +
            ", kernel=" + kernel +
            ", size=" + size +
            ", locked=" + locked +
            ", created_at='" + created_at + '\'' +
            ", status='" + status + '\'' +
            ", networks=" + networks +
            ", backups=" + Arrays.toString(backups) +
            ", snapshots=" + Arrays.toString(snapshots) +
            ", features=" + Arrays.toString(features) +
            '}';
   }
}
