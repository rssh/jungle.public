/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2008 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Java - a JSON-RPC to Java Bridge with dynamic invocation
 *
 * Copyright Metaparadigm Pte. Ltd. 2004.
 * Michael Clark <michael@metaparadigm.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jabsorb.client;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jabsorb.JSONSerializer;
import org.jabsorb.reflect.ClassAnalyzer;
import org.jabsorb.serializer.AccessibleObjectResolver;
import org.jabsorb.serializer.Serializer;
import org.jabsorb.test.ConstructorTest;
import org.json.JSONArray;

import junit.framework.TestCase;

public class AccessibleObjectResolverTestCase extends TestCase {
  AccessibleObjectResolver resolver;
  Map methodMap;
  JSONSerializer serializer;

  protected void setUp() throws Exception {
    resolver= new AccessibleObjectResolver();
    methodMap = new HashMap();
        methodMap.putAll(ClassAnalyzer.getClassData(ConstructorTest.class).getMethodMap());
        methodMap.putAll(ClassAnalyzer.getClassData(ConstructorTest.class).getConstructorMap());
        serializer = new JSONSerializer();
        serializer.registerDefaultSerializers();
  }

  public void testResolution() {
    JSONArray args= new JSONArray();
    args.put(1);
    Constructor methodInt= (Constructor)resolver.resolveMethod(methodMap, "$constructor", args, serializer);
    Class[] params= methodInt.getParameterTypes();
    assertNotNull(params);
    assertEquals(1, params.length);
    assertEquals(Integer.TYPE, params[0]);
  }
}
