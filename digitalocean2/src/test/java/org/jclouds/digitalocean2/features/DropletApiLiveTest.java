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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Set;

import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2LiveTest;
import org.testng.annotations.Test;

public class DropletApiLiveTest extends BaseDigitalOcean2LiveTest {

   private DropletApi api() {
      return api.getDropletApi();
   }
   private int dropletId;

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListDroplets() {

      Set<Droplet> droplets = api().listDroplets();
      assertEquals(droplets.size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListKernels() {

      Set<Droplet> droplets = api().listDroplets();
      Set<Kernel> kernels = api().listKernels(droplets.iterator().next().getId());
      assertEquals(kernels.iterator().next().getName(), "DO-recovery-static-fsck");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testSnapshots() {

      Set<Droplet> droplets = api().listDroplets();
      Set<Snapshot> snapshots = api().listSnapshots(droplets.iterator().next().getId());
      assertEquals(snapshots.iterator().next().getName(), "jclouds-test-snapshot");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testBackups() {

//      Can't even find docs for this one
      Set<Droplet> droplets = api().listDroplets();
      Set<Backup> backups = api().listBackups(droplets.iterator().next().getId());
//      assertEquals(backups.iterator().next().getName(), "jclouds-test-snapshot");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testActions() {
      Set<Droplet> droplets = api().listDroplets();
      Set<Action> actions = api().listActions(droplets.iterator().next().getId());
      assertEquals(actions.iterator().next().getType(), "power_cycle");
   }

   @Test(groups = "live")
   public void testCreate() {
      Droplet droplet = api().create("test1", "nyc3", "512mb", "ubuntu-14-04-x64");
      dropletId = droplet.getId();
      droplet.getStatus();

   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testGet() {
      Droplet droplet = api().getDroplet(dropletId);
      assertNotNull(droplet);

   }

   @Test(groups = "live", dependsOnMethods = "testGet")
   public void testDelete() throws InterruptedException {
      api().deleteDroplet(dropletId);
      Thread.sleep(10000);
      Droplet droplet = api().getDroplet(dropletId);
      assertNull(droplet);

   }
}
