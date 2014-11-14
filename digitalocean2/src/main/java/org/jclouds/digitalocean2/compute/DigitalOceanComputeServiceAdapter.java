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
package org.jclouds.digitalocean2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.digitalocean2.compute.util.LocationNamingUtils.extractRegionId;

import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.compute.options.DigitalOceanTemplateOptions;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Image;
import org.jclouds.digitalocean2.domain.Region;
import org.jclouds.digitalocean2.domain.Size;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Atomics;

/**
 * Implementation of the Compute Service for the DigitalOcean API.
 */
public class DigitalOceanComputeServiceAdapter implements ComputeServiceAdapter<Droplet, Size, Image, Region> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOcean2Api api;
   private final Predicate<AtomicReference<Action>> nodeRunningPredicate;
   private final Predicate<AtomicReference<Action>> nodeStoppedPredicate;
   private final Predicate<Integer> nodeTerminatedPredicate;

   @Inject DigitalOceanComputeServiceAdapter(DigitalOcean2Api api,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<Action>> nodeRunningPredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<Action>> nodeStoppedPredicate,
         @Named(TIMEOUT_NODE_TERMINATED) Predicate<Integer> nodeTerminatedPredicate) {
      this.api = checkNotNull(api, "api cannot be null");
      this.nodeRunningPredicate = checkNotNull(nodeRunningPredicate, "nodeRunningPredicate cannot be null");
      this.nodeStoppedPredicate = checkNotNull(nodeStoppedPredicate, "nodeStoppedPredicate cannot be null");
      this.nodeTerminatedPredicate = checkNotNull(nodeTerminatedPredicate, "nodeTerminatedPredicate cannot be null");
   }

   @Override
   public NodeAndInitialCredentials<Droplet> createNodeWithGroupEncodedIntoName(String group, final String name,
         Template template) {
      DigitalOceanTemplateOptions templateOptions = template.getOptions().as(DigitalOceanTemplateOptions.class);
      CreateDropletOptions.Builder options = CreateDropletOptions.builder();

      // DigitalOcean specific options
      if (!templateOptions.getSshKeyIds().isEmpty()) {
         options.addSshKeyIds(templateOptions.getSshKeyIds());
      }
      if (templateOptions.getPrivateNetworking() != null) {
         options.privateNetworking(templateOptions.getPrivateNetworking());
      }
      if (templateOptions.getBackupsEnabled() != null) {
         options.backupsEnabled(templateOptions.getBackupsEnabled());
      }

      // Find the location where the Droplet has to be created
      String region = extractRegionId(template.getLocation());

//      DropletCreation dropletCreation = api.getDropletApi().create(name,
      DropletCreate dropletCreated = api.getDropletApi().create(name,
            region,
            template.getHardware().getProviderId(),
            template.getImage().getProviderId(),
            options.build());

      // We have to actively wait until the droplet has been provisioned until
      // we can build the entire Droplet object we want to return
      nodeRunningPredicate.apply(Atomics.newReference(Iterables.getOnlyElement(dropletCreated.links().actions())));
      Droplet droplet = api.getDropletApi().getDroplet(dropletCreated.droplet().id());

      LoginCredentials defaultCredentials = LoginCredentials.builder().user("root")
            .privateKey(templateOptions.getLoginPrivateKey()).build();

      return new NodeAndInitialCredentials<Droplet>(droplet, String.valueOf(droplet.id()), defaultCredentials);
   }

   @Override
   public Iterable<Image> listImages() {
      return api.getImageApi().listImages(1000);
   }

   @Override
   public Iterable<Size> listHardwareProfiles() {
      return api.getSizeApi().listSizes();
   }

   @Override
   public Iterable<Region> listLocations() {
      // DigitalOcean lists regions that are unavailable for droplet creation
      return filter(api.getRegionApi().listRegions(), new Predicate<Region>() {
         @Override
         public boolean apply(Region region) {
            return region.available();
         }
      });
   }

   @Override
   public Iterable<Droplet> listNodes() {
      return api.getDropletApi().listDroplets(1000);
   }

   @Override
   public Iterable<Droplet> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Droplet>() {
         @Override
         public boolean apply(Droplet droplet) {
            return contains(ids, String.valueOf(droplet.id()));
         }
      });
   }

   @Override
   public Image getImage(String id) {
      // The id of the image can be an id or a slug. Use the corresponding method of the API depending on what is
      // provided. If it can be parsed as a number, use the method to get by ID. Otherwise, get by slug.
      Integer imageId = Ints.tryParse(id);
      return imageId != null ? api.getImageApi().getImage(imageId) : api.getImageApi().getImage(id);
   }

   @Override
   public Droplet getNode(String id) {
      return api.getDropletApi().getDroplet(Integer.parseInt(id));
   }

   @Override
   public void destroyNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      int dropletId = Integer.parseInt(id);
      api.getDropletApi().deleteDroplet(dropletId);
      //TODO: Need to check action
      nodeTerminatedPredicate.apply(dropletId);
   }



   @Override
   public void rebootNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      Action action = api.getDropletApi().reboot(Integer.parseInt(id));
      nodeRunningPredicate.apply(Atomics.newReference(action));
   }

   @Override
   public void resumeNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      Action action = api.getDropletApi().powerOn(Integer.parseInt(id));
      nodeRunningPredicate.apply(Atomics.newReference(action));
   }

   @Override
   public void suspendNode(String id) {
      // We have to wait here, as the api does not properly populate the state
      // but fails if there is a pending event
      Action action = api.getDropletApi().powerOff(Integer.parseInt(id));
      nodeStoppedPredicate.apply(Atomics.newReference(action));
   }

}
