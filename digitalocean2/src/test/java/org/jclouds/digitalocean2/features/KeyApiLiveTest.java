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

import java.util.List;

import org.jclouds.digitalocean2.domain.Key;
import org.jclouds.digitalocean2.internal.BaseDigitalOcean2LiveTest;
import org.testng.annotations.Test;

public class KeyApiLiveTest extends BaseDigitalOcean2LiveTest {

   private KeyApi api() {
      return api.getKeyApi();
   }
   private int keyId;
   private String fingerprint;

   @Test(groups = "live", dependsOnMethods = "testRetrieveKey")
   public void testListKeys() {

      List<Key> keys = api().listKeys(1000);
      assertEquals(keys.size(), 2);
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testRetrieveKey() {

      Key key = api().getKey(keyId);
      assertEquals(key.fingerprint(), fingerprint);
   }

   @Test(groups = "live")
   public void testCreate() {
      Key key = api().create("test-key", "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDtUlMVNzUQY7WK8nhweUwmkgHCgsiL/HwHckllWln73UIQ0p1Fw+IkFgX9l5+SLZ5TJXgw4PZ5b5BESifLIiThj6N1zKtOeFQZYuCnajNHsrxyhceLNXGo+wNcqUiwwzUJmF2j16Mbj0imV8JY23L31ez4q92wXUg0JdwNs+DXJBbcT9af0GRQDNkpuwRpiA1A2XfnacxoixPzcBhZKrTC8VmRXZuYfV7u92EHWqBV6YASmybhSOv1NqBuUmsHp2w7Vf7O+5ZX21x2yjJoNTZQqhy4+imMqCbsWW30JimTVl5U+W+RcaIY2PhbxQrPNZsHNIKh1A3U/14OWff2l1hb someone@somewhere.com");
      keyId = key.id();
      fingerprint = key.fingerprint();

   }

   @Test(groups = "live", dependsOnMethods = "testListKeys")
   public void testDeleteKey() {
      api().deleteKey(keyId);

   }

}
