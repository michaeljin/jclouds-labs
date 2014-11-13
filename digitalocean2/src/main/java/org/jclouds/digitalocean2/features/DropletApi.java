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
package org.jclouds.digitalocean2.features;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.digitalocean2.DigitalOcean2Constants.READ_ONLY_SCOPE;
import static org.jclouds.digitalocean2.DigitalOcean2Constants.READ_WRITE_SCOPE;

import java.io.Closeable;
import java.util.Set;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Instances via their REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#droplets"/>
 * @see DropletApi
 */
@Path("/droplets")
@RequestFilters(OAuthAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface DropletApi extends Closeable {


   @Named("droplet:list")
   @GET
   @SelectJson("droplets")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Set<Droplet> listDroplets();

   @Named("droplet:listkernels")
   @GET
   @SelectJson("kernels")
   @Path("/{id}/kernels")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Set<Kernel> listKernels(@PathParam("id") int id);

   @Named("droplet:listsnapshots")
   @GET
   @SelectJson("snapshots")
   @Path("/{id}/snapshots")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Set<Snapshot> listSnapshots(@PathParam("id") int id);

   @Named("droplet:listbackups")
   @GET
   @SelectJson("backups")
   @Path("/{id}/backups")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Set<Backup> listBackups(@PathParam("id") int id);

   @Named("droplet:actions")
   @GET
   @SelectJson("actions")
   @Path("/{id}/actions")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Set<Action> listActions(@PathParam("id") int id);

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   @OAuthScopes(READ_WRITE_SCOPE)
   DropletCreate create(@PayloadParam("name") String name, @PayloadParam("region") String region,
                  @PayloadParam("size") String size, @PayloadParam("image") String image);

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(CreateDropletOptions.class)
   @OAuthScopes(READ_WRITE_SCOPE)
   DropletCreate create(@PayloadParam("name") String name, @PayloadParam("region") String region,
         @PayloadParam("size") String size, @PayloadParam("image") String image, CreateDropletOptions options);

   @Named("droplet:get")
   @GET
   @SelectJson("droplet")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @OAuthScopes(READ_ONLY_SCOPE)
   Droplet getDroplet(@PathParam("id") int id);

   @Named("droplet:delete")
   @DELETE
   @Path("/{id}")
   @OAuthScopes(READ_WRITE_SCOPE)
   void deleteDroplet(@PathParam("id") int id);

   @Named("droplet:reboot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"reboot\"\n}")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action reboot(@PathParam("id") int id);

   @Named("droplet:power_cycle")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_cycle\"\n}")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action powerCycle(@PathParam("id") int id);

   @Named("droplet:shutdown")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"shutdown\"\n}")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action shutdown(@PathParam("id") int id);

   @Named("droplet:power_off")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_off\"\n}")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action powerOff(@PathParam("id") int id);

   @Named("droplet:power_on")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_on\"\n}")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action powerOn(@PathParam("id") int id);

   @Named("droplet:snapshot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("%7B\"type\": \"snapshot\", \"name\":\"{name}\"%7D")
   @OAuthScopes(READ_WRITE_SCOPE)
   Action snapshot(@PathParam("id") int id, @PayloadParam("name") String name);

}

