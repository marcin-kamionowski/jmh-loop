/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@SuppressWarnings("ALL")
public class MyBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        public List<Integer> testList;
        public int[] testArray;

        @Setup(Level.Trial)
        public void setUp() {
            testList = new Random()
                .ints()
                .limit(1002)
                .boxed()
                .collect(Collectors.toList());

            testArray = testList.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testStopConditionWithMethodCall(Blackhole blackhole, BenchmarkState state) {
        var v = state.testList;
        for (int i = 0; i < v.size(); i++) {
            blackhole.consume(v.get(i));
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testStopConditionWithLocalVar(Blackhole blackhole, BenchmarkState state) {
        var v = state.testList;
        var stop = v.size();
        for (int i = 0; i < stop; i++) {
            blackhole.consume(v.get(i));
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testLoopUnrollingBaseline(Blackhole blackhole, BenchmarkState state) {
        var j = 0;
        for (int i = 0; i < 15; i++) {
            j += 10;
        }
        blackhole.consume(j);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testLoopUnrolling(Blackhole blackhole, BenchmarkState state) {
        var j = 0;
        for (int i = 0; i < 5; i++) {
            j += 10;
            j += 10;
            j += 10;
        }
        blackhole.consume(j);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testLoopUnrollingSumListBaseline(Blackhole blackhole, BenchmarkState state) {
        var j = 0;
        var v = state.testList;
        var size = v.size();
        for (int i = 0; i < size; i++) {
            j += v.get(i);
        }
        blackhole.consume(j);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testLoopUnrollingSumList(Blackhole blackhole, BenchmarkState state) {
        var j = 0;
        var v = state.testList;
        var size = v.size() / 3;
        for (int i = 0; i < size; i += 5) {
            j += v.get(i);
            j += v.get(i + 1);
            j += v.get(i + 2);
            j += v.get(i + 3);
            j += v.get(i + 4);
        }
        blackhole.consume(j);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testCountingDown(Blackhole blackhole, BenchmarkState state) {
        var v = state.testList;
        for (int i = v.size() - 1; i >= 0; i--) {
            blackhole.consume(v.get(i));
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testArrayLoop(Blackhole blackhole, BenchmarkState state) {
        var v = state.testArray;
        for (int i = 0; i < v.length; i++) {
            blackhole.consume(v[i]);
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testArrayLoopExc(Blackhole blackhole, BenchmarkState state) {
        var v = state.testArray;
        try {
            for (int i = 0; ; i++) {
                blackhole.consume(v[i]);
            }
        } catch (ArrayIndexOutOfBoundsException exc) {

        }
    }

}
