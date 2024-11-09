package test.matrix;

import org.example.SparseMatrixCSCMul;
import org.openjdk.jmh.annotations.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.sun.management.OperatingSystemMXBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static org.example.SparseMatrixCSCMul.CSCMatrix;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class TestSparseMatrixMultiplicationCSC {

    @Param({"10", "100", "250", "500", "1000", "2000"})
    private int size;

    @Param({"0", "0.5", "0.8", "0.9"})
    private double sparsityLevel;

    private CSCMatrix cscA;
    private CSCMatrix cscB;

    private Map<String, Result> results = new HashMap<>();

    @Setup(Level.Trial)
    public void setup() {
        double[][] A = new double[size][size];
        double[][] B = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Math.random() > sparsityLevel) {
                    A[i][j] = Math.random();
                    B[i][j] = Math.random();
                }
            }
        }

        cscA = SparseMatrixCSCMul.convertToCSC(A);
        cscB = SparseMatrixCSCMul.convertToCSC(B);
    }

    @Benchmark
    public void benchmarkSparseMatrixMult() {
        measure("CSC Matrix Multiplication", () -> cscA.multiply(cscB));
    }

    private void measure(String algorithmName, Runnable matrixMultiplicationAlgorithm) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMemory = memoryBean.getHeapMemoryUsage();
        long beforeUsedMemory = beforeMemory.getUsed();

        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuBefore = osBean.getProcessCpuLoad() * 100;

        long startTime = System.nanoTime();
        matrixMultiplicationAlgorithm.run();
        long endTime = System.nanoTime();

        MemoryUsage afterMemory = memoryBean.getHeapMemoryUsage();
        long afterUsedMemory = afterMemory.getUsed();
        long memoryUsed = afterUsedMemory - beforeUsedMemory;

        double executionTime = (endTime - startTime) / 1e6;
        double cpuAfter = osBean.getProcessCpuLoad() * 100;

        results.put(algorithmName, new Result(executionTime, memoryUsed, cpuBefore, cpuAfter));
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        for (Map.Entry<String, Result> entry : results.entrySet()) {
            printResults(size, entry.getKey(), entry.getValue());
        }
    }

    private static void printResults(int size, String algorithmName, Result result) {
        System.out.println("\n\nAlgorithm: " + algorithmName);
        System.out.println("Matrix size " + size + "x" + size);
        System.out.println("Execution time: " + result.executionTime + " ms");
        System.out.println("Memory used: " + Math.abs(result.memoryUsed) / (1024 * 1024) + " MB");
        System.out.println("CPU usage before: " + String.format("%.2f", result.cpuBefore) + " %");
        System.out.println("CPU usage after: " + String.format("%.2f", result.cpuAfter) + " %\n");
    }

    private static class Result {
        double executionTime;
        long memoryUsed;
        double cpuBefore;
        double cpuAfter;

        Result(double executionTime, long memoryUsed, double cpuBefore, double cpuAfter) {
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
            this.cpuBefore = cpuBefore;
            this.cpuAfter = cpuAfter;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestSparseMatrixMultiplicationCSC.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
