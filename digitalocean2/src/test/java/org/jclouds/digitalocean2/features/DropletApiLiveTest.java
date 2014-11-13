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

import static org.jclouds.digitalocean2.domain.Droplet.Status.OFF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.digitalocean2.domain.Action;
import org.jclouds.digitalocean2.domain.Backup;
import org.jclouds.digitalocean2.domain.Droplet;
import org.jclouds.digitalocean2.domain.DropletCreate;
import org.jclouds.digitalocean2.domain.Kernel;
import org.jclouds.digitalocean2.domain.Snapshot;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2LiveTest;
import org.testng.annotations.Test;
import com.google.common.collect.Iterables;

public class DropletApiLiveTest extends BaseDigitalOcean2LiveTest {

   private DropletApi api() {
      return api.getDropletApi();
   }
   private int dropletId;

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testListDroplets() {
//      Droplet droplet = api().getDroplet(dropletId);
      Set<Droplet> droplets = api().listDroplets();
      assertTrue(droplets.size() == 1, "The created droplet must be in the list");
   }

//   @Test(groups = "live", dependsOnMethods = "testPowerOn")
   @Test(groups = "live", dependsOnMethods = "testBackups")
   public void testListKernels() {
      Set<Droplet> droplets = api().listDroplets();
      Set<Kernel> kernels = api().listKernels(droplets.iterator().next().id());
      assertEquals(kernels.iterator().next().name(), "DO-recovery-static-fsck");
   }

   @Test(groups = "live", dependsOnMethods = "testPowerOff")
   public void testSnapshots() {
      Action action = api().snapshot(dropletId, prefix + dropletId + "-snapshot");
      assertActionCompleted(action);
      Set<Snapshot> snapshots = api().listSnapshots(dropletId);
      assertEquals(snapshots.size(), 1, "Must contain 1 snapshot");
   }

   @Test(groups = "live", dependsOnMethods = "testSnapshots")
   public void testBackups() {

//      Can't find docs for this one at the moment
      Set<Droplet> droplets = api().listDroplets();
      Set<Backup> backups = api().listBackups(droplets.iterator().next().id());
//      assertEquals(backups.iterator().next().getName(), "jclouds-test-snapshot");
   }

   @Test(groups = "live", dependsOnMethods = "testListKernels")
   public void testActions() {
      Set<Droplet> droplets = api().listDroplets();
      Set<Action> actions = api().listActions(droplets.iterator().next().id());
      assertEquals(actions.iterator().next().type(), "snapshot");
   }

   @Test(groups = "live")
   public void testCreate() {
      DropletCreate dropletCreate = api().create("test1", "nyc3", "512mb", "ubuntu-14-04-x64");
      assertActionCompleted(Iterables.getOnlyElement(dropletCreate.links().actions()));
      dropletId = dropletCreate.droplet().id();
      Droplet droplet = api().getDroplet(dropletId);
      assertNotNull(droplet, "Droplet should not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testGet")
   public void testPowerOff() {
      Action action = api().powerOff(dropletId);
      assertActionCompleted(action);
      Droplet droplet = api().getDroplet(dropletId);
      assertEquals(droplet.status(), OFF, "Droplet should be off");
   }

/* Apparently the droplet is powered on automatically after taking a snapshot
   @Test(groups = "live", dependsOnMethods = "testBackups")
   public void testPowerOn() {
      Action action = api().powerOn(dropletId);
      assertActionCompleted(action);
      Droplet droplet = api().getDroplet(dropletId);
      assertEquals(droplet.status(), ACTIVE, "Droplet should be Active");
   }
*/

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testGet() {
      Droplet droplet = api().getDroplet(dropletId);
      assertNotNull(droplet);

   }

   @Test(groups = "live", dependsOnMethods = "testActions")
   public void testDelete() throws InterruptedException {
      api().deleteDroplet(dropletId);
      assertNodeTerminated(dropletId);
      Droplet droplet = api().getDroplet(dropletId);
      assertNull(droplet);

   }
}
