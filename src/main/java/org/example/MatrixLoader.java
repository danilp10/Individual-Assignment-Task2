package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatrixLoader {

    // Cambia el método para devolver un objeto CSRMatrix en lugar de MatrixLoader
    public static SparseMatrixCSRMul.CSRMatrix loadFromMTX(String filePath) throws IOException {
        List<Integer> rowIndices = new ArrayList<>();
        List<Integer> colIndices = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        int numRows = 0, numCols = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean dimensionsRead = false;

            while ((line = br.readLine()) != null) {
                // Omitir comentarios
                if (line.startsWith("%")) continue;
                String[] parts = line.trim().split("\\s+");

                if (!dimensionsRead && parts.length == 3) {
                    // Leer dimensiones en la primera línea sin comentarios
                    numRows = Integer.parseInt(parts[0]);
                    numCols = Integer.parseInt(parts[1]);
                    dimensionsRead = true;  // Evitar relectura de dimensiones
                } else if (parts.length == 3) {
                    // Leer datos (row, col, value)
                    int row = Integer.parseInt(parts[0]) - 1;  // Ajustar índices a base 0
                    int col = Integer.parseInt(parts[1]) - 1;
                    double value = Double.parseDouble(parts[2]);
                    rowIndices.add(row);
                    colIndices.add(col);
                    values.add(value);
                }
            }
        }

        // Convertir listas a arrays
        int[] rowIndicesArray = rowIndices.stream().mapToInt(i -> i).toArray();
        int[] colIndicesArray = colIndices.stream().mapToInt(i -> i).toArray();
        double[] valuesArray = values.stream().mapToDouble(d -> d).toArray();

        // Retornar el objeto CSRMatrix con los datos cargados
        return new SparseMatrixCSRMul.CSRMatrix(valuesArray, colIndicesArray, rowIndicesArray, numRows, numCols);
    }
}
