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

import java.lang.reflect.Field;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

@AutoValue
public abstract class Action {
   public enum Status {
      COMPLETED, INPROGRESS("in-progress"), ERRORED;

      Status(final String displayName) {
         try {
            final Field nameField = this.getClass().getSuperclass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(this, displayName);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      Status() {}

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   public abstract int id();
   @Nullable public abstract String rel();
   @Nullable public abstract String href();
   @Nullable public abstract Status status();
   @Nullable public abstract String type();
   @Nullable public abstract String started_at();
   @Nullable public abstract String completed_at();
   @Nullable public abstract int resource_id();
   @Nullable public abstract String resource_type();
   @Nullable public abstract String region();

   @SerializedNames({ "id", "rel", "href", "status", "type", "started_at", "completed_at", "resource_id",
         "resource_type", "region" })
   public static Action create(int id, String rel, String href, Status status, String type, String started_at, String completed_at, int resource_id,
         String resource_type, String region) {
      return new AutoValue_Action(id, rel, href, status, type, started_at, completed_at, resource_id,
      resource_type, region);
   }

   Action() {}
}
