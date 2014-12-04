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
import java.util.List;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Keys via the REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#keys"/>
 * @see KeyApi
 */
@Path("/account/keys")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface KeyApi extends Closeable {


   @Named("key:list")
   @GET
   @SelectJson("ssh_keys")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   List<Key> listKeys();

   @Named("key:list")
   @GET
   @SelectJson("ssh_keys")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   List<Key> listKeys(@QueryParam("per_page") int perPage);

   @Named("key:create")
   @POST
   @SelectJson("ssh_key")
   @MapBinder(BindToJsonPayload.class)
   Key create(@PayloadParam("name") String name, @PayloadParam("public_key") String key);

   @Named("key:get")
   @GET
   @SelectJson("ssh_key")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Key getKey(@PathParam("id") int id);

   @Named("key:get")
   @GET
   @SelectJson("ssh_key")
   @Path("/{fingerprint}")
   @Fallback(NullOnNotFoundOr404.class)
   Key getKey(@PathParam("fingerprint") String fingerprint);

   @Named("key:update")
   @PUT
   @SelectJson("ssh_key")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Key updateKey(@PathParam("id") int id);

   @Named("key:update")
   @PUT
   @SelectJson("ssh_key")
   @Path("/{fingerprint}")
   @Fallback(NullOnNotFoundOr404.class)
   Key updateKey(@PathParam("fingerprint") String fingerprint);

   @Named("key:delete")
   @DELETE
   @Path("/{id}")
   void deleteKey(@PathParam("id") int id);

   @Named("key:delete")
   @DELETE
   @Path("/{fingerprint}")
   void deleteKey(@PathParam("fingerprint") String fingerprint);

}

