/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOvfEnvelope;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOvfNetworkSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOwner;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVAppTemplate;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.metadataToMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.AbstractVAppApiLiveTest;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Link.Rel;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests the request/response behavior of {@link VAppTemplateApi}
 * 
 * NOTE The environment MUST have at least one template configured
 *
 * @author Aled Sage
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VAppTemplateApiLiveTest")
public class VAppTemplateApiLiveTest extends AbstractVAppApiLiveTest {

   private String key;
   private String val;

   @AfterClass(alwaysRun = true, dependsOnMethods = { "cleanUpEnvironment" })
   protected void tidyUp() {
      if (key != null) {
         try {
	         Task delete = vAppTemplateApi.getMetadataApi().deleteEntry(vAppTemplateURI, key);
	         taskDoneEventually(delete);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting metadata entry '%s'", key);
         }
      }
   }
   
   private VAppTemplate cloneVAppTemplate(boolean waitForTask) throws Exception {
      CloneVAppTemplateParams cloneVAppTemplateParams = CloneVAppTemplateParams.builder()
               .source(Reference.builder().href(vAppTemplateURI).build())
               .isSourceDelete(false)
               .name("clone")
               .build();
      VAppTemplate clonedVappTemplate = vdcApi.cloneVAppTemplate(vdcUrn, cloneVAppTemplateParams);
      
      if (waitForTask) {
         Task cloneTask = Iterables.getFirst(clonedVappTemplate.getTasks(), null);
         assertNotNull(cloneTask, "vdcApi.cloneVAppTemplate returned VAppTemplate that did not contain any tasks");
         assertTaskSucceeds(cloneTask);
      }
      return clonedVappTemplate;
   }

   @Test(description = "GET /vAppTemplate/{id}")
   public void testGetVAppTemplate() {
      vAppTemplate = vAppTemplateApi.getVAppTemplate(vAppTemplateURI);
      
      checkVAppTemplate(vAppTemplate);
      assertEquals(vAppTemplate.getHref(), vAppTemplateURI);
   }
   
   @Test(description = "GET /vAppTemplate/{id}/owner")
   public void testGetVAppTemplateOwner() {
      Owner owner = vAppTemplateApi.getOwner(vAppTemplateURI);
      
      checkOwner(owner);
      assertEquals(owner.getUser(), vAppTemplateApi.getVAppTemplate(vAppTemplateURI).getOwner().getUser());
   }
   
   @Test(description = "GET /vAppTemplate/{id}/customizationSection")
   public void testGetCustomizationSection() {
      CustomizationSection customizationSection = vAppTemplateApi.getCustomizationSection(vAppTemplateURI);
      
      checkCustomizationSection(customizationSection);
   }
   
   @Test(description = "GET /vAppTemplate/{id}/productSections")
   public void testGetProductSections() {
      ProductSectionList productSectionList = vAppTemplateApi.getProductSections(vAppTemplateURI);
      
      checkProductSectionList(productSectionList);
   }
   
   @Test(description = "PUT /vAppTemplate/{id}/productSections")
   public void testEditProductSections() {
      // TODO make a real modification
      
      ProductSectionList origSections = vAppTemplateApi.getProductSections(vApp.getHref());
      ProductSectionList newSections = origSections.toBuilder().build();
      
      Task task = vAppTemplateApi.modifyProductSections(vApp.getHref(), newSections);
      assertTaskSucceeds(task);

      ProductSectionList modified = vAppTemplateApi.getProductSections(vApp.getHref());
      checkProductSectionList(modified);
   }
   
   @Test(description = "GET /vAppTemplate/{id}/guestCustomizationSection")
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<URI, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(URI uri) {
            return vAppTemplateApi.getGuestCustomizationSection(uri);
         }
      });
   }
   
   @Test(description = "GET /vAppTemplate/{id}/leaseSettingsSection")
   public void testGetLeaseSettingsSection() {
      LeaseSettingsSection leaseSettingsSection = vAppTemplateApi.getLeaseSettingsSection(vAppTemplateURI);
      
      checkLeaseSettingsSection(leaseSettingsSection);
   }
   
   @Test(description = "GET /vAppTemplate/{id}/metadata", dependsOnMethods = { "testEditMetadataValue" })
   public void testGetVAppTemplateMetadata() {
      Metadata metadata = vAppTemplateApi.getMetadataApi().get(vAppTemplateURI);
      
      checkMetadata(metadata);
   }

   // implicitly tested by testEditVAppTemplateMetadataValue, which first creates the metadata entry; otherwise no entry may exist
   @Test(description = "GET /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testGetVAppTemplateMetadata" })
   public void testGetMetadataValue() {
      Metadata metadata = vAppTemplateApi.getMetadataApi().get(vAppTemplateURI);
      MetadataEntry entry = Iterables.get(metadata.getMetadataEntries(), 0);
      
      MetadataValue val = vAppTemplateApi.getMetadataApi().getValue(vAppTemplateURI, entry.getKey());
      
      checkMetadataValue(val);
      assertEquals(val.getValue(), entry.getValue());
   }
   
   @Test(description = "GET /vAppTemplate/{id}/networkConfigSection")
   public void testGetVAppTemplateNetworkConfigSection() {
      NetworkConfigSection networkConfigSection = vAppTemplateApi.getNetworkConfigSection(vAppTemplateURI);
      
      checkNetworkConfigSection(networkConfigSection);
   }
   
   @Test(description = "GET /vAppTemplate/{id}/networkConnectionSection")
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<URI, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(URI uri) {
            return vAppTemplateApi.getNetworkConnectionSection(uri);
         }
      });
   }

   @Test(description = "GET /vAppTemplate/{id}/networkSection")
   public void testGetVAppTemplateNetworkSection() {
      NetworkSection networkSection = vAppTemplateApi.getNetworkSection(vAppTemplateURI);

      checkOvfNetworkSection(networkSection);
   }

   @Test(description = "GET /vAppTemplate/{id}/ovf")
   public void testGetVAppTemplateOvf() {
      Envelope envelope = vAppTemplateApi.getOvf(vAppTemplateURI);
      
      checkOvfEnvelope(envelope);
   }

   @Test(description = "PUT /vAppTemplate/{id}")
   public void testEditVAppTemplate() {
      String name = name("myname-");
      String description = name("Description ");
      VAppTemplate template = VAppTemplate.builder()
               .name(name)
               .description(description)
               .build();
      
      final Task task = vAppTemplateApi.modifyVAppTemplate(vAppTemplateURI, template);
      assertTaskSucceeds(task);

      VAppTemplate newTemplate = vAppTemplateApi.getVAppTemplate(vAppTemplateURI);
      assertEquals(newTemplate.getName(), name);
      assertEquals(newTemplate.getDescription(), description);
   }

   @Test(description = "POST /vAppTemplate/{id}/metadata", dependsOnMethods = { "testGetVAppTemplate" })
   public void testEditMetadata() {
      Metadata oldMetadata = vAppTemplateApi.getMetadataApi().get(vAppTemplateURI);
      Map<String,String> oldMetadataMap = metadataToMap(oldMetadata);

      key = name("key-");
      val = name("value-");
      MetadataEntry metadataEntry = MetadataEntry.builder().entry(key, val).build();
      Metadata metadata = Metadata.builder().fromMetadata(oldMetadata).entry(metadataEntry).build();
      
      final Task task = vAppTemplateApi.getMetadataApi().merge(vAppTemplateURI, metadata);
      assertTaskSucceeds(task);

      Metadata newMetadata = vAppTemplateApi.getMetadataApi().get(vAppTemplateURI);
      Map<String,String> expectedMetadataMap = ImmutableMap.<String,String>builder()
               .putAll(oldMetadataMap)
               .put(key, val)
               .build();
      checkMetadataFor("vAppTemplate", newMetadata, expectedMetadataMap);
   }
   
   @Test(description = "PUT /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testEditMetadata" })
   public void testEditMetadataValue() {
      val = "new"+val;
      MetadataValue metadataValue = MetadataValue.builder().value(val).build();
      
      final Task task = vAppTemplateApi.getMetadataApi().putEntry(vAppTemplateURI, key, metadataValue);
      retryTaskSuccess.apply(task);

      MetadataValue newMetadataValue = vAppTemplateApi.getMetadataApi().getValue(vAppTemplateURI, key);
      assertEquals(newMetadataValue.getValue(), metadataValue.getValue());
   }

   @Test(description = "DELETE /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadataValue" })
   public void testDeleteVAppTemplateMetadataValue() {
      final Task deletionTask = vAppTemplateApi.getMetadataApi().deleteEntry(vAppTemplateURI, key);
      assertTaskSucceeds(deletionTask);

      Metadata newMetadata = vAppTemplateApi.getMetadataApi().get(vAppTemplateURI);
      checkMetadataKeyAbsentFor("vAppTemplate", newMetadata, key);
      key = null;
   }

   @Test(description = "PUT /vAppTemplate/{id}/guestCustomizationSection")
   public void testEditGuestCustomizationSection() {
      String computerName = name("n");
      GuestCustomizationSection newSection = GuestCustomizationSection.builder()
               .info("")
               .computerName(computerName)
               .build();
      
      final Task task = vAppTemplateApi.modifyGuestCustomizationSection(vm.getHref(), newSection);
      assertTaskSucceeds(task);

      GuestCustomizationSection modified = vAppTemplateApi.getGuestCustomizationSection(vm.getHref());
      
      checkGuestCustomizationSection(modified);
      assertEquals(modified.getComputerName(), computerName);
   }
   
   // NOTE vAppTemplate supports only storageLease (deployment lease applies to vApp too)
   @Test(description = "PUT /vAppTemplate/{id}/leaseSettingsSection")
   public void testEditLeaseSettingsSection() throws Exception {
      // NOTE use smallish number for storageLeaseInSeconds; it seems to be capped at 5184000?
      int storageLeaseInSeconds = random.nextInt(10000)+1;

      LeaseSettingsSection leaseSettingSection = LeaseSettingsSection.builder()
               .info("my info")
               .storageLeaseInSeconds(storageLeaseInSeconds)
               .build();
      
      final Task task = vAppTemplateApi.modifyLeaseSettingsSection(vAppTemplateURI, leaseSettingSection);
      assertTaskSucceeds(task);
      
      LeaseSettingsSection newLeaseSettingsSection = vAppTemplateApi.getLeaseSettingsSection(vAppTemplateURI);
      assertEquals(newLeaseSettingsSection.getStorageLeaseInSeconds(), (Integer) storageLeaseInSeconds);
   }

   @Test(description = "DELETE /vAppTemplate/{id}", dependsOnMethods = { "testGetVAppTemplate" }) 
   public void testDeleteVAppTemplate() throws Exception {
      VAppTemplate clonedVappTemplate = cloneVAppTemplate(true);

      // Confirm that "get" works pre-delete
      VAppTemplate vAppTemplatePreDelete = vAppTemplateApi.getVAppTemplate(clonedVappTemplate.getHref());
      checkVAppTemplate(vAppTemplatePreDelete);
      
      // Delete the template
      final Task task = vAppTemplateApi.deleteVappTemplate(clonedVappTemplate.getHref());
      assertTaskSucceeds(task);

      // Confirm that can't access post-delete, i.e. template has been deleted
      VAppTemplate deleted = vAppTemplateApi.getVAppTemplate(clonedVappTemplate.getHref());
      assertNull(deleted);
   }

   @Test(description = "POST /vAppTemplate/{id}/action/disableDownload")
   public void testDisableVAppTemplateDownload() throws Exception {
      vAppTemplateApi.disableDownload(vAppTemplateURI);
      
      // TODO Check that it really is disabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      VAppTemplate vAppTemplate = vAppTemplateApi.getVAppTemplate(vAppTemplateURI);
      Set<Link> links = vAppTemplate.getLinks();
      assertTrue(Iterables.all(Iterables.transform(links, rel), Predicates.not(Predicates.in(EnumSet.of(Link.Rel.DOWNLOAD_DEFAULT, Link.Rel.DOWNLOAD_ALTERNATE)))),
            "Should not offer download link after disabling download: "+vAppTemplate);
   }
   
   @Test(description = "POST /vAppTemplate/{id}/action/enableDownload")
   public void testEnableVAppTemplateDownload() throws Exception {
      // First disable so that enable really has some work to do...
      vAppTemplateApi.disableDownload(vAppTemplateURI);
      final Task task = vAppTemplateApi.enableDownload(vAppTemplateURI);
      assertTaskSucceeds(task);
      
      // TODO Check that it really is enabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      VAppTemplate vAppTemplate = vAppTemplateApi.getVAppTemplate(vAppTemplateURI);
      Set<Link> links = vAppTemplate.getLinks();
      assertTrue(Iterables.any(Iterables.transform(links, rel), Predicates.in(EnumSet.of(Link.Rel.DOWNLOAD_DEFAULT, Link.Rel.DOWNLOAD_ALTERNATE))),
            "Should offer download link after enabling download: "+vAppTemplate);
   }
   
   private Function<Link, Link.Rel> rel = new Function<Link, Link.Rel>() {
      @Override
      public Rel apply(Link input) {
         return input.getRel();
      }
   };
   
   @Test(description = "POST /vAppTemplate/{id}/action/consolidate")
   public void testConsolidateVAppTemplate() throws Exception {
      final Task task = vAppTemplateApi.consolidateVm(vm.getHref());
      assertTaskSucceedsLong(task);
      
      // TODO Need assertion that command had effect
   }
   
   // TODO How to obtain a datastore reference?
   @Test(description = "POST /vAppTemplate/{id}/action/relocate")
   public void testRelocateVAppTemplate() throws Exception {
      Reference dataStore = null; // FIXME
      RelocateParams relocateParams = RelocateParams.builder()
               .datastore(dataStore)
               .build();
      
      final Task task = vAppTemplateApi.relocateVm(vAppTemplateURI, relocateParams);
      assertTaskSucceedsLong(task);

      // TODO Need assertion that command had effect
   }
   
   // NOTE This will fail unless we can relocate a template to another datastore
   @Test(description = "GET /vAppTemplate/{id}/shadowVms", dependsOnMethods = { "testRelocateVAppTemplate" })
   public void testGetShadowVms() {
      References references = vAppTemplateApi.getShadowVms(vAppTemplateURI);
      
      Checks.checkReferences(references);
   }
   
   // This failed previously, but is passing now. 
   // However, it's not part of the official API so not necessary to assert it.
   @Test(description = "test completed task not included in vAppTemplate") 
   public void testCompletedTaskNotIncludedInVAppTemplate() throws Exception {
      // Kick off a task, and wait for it to complete
      vAppTemplateApi.disableDownload(vAppTemplateURI);
      final Task task = vAppTemplateApi.enableDownload(vAppTemplateURI);
      assertTaskDoneEventually(task);

      // Ask the VAppTemplate for its tasks, and the status of the matching task if it exists
      VAppTemplate vAppTemplate = vAppTemplateApi.getVAppTemplate(vAppTemplateURI);
      List<Task> tasks = vAppTemplate.getTasks();
      for (Task contender : tasks) {
         if (task.getId().equals(contender.getId())) {
            Task.Status status = contender.getStatus();
            if (EnumSet.of(Task.Status.QUEUED, Task.Status.PRE_RUNNING, Task.Status.RUNNING).contains(status)) {
               fail("Task "+contender+" reported complete, but is included in VAppTemplate in status "+status);
            }
         }
      }
   }
}
