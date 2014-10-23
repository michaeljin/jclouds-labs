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
import java.security.PublicKey;
import javax.inject.Named;

/**
 * A droplet.
 */
public class Key {

   private final int id;
   private final String name;
   private final String fingerprint;
   @Named("public_key")
   private final PublicKey publicKey;

   @ConstructorProperties({ "id", "name", "fingerprint", "public_key" })

   public Key(int id, String name, String fingerprint, PublicKey publicKey) {
      this.id = id;
      this.name = name;
      this.fingerprint = fingerprint;
      this.publicKey = publicKey;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getFingerprint() {
      return fingerprint;
   }

   public PublicKey getPublicKey() {
      return publicKey;
   }

   @Override public String toString() {
      return "Key{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", fingerprint='" + fingerprint + '\'' +
            ", publicKey='" + publicKey + '\'' +
            '}';
   }
}
