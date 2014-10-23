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
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.domain.options.CreateDropletOptions;
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
   Set<Droplet> listDroplets();

   @Named("droplet:listkernels")
   @GET
   @SelectJson("kernels")
   @Path("/{id}/kernels")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   Set<Kernel> listKernels(@PathParam("id") int id);

   @Named("droplet:listsnapshots")
   @GET
   @SelectJson("snapshots")
   @Path("/{id}/snapshots")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   Set<Snapshot> listSnapshots(@PathParam("id") int id);

   @Named("droplet:listbackups")
   @GET
   @SelectJson("backups")
   @Path("/{id}/backups")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   Set<Backup> listBackups(@PathParam("id") int id);

   @Named("droplet:actions")
   @GET
   @SelectJson("actions")
   @Path("/{id}/actions")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   Set<Action> listActions(@PathParam("id") int id);

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("droplet")
   @MapBinder(BindToJsonPayload.class)
   Droplet create(@PayloadParam("name") String name, @PayloadParam("region") String region,
                  @PayloadParam("size") String size, @PayloadParam("image") String image);

   @Named("droplet:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("droplet")
   @MapBinder(BindToJsonPayload.class)
   Droplet create(@PayloadParam("name") String name, @PayloadParam("region") String region,
         @PayloadParam("size") String size, @PayloadParam("image") String image, CreateDropletOptions options);

   @Named("droplet:get")
   @GET
   @SelectJson("droplet")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Droplet getDroplet(@PathParam("id") int id);

   @Named("droplet:delete")
   @DELETE
   @Path("/{id}")
   void deleteDroplet(@PathParam("id") int id);

   @Named("droplet:reboot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"reboot\"\n}")
   Action reboot(@PathParam("id") int id);

   @Named("droplet:power_cycle")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_cycle\"\n}")
   Action powerCycle(@PathParam("id") int id);

   @Named("droplet:shutdown")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"shutdown\"\n}")
   Action shutdown(@PathParam("id") int id);

   @Named("droplet:power_off")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_off\"\n}")
   Action powerOff(@PathParam("id") int id);

   @Named("droplet:power_on")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"power_on\"\n}")
   Action powerOn(@PathParam("id") int id);

   @Named("droplet:snapshot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\n  \"type\": \"snapshot\"\n}")
   Action snapshot(@PathParam("id") int id);

   @Named("droplet:snapshot")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("action")
   @Path("/{id}/actions")
   @Payload("{\"type\": \"snapshot\", \"name\":\"{name}\"}")
   Action snapshot(@PathParam("id") int id, @PayloadParam("name") String name);

}

