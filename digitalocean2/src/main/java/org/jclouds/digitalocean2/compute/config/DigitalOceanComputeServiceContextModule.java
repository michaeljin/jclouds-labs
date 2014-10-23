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
package org.jclouds.digitalocean2.compute.config;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.extensions.DigitalOceanImageExtension;
import org.jclouds.digitalocean2.compute.functions.DropletStatusToStatus;
import org.jclouds.digitalocean2.compute.functions.DropletToNodeMetadata;
import org.jclouds.digitalocean2.compute.functions.ImageToImage;
import org.jclouds.digitalocean2.compute.functions.RegionToLocation;
import org.jclouds.digitalocean2.compute.functions.SizeToHardware;
import org.jclouds.digitalocean2.compute.functions.TemplateOptionsToStatementWithoutPublicKey;
import org.jclouds.digitalocean2.compute.options.DigitalOceanTemplateOptions;
import org.jclouds.digitalocean2.compute.strategy.CreateKeyPairsThenCreateNodes;
import org.jclouds.digitalocean2.compute.strategy.DigitalOceanComputeServiceAdapter;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.domain.Location;
import org.jclouds.util.Predicates2;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the compute service classes for the DigitalOcean API.
 */
public class DigitalOceanComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<Droplet, Size, Image, Region> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<Droplet, Size, Image, Region>>() {
      }).to(DigitalOceanComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Droplet, NodeMetadata>>() {
      }).to(DropletToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ImageToImage.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<Size, Hardware>>() {
      }).to(SizeToHardware.class);
      bind(new TypeLiteral<Function<Droplet.Status, Status>>() {
      }).to(DropletStatusToStatus.class);

      install(new LocationsFromComputeServiceAdapterModule<Droplet, Size, Image, Region>() {
      });

      bind(CreateNodesInGroupThenAddToSet.class).to(CreateKeyPairsThenCreateNodes.class);
      bind(TemplateOptions.class).to(DigitalOceanTemplateOptions.class);
      bind(TemplateOptionsToStatement.class).to(TemplateOptionsToStatementWithoutPublicKey.class);

      bind(new TypeLiteral<ImageExtension>() {
      }).to(DigitalOceanImageExtension.class);
   }

   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_RUNNING)
   protected Predicate<Action> provideDropletRunningPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new ActionDonePredicate(), timeouts.nodeRunning, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_SUSPENDED)
   protected Predicate<Action> provideDropletSuspendedPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new ActionDonePredicate(), timeouts.nodeSuspended, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_NODE_TERMINATED)
   protected Predicate<Action> provideDropletTerminatedPredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new ActionDonePredicate(), timeouts.nodeTerminated, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<Action> provideImageAvailablePredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new ActionDonePredicate(), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @Provides
   @Singleton
   protected Predicate<Region> provideRegionAvailablePredicate(final DigitalOcean2Api api, Timeouts timeouts,
         PollPeriod pollPeriod) {
      return Predicates2.retry(new RegionAvailablePredicate(), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class ActionDonePredicate implements Predicate<Action> {

//      private final DigitalOcean2Api api;

//      public ActionDonePredicate(DigitalOcean2Api api) {
//         this.api = checkNotNull(api, "api must not be null");
//      }

      @Override
      public boolean apply(Action input) {
         switch (input.getStatus()) {
            case COMPLETED:
               return true;
            case INPROGRESS:
               return false;
            case ERROR:
            default:
               throw new IllegalStateException("Resource is in invalid status: " + input.getStatus().name());
         }
      }

   }

   @VisibleForTesting
   static class RegionAvailablePredicate implements Predicate<Region> {

/*
      private final DigitalOcean2Api api;

      public RegionAvailablePredicate(DigitalOcean2Api api) {
         this.api = checkNotNull(api, "api must not be null");
      }
*/

      @Override
      public boolean apply(Region input) {
         return input.isAvailable();
      }

   }

}
