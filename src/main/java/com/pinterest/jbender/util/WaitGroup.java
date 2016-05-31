/*
Copyright 2014 Pinterest.com
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.pinterest.jbender.util;

import java.util.concurrent.atomic.AtomicLong;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

/**
 * Simplified phaser supporting up to {@code Long.MAX_VALUE} fibers.
 */
public class WaitGroup {
  private volatile Strand waiter;
  private AtomicLong running;

  public WaitGroup() {
    running = new AtomicLong();
    waiter = null;
  }

  public void add() {
    running.incrementAndGet();
  }

  public void done() {
    long count = running.decrementAndGet();
    if (count == 0 && waiter != null) {
      waiter.unpark();
    }
  }

  public void await() throws SuspendExecution {
    waiter = Strand.currentStrand();
    while (running.get() > 0) {
      Strand.park();
    }
  }
}
