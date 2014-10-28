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
import java.util.Set;
import javax.inject.Named;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public class Network {
   private Set<Address> ipv4Networks = ImmutableSet.of();
   private Set<Address> ipv6Networks = ImmutableSet.of();

   public static class Address {
      @Named("ip_address")
      private final String ip;
      private final String netmask;
      private final String gateway;
      private final String type;

      @ConstructorProperties({ "ip_address", "netmask", "gateway", "type"})
      Address(String ip, String netmask, String gateway, String type) {
         this.ip = ip;
         this.netmask = netmask;
         this.gateway = gateway;
         this.type = type;
      }

      public String getIp() {
         return ip;
      }

      public String getNetmask() {
         return netmask;
      }

      public String getGateway() {
         return gateway;
      }

      public String getType() {
         return type;
      }
   }

   @ConstructorProperties({ "v4", "v6" })
   public Network(Set<Address> ipv4Networks, Set<Address> ipv6Networks) {
      this.ipv4Networks = ipv4Networks;
      this.ipv6Networks = ipv6Networks;
   }

   public Set<Address> getIpv4Networks() {
      return ipv4Networks;
   }

   public Set<Address> getIpv6Networks() {
      return ipv6Networks;
   }

   public static class Predicates {

      public static Predicate<Address> publicNetworks() {
         return new Predicate<Address>() {
            @Override
            public boolean apply(Address network) {
               return network.getType().equals("public");
            }
         };
      }
   }

}
