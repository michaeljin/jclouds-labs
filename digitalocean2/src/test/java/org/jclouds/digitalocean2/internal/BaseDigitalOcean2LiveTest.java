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
package org.jclouds.digitalocean2.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.DigitalOcean2Constants;
import org.jclouds.digitalocean2.DigitalOcean2ProviderMetadata;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.providers.ProviderMetadata;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class BaseDigitalOcean2LiveTest extends BaseApiLiveTest<DigitalOcean2Api> {

   protected Predicate<AtomicReference<Action>> actionCompleted;
   protected Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   protected Predicate<Integer> nodeTerminated;


   public BaseDigitalOcean2LiveTest() {
      provider = DigitalOcean2Constants.DIGITALOCEANV2_PROVIDER_NAME;
   }

   @Override protected ProviderMetadata createProviderMetadata() {
      return new DigitalOcean2ProviderMetadata();
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      props.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      return props;
   }

   protected DigitalOcean2Api create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      actionCompleted = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Action>>>(){}));
/*
      nodeRunning = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Action>>>(){},
            Names.named(TIMEOUT_NODE_RUNNING)));
*/
      nodeTerminated = injector.getInstance(Key.get(new TypeLiteral<Predicate<Integer>>(){},
            Names.named(TIMEOUT_NODE_TERMINATED)));
      return injector.getInstance(DigitalOcean2Api.class);
   }

   protected void assertActionCompleted(Action action) {
      AtomicReference<Action> ref = Atomics.newReference(checkNotNull(action, "action"));
      checkState(actionCompleted.apply(ref), "Timeout waiting for action: %s", action);
      assertEquals(ref.get().status(), Action.Status.COMPLETED);
   }

   protected void assertNodeTerminated(Integer dropletId) {
      checkNotNull(dropletId, "dropletId");
      assertEquals(nodeTerminated.apply(dropletId), true, String.format("Timeout waiting for dropletId: %s", dropletId));
   }
}

