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
package org.jclouds.digitalocean2.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.List;
import java.util.concurrent.Callable;
import javax.inject.Inject;

import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.features.KeyApi;
import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.assistedinject.Assisted;

/**
 * The {@link org.jclouds.digitalocean.features.KeyPairApi} only returns the id and name of each key but not the actual
 * public key when listing all keys.
 * <p>
 * This strategy provides a helper to get all the keys with all details populated.
 */
public class ListSshKeys {

   public interface Factory {
      ListSshKeys create(ListeningExecutorService executor);
   }

   private final KeyApi keyPairApi;
   private final ListeningExecutorService executor;

   @Inject ListSshKeys(DigitalOcean2Api api, @Assisted ListeningExecutorService executor) {
      checkNotNull(api, "api cannot be null");
      this.executor = checkNotNull(executor, "executor cannot be null");
      this.keyPairApi = api.getKeyApi();
   }

   public List<Key> execute() {
      List<Key> keys = keyPairApi.listKeys();

      ListenableFuture<List<Key>> futures = allAsList(transform(keys,
            new Function<Key, ListenableFuture<Key>>() {
               @Override
               public ListenableFuture<Key> apply(final Key input) {
                  return executor.submit(new Callable<Key>() {
                     @Override
                     public Key call() throws Exception {
                        return keyPairApi.getKey(input.id());
                     }
                  });
               }
            }));

      return getUnchecked(futures);
   }
}
