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
import java.util.List;

public class DropletCreate {
   private final Droplet droplet;
   private final Links links;

   public static class Links {
      Action[] actions;

      @ConstructorProperties({ "actions" })
      Links(Action[] actions) {
         this.actions = actions;
      }

      public Action[] getActions() {
         return actions;
      }
   }

   public static class Action {
      private final int id;
      private final String rel;
      private final String href;

      @ConstructorProperties({ "id", "rel", "href" })
      Action(int id, String rel, String href) {
         this.id = id;
         this.rel = rel;
         this.href = href;
      }

      public int getId() {
         return id;
      }

      public String getRel() {
         return rel;
      }

      public String getHref() {
         return href;
      }
   }

   @ConstructorProperties({ "droplet", "links" })
   public DropletCreate(Droplet droplet, Links links) {
      this.droplet = droplet;
      this.links = links;


   }

   public Links getLinks() {
      return links;
   }

   public Droplet getDroplet() {
      return droplet;
   }
}
