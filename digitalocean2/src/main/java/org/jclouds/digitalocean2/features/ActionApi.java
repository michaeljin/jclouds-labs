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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides access to Actions via the REST API.
 *
 * @see <a href="https://developers.digitalocean.com/v2/#actions"/>
 * @see ActionApi
 */
@Path("/actions")
@RequestFilters(OAuthAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ActionApi extends Closeable {


   @Named("action:list")
   @GET
   @SelectJson("actions")
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   Set<Action> listActions();

   @Named("action:get")
   @GET
   @SelectJson("action")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Action getAction(@PathParam("id") int id);

}

