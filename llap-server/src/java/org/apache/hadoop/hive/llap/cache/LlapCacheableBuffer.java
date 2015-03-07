/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.llap.cache;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hive.llap.DebugUtils;
import org.apache.hadoop.hive.llap.io.api.cache.LlapMemoryBuffer;
import org.apache.hadoop.hive.llap.io.api.impl.LlapIoImpl;
import org.apache.hadoop.hive.llap.metrics.LlapDaemonCacheMetrics;

import com.google.common.annotations.VisibleForTesting;
/**
 * Buffer that can be managed by LowLevelEvictionPolicy.
 */
public abstract class LlapCacheableBuffer {

  /** Priority for cache policy (should be pretty universal). */
  public double priority;
  /** Last priority update time for cache policy (should be pretty universal). */
  public long lastUpdate = -1;

  // TODO: remove some of these fields as needed?
  /** Linked list pointers for LRFU/LRU cache policies. Given that each block is in cache
   * that might be better than external linked list. Or not, since this is not concurrent. */
  public LlapCacheableBuffer prev = null;
  /** Linked list pointers for LRFU/LRU cache policies. Given that each block is in cache
   * that might be better than external linked list. Or not, since this is not concurrent. */
  public LlapCacheableBuffer next = null;
  /** Index in heap for LRFU/LFU cache policies. */
  protected static final int IN_LIST = -2;
  protected static final int NOT_IN_CACHE = -1;
  public int indexInHeap = NOT_IN_CACHE;

  protected abstract boolean invalidate();
  public abstract long getMemoryUsage();
  public abstract void notifyEvicted(EvictionDispatcher evictionDispatcher);

  @Override
  public String toString() {
    return "0x" + Integer.toHexString(System.identityHashCode(this));
  }

  public String toStringForCache() {
    return "[" + Integer.toHexString(hashCode()) + " " + String.format("%1$.2f", priority) + " "
        + lastUpdate + " " + (isLocked() ? "!" : ".") + "]";
  }

  protected abstract boolean isLocked();
}
