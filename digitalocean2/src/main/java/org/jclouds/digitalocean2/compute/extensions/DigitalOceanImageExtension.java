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
package org.jclouds.digitalocean2.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.find;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean2.DigitalOcean2Api;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.logging.Logger;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * The {@link org.jclouds.compute.extensions.ImageExtension} implementation for the DigitalOcean provider.
 */
@Singleton
public class DigitalOceanImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOcean2Api api;
   private final Predicate<AtomicReference<Action>> imageAvailablePredicate;
   private final Predicate<AtomicReference<Action>> nodeStoppedPredicate;
   private final Function<org.jclouds.digitalocean2.domain.Image, Image> imageTransformer;

   @Inject DigitalOceanImageExtension(DigitalOcean2Api api,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<AtomicReference<Action>> imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<AtomicReference<Action>> nodeStoppedPredicate,
         Function<org.jclouds.digitalocean2.domain.Image, Image> imageTransformer) {
      this.api = Preconditions.checkNotNull(api, "api cannot be null");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate cannot be null");
      this.nodeStoppedPredicate = checkNotNull(nodeStoppedPredicate, "nodeStoppedPredicate cannot be null");
      this.imageTransformer = checkNotNull(imageTransformer, "imageTransformer cannot be null");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      Droplet droplet = api.getDropletApi().getDroplet(Integer.parseInt(id));

      if (droplet == null) {
         throw new NoSuchElementException("Cannot find droplet with id: " + id);
      }

      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, "DigitalOcean only supports creating images through cloning.");
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;

      // Droplet needs to be stopped
      Action powerOffEvent = api.getDropletApi().powerOff(Integer.parseInt(cloneTemplate.getSourceNodeId()));
      nodeStoppedPredicate.apply(Atomics.newReference(powerOffEvent));

      Action snapshotEvent = api.getDropletApi().snapshot(Integer.parseInt(cloneTemplate.getSourceNodeId()),
            cloneTemplate.getName());

      logger.info(">> registered new Image, waiting for it to become available");

      // Until the process completes we don't have enough information to build an image to return
      imageAvailablePredicate.apply(Atomics.newReference(snapshotEvent));

      org.jclouds.digitalocean2.domain.Image snapshot = find(api.getImageApi().listImages(100),
            new Predicate<org.jclouds.digitalocean2.domain.Image>() {
               @Override
               public boolean apply(org.jclouds.digitalocean2.domain.Image input) {
                  return input.name().equals(cloneTemplate.getName());
               }
            });

      return immediateFuture(imageTransformer.apply(snapshot));
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         // The id of the image can be an id or a slug. Use the corresponding method of the API depending on what is
         // provided. If it can be parsed as a number, use the method to destroy by ID. Otherwise, destroy by slug.
         Integer imageId = Ints.tryParse(id);
         if (imageId != null) {
            logger.debug(">> image does not have a slug. Using the id to delete the image...");
            api.getImageApi().deleteImage(imageId);
         }
         return true;
      } catch (Exception ex) {
         return false;
      }
   }
}
