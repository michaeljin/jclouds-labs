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
package org.jclouds.docker.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.docker.DockerApi;
import org.jclouds.docker.compute.options.DockerTemplateOptions;
import org.jclouds.docker.domain.Config;
import org.jclouds.docker.domain.Container;
import org.jclouds.docker.domain.HostConfig;
import org.jclouds.docker.domain.Image;
import org.jclouds.docker.options.ListContainerOptions;
import org.jclouds.docker.options.RemoveContainerOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * defines the connection between the {@link org.jclouds.docker.DockerApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class DockerComputeServiceAdapter implements
        ComputeServiceAdapter<Container, Hardware, Image, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DockerApi api;

   @Inject
   public DockerComputeServiceAdapter(DockerApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   public NodeAndInitialCredentials<Container> createNodeWithGroupEncodedIntoName(String group, String name,
                                                                                  Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");

      String imageId = checkNotNull(template.getImage().getId(), "template image id must not be null");
      String loginUser = template.getImage().getDefaultCredentials().getUser();
      String loginUserPassword = template.getImage().getDefaultCredentials().getPassword();

      DockerTemplateOptions templateOptions = DockerTemplateOptions.class.cast(template.getOptions());
      int[] inboundPorts = templateOptions.getInboundPorts();

      Map<String, Object> exposedPorts = Maps.newHashMap();
      for (int inboundPort : inboundPorts) {
         exposedPorts.put(inboundPort + "/tcp", Maps.newHashMap());
      }

      Config.Builder containerConfigBuilder = Config.builder()
              .imageId(imageId)
              .exposedPorts(exposedPorts);

      if (templateOptions.getCommands().isPresent()) {
         containerConfigBuilder.cmd(templateOptions.getCommands().get());
      }

      if (templateOptions.getMemory().isPresent()) {
         containerConfigBuilder.memory(templateOptions.getMemory().get());
      }

      if (templateOptions.getHostname().isPresent()) {
         containerConfigBuilder.hostname(templateOptions.getHostname().get());
      }

      if (templateOptions.getCpuShares().isPresent()) {
         containerConfigBuilder.cpuShares(templateOptions.getCpuShares().get());
      }

      if (templateOptions.getEnv().isPresent()) {
         containerConfigBuilder.env(templateOptions.getEnv().get());
      }

      if (templateOptions.getVolumes().isPresent()) {
         Map<String, Object> volumes = Maps.newLinkedHashMap();
         for (String containerDir : templateOptions.getVolumes().get().values()) {
            volumes.put(containerDir, Maps.newHashMap());
         }
         containerConfigBuilder.volumes(volumes);
      }
      Config containerConfig = containerConfigBuilder.build();

      logger.debug(">> creating new container with containerConfig(%s)", containerConfig);
      Container container = api.getRemoteApi().createContainer(name, containerConfig);
      logger.trace("<< container(%s)", container.getId());

      HostConfig.Builder hostConfigBuilder = HostConfig.builder()
              .publishAllPorts(true)
              .privileged(true);

      if (templateOptions.getDns().isPresent()) {
         hostConfigBuilder.dns(templateOptions.getDns().get());
      }
      // set up for volume bindings
      if (templateOptions.getVolumes().isPresent()) {
         for (Map.Entry<String, String> entry : templateOptions.getVolumes().get().entrySet()) {
            hostConfigBuilder.binds(ImmutableList.of(entry.getKey() + ":" + entry.getValue()));
         }
      }
      HostConfig hostConfig = hostConfigBuilder.build();

      api.getRemoteApi().startContainer(container.getId(), hostConfig);
      container = api.getRemoteApi().inspectContainer(container.getId());
      if (container.getState().getExitCode() != 0) {
         destroyNode(container.getId());
         throw new IllegalStateException(String.format("Container %s has not started correctly", container.getId()));
      }
      return new NodeAndInitialCredentials<Container>(container, container.getId(),
              LoginCredentials.builder().user(loginUser).password(loginUserPassword).build());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Set<Hardware> hardware = Sets.newLinkedHashSet();
      // todo they are only placeholders at the moment
      hardware.add(new HardwareBuilder().ids("micro").hypervisor("lxc").name("micro").processor(new Processor(1, 1)).ram(512).build());
      hardware.add(new HardwareBuilder().ids("small").hypervisor("lxc").name("small").processor(new Processor(1, 1)).ram(1024).build());
      hardware.add(new HardwareBuilder().ids("medium").hypervisor("lxc").name("medium").processor(new Processor(1, 1)).ram(2048).build());
      hardware.add(new HardwareBuilder().ids("large").hypervisor("lxc").name("large").processor(new Processor(1, 1)).ram(3072).build());
      return hardware;
   }

   @Override
   public Set<Image> listImages() {
      Set<Image> images = Sets.newHashSet();
      for (Image image : api.getRemoteApi().listImages()) {
         // less efficient than just listImages but returns richer json that needs repoTags coming from listImages
         Image inspected = api.getRemoteApi().inspectImage(image.getId());
         if (inspected.getRepoTags().isEmpty()) {
            inspected = Image.builder().fromImage(inspected).repoTags(image.getRepoTags()).build();
         }
         images.add(inspected);
      }
      return images;
   }

   @Override
   public Image getImage(final String imageId) {
      // less efficient than just inspectImage but listImages return repoTags
      return find(listImages(), new Predicate<Image>() {

         @Override
         public boolean apply(Image input) {
            return input.getId().equals(imageId);
         }
      }, null);
   }

   @Override
   public Iterable<Container> listNodes() {
      Set<Container> containers = Sets.newHashSet();
      for (Container container : api.getRemoteApi().listContainers(ListContainerOptions.Builder.all(true))) {
         // less efficient than just listNodes but returns richer json
         containers.add(api.getRemoteApi().inspectContainer(container.getId()));
      }
      return containers;
   }

   @Override
   public Iterable<Container> listNodesByIds(final Iterable<String> ids) {
      Set<Container> containers = Sets.newHashSet();
      for (String id : ids) {
         containers.add(api.getRemoteApi().inspectContainer(id));
      }
      return containers;
   }

   @Override
   public Iterable<Location> listLocations() {
      return ImmutableSet.of();
   }

   @Override
   public Container getNode(String id) {
      return api.getRemoteApi().inspectContainer(id);
   }

   @Override
   public void destroyNode(String id) {
      api.getRemoteApi().removeContainer(id, RemoveContainerOptions.Builder.force(true));
   }

   @Override
   public void rebootNode(String id) {
      api.getRemoteApi().stopContainer(id);
      api.getRemoteApi().startContainer(id);
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException("resume not supported");
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

}
