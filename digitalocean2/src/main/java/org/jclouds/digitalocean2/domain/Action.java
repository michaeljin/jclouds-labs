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

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

public class Action {
   public enum Status {
      COMPLETED, INPROGRESS("in-progress"), ERROR;

      private String displayName;

      Status(String displayName) {
         this.displayName = displayName;
      }

      Status() {
      }

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   private final int id;
   private final Status status;
   private final String type;
   private final String started_at;
   private final String completed_at;
   private final int resource_id;
   private final String resource_type;
   private final String region;

   @ConstructorProperties({ "id", "status", "type", "started_at", "completed_at", "resource_id",
         "resource_type", "region" })
   public Action(int id, Status status, String type, String started_at, String completed_at, int resource_id,
         String resource_type, String region) {
      this.id = id;
      this.status = status;
      this.type = type;
      this.started_at = started_at;
      this.completed_at = completed_at;
      this.resource_id = resource_id;
      this.resource_type = resource_type;
      this.region = region;
   }

   public int getId() {
      return id;
   }

   public Status getStatus() {
      return status;
   }

   public String getType() {
      return type;
   }

   public String getStarted_at() {
      return started_at;
   }

   public String getCompleted_at() {
      return completed_at;
   }

   public int getResource_id() {
      return resource_id;
   }

   public String getResource_type() {
      return resource_type;
   }

   public String getRegion() {
      return region;
   }

   @Override public String toString() {
      return "Action{" +
            "id=" + id +
            ", status='" + status + '\'' +
            ", type='" + type + '\'' +
            ", started_at='" + started_at + '\'' +
            ", completed_at='" + completed_at + '\'' +
            ", resource_id=" + resource_id +
            ", resource_type='" + resource_type + '\'' +
            ", region='" + region + '\'' +
            '}';
   }
}
