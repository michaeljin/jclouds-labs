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
package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Closeables2.closeQuietly;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.rest.HttpClient;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class FileDownloadFromURI implements Function<URI, File> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final HttpClient client;
   private final String isosDir;

   @Inject
   public FileDownloadFromURI(HttpClient client, @Named(VIRTUALBOX_WORKINGDIR) String workingDir) {
      this.client = client;
      this.isosDir = workingDir + File.separator + "isos";
   }

   @Override
   public File apply(@Nullable URI input) {
      final File file = new File(isosDir, new File(input.getPath()).getName());
      try {
         if (!file.exists()) {
            final InputStream inputStream = client.get(input);
            checkNotNull(inputStream, "%s not found", input);
            try {
               Files.asByteSink(file).writeFrom(inputStream);
               return file;
            } catch (FileNotFoundException e) {
               logger.error(e, "File %s could not be found", file.getPath());
            } catch (IOException e) {
               logger.error(e, "Error when downloading file %s", input.toString());
            } finally {
               closeQuietly(inputStream);
            }
            return null;
         } else {
            logger.debug("File %s already exists. Skipping download", file.getPath());
            return file;
         }
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }
}
