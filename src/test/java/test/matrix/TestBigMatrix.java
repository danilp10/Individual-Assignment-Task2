package test.matrix;

import org.example.MatrixLoader;
import org.example.SparseMatrixCSRMul;
import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class TestBigMatrix {

    public static void main(String[] args) {
        try {
            // Cargar la matriz desde el archivo en formato .mtx
            SparseMatrixCSRMul.CSRMatrix matrix = MatrixLoader.loadFromMTX("mc2depi.mtx");

            // Medir el uso de memoria antes de la multiplicación
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage beforeMemory = memoryBean.getHeapMemoryUsage();
            long beforeUsedMemory = beforeMemory.getUsed();

            // Obtener el uso de CPU antes de la ejecución
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuBefore = osBean.getProcessCpuLoad() * 100;

            // Medir el tiempo de ejecución de la multiplicación
            long startTime = System.nanoTime();
            SparseMatrixCSRMul.CSRMatrix result = matrix.multiply(matrix);
            long endTime = System.nanoTime();

            // Medir el uso de memoria después de la multiplicación
            MemoryUsage afterMemory = memoryBean.getHeapMemoryUsage();
            long afterUsedMemory = afterMemory.getUsed();
            long memoryUsed = afterUsedMemory - beforeUsedMemory;

            // Obtener el uso de CPU después de la ejecución
            double cpuAfter = osBean.getProcessCpuLoad() * 100;

            // Calcular y mostrar resultados
            double executionTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
            System.out.println("Tiempo de ejecución: " + executionTimeInSeconds + " segundos");
            System.out.println("Memoria utilizada: " + memoryUsed / (1024 * 1024) + " MB");
            System.out.println("CPU antes de ejecución: " + String.format("%.2f", cpuBefore) + " %");
            System.out.println("CPU después de ejecución: " + String.format("%.2f", cpuAfter) + " %");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
