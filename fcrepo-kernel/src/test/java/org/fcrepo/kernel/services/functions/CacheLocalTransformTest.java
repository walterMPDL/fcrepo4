/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.kernel.services.functions;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Field;

import org.fcrepo.kernel.services.functions.CacheLocalTransform;
import org.fcrepo.kernel.services.functions.GetCacheStore;
import org.fcrepo.kernel.utils.LowLevelCacheEntry;
import org.fcrepo.kernel.utils.impl.CacheStoreEntry;
import org.infinispan.Cache;
import org.infinispan.CacheImpl;
import org.infinispan.loaders.CacheStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.modeshape.jcr.value.BinaryKey;

import com.google.common.base.Function;

public class CacheLocalTransformTest {

    @Mock
    private GetCacheStore cacheExtractor;

    @Mock
    private CacheStore mockCacheStore;

    @Mock
    private Function<LowLevelCacheEntry, Object> mockTransform;

    @Mock
    private CacheImpl<Object, Object> mockCache;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testTransform() throws Exception {

        final Field field =
                CacheLocalTransform.class.getDeclaredField("transform");
        field.setAccessible(true);
        field.set(CacheLocalTransform.class, cacheExtractor);
        when(cacheExtractor.apply(any(Cache.class))).thenReturn(mockCacheStore);
        final BinaryKey key = new BinaryKey("key-123");
        final CacheLocalTransform<Object, Object, Object> testObj =
                new CacheLocalTransform<>(key, mockTransform);
        when(mockCache.getName()).thenReturn("foo");
        testObj.setEnvironment(mockCache, null);

        testObj.call();

        verify(mockTransform).apply(
                eq(new CacheStoreEntry(mockCacheStore, "foo", key)));
    }
}
