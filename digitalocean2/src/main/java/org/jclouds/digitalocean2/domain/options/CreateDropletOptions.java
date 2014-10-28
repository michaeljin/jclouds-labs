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
package org.jclouds.digitalocean2.domain.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import com.google.common.collect.ImmutableSet;

/**
 * Options to customize droplet creation.
 */
public class CreateDropletOptions implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   private final Set<Integer> sshKeys;
   private final Boolean backupsEnabled;
   private final Boolean ipv6Enabled;
   private final Boolean privateNetworking;
   private final String userData;

   public CreateDropletOptions(@Nullable Set<Integer> sshKeys, @Nullable Boolean backupsEnabled,
         @Nullable Boolean ipv6Enabled, @Nullable Boolean privateNetworking, @Nullable String userData) {
      this.sshKeys = sshKeys;
      this.backupsEnabled = backupsEnabled != null ? backupsEnabled : false;
      this.ipv6Enabled = ipv6Enabled != null ? ipv6Enabled : false;
      this.privateNetworking = privateNetworking != null ? privateNetworking : false;
      this.userData = userData != null ? userData : "";
   }

   static class DropletRequest {
      final String name;
      final String region;
      final String size;
      final String image;

      @Named("ssh_keys")
      Set<Integer> sshKeys;
      @Named("backups")
      Boolean backups;
      @Named("ipv6")
      Boolean ipv6;
      @Named("private_networking")
      Boolean privateNetworking;
      @Named("user_data")
      String userData;

      DropletRequest(String name, String region, String size, String image) {
         this.name = name;
         this.region = region;
         this.size = size;
         this.image = image;
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      DropletRequest droplet = new DropletRequest(checkNotNull(postParams.get("name"), "name parameter not present").toString(),
            checkNotNull(postParams.get("region"), "region parameter not present").toString(),
            checkNotNull(postParams.get("size"), "size parameter not present").toString(),
            checkNotNull(postParams.get("image"), "image parameter not present").toString());

      if (sshKeys != null)
         droplet.sshKeys = sshKeys;
      droplet.backups = backupsEnabled;
      droplet.ipv6 = ipv6Enabled;
      droplet.privateNetworking = privateNetworking;
      if (!userData.isEmpty())
         droplet.userData = userData;

      return bindToRequest(request, droplet);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }

   public Set<Integer> getSshKeys() {
      return sshKeys;
   }

   public Boolean getPrivateNetworking() {
      return privateNetworking;
   }

   public Boolean getBackupsEnabled() {
      return backupsEnabled;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private ImmutableSet.Builder<Integer> sshKeyIds = ImmutableSet.builder();
      private Boolean backupsEnabled;
      private Boolean ipv6Enabled;
      private Boolean privateNetworking;
      private String userData;

      /**
       * Adds a set of ssh key ids to be added to the droplet.
       */
      public Builder addSshKeyIds(Iterable<Integer> sshKeyIds) {
         this.sshKeyIds.addAll(sshKeyIds);
         return this;
      }

      /**
       * Adds an ssh key id to be added to the droplet.
       */
      public Builder addSshKeyId(int sshKeyId) {
         this.sshKeyIds.add(sshKeyId);
         return this;
      }

      /**
       * Enables a private network interface if the region supports private
       * networking.
       */
      public Builder privateNetworking(boolean privateNetworking) {
         this.privateNetworking = privateNetworking;
         return this;
      }

      /**
       * Enabled backups for the droplet.
       */
      public Builder backupsEnabled(boolean backupsEnabled) {
         this.backupsEnabled = backupsEnabled;
         return this;
      }

      public CreateDropletOptions build() {
         return new CreateDropletOptions(sshKeyIds.build(), backupsEnabled, ipv6Enabled, privateNetworking, userData);
      }
   }
}
