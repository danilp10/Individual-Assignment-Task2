package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparseMatrixCSRMul {

    public static class CSRMatrix {
        double[] values;
        int[] columnIndices;
        int[] rowPointers;

        int rows, cols;

        CSRMatrix(double[] values, int[] columnIndices, int[] rowPointers, int rows, int cols) {
            this.values = values;
            this.columnIndices = columnIndices;
            this.rowPointers = rowPointers;
            this.rows = rows;
            this.cols = cols;
        }

        public void printCSRDetails() {
            System.out.println("CSR Representation:");
            System.out.println("Values: " + Arrays.toString(values));
            System.out.println("Column Indices: " + Arrays.toString(columnIndices));
            System.out.println("Row Pointers: " + Arrays.toString(rowPointers));
        }

        public void printDenseMatrix() {
            double[][] denseMatrix = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++) {
                    denseMatrix[i][columnIndices[j]] = values[j];
                }
            }

            System.out.println("Dense Matrix:");
            for (double[] row : denseMatrix) {
                System.out.println(Arrays.toString(row));
            }
        }

        public CSRMatrix multiply(CSRMatrix B) {
            if (this.cols != B.rows) {
                throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
            }

            List<Double> resultValues = new ArrayList<>();
            List<Integer> resultColumnIndices = new ArrayList<>();
            List<Integer> resultRowPointers = new ArrayList<>();
            resultRowPointers.add(0);

            double[] rowResult = new double[B.cols];

            for (int i = 0; i < this.rows; i++) {
                Arrays.fill(rowResult, 0.0);

                for (int j = this.rowPointers[i]; j < this.rowPointers[i + 1]; j++) {
                    int colA = this.columnIndices[j];
                    double valA = this.values[j];

                    for (int k = B.rowPointers[colA]; k < B.rowPointers[colA + 1]; k++) {
                        int colB = B.columnIndices[k];
                        double valB = B.values[k];
                        rowResult[colB] += valA * valB;
                    }
                }

                int nonZeroCount = 0;
                for (int j = 0; j < B.cols; j++) {
                    if (rowResult[j] != 0.0) {
                        resultValues.add(rowResult[j]);
                        resultColumnIndices.add(j);
                        nonZeroCount++;
                    }
                }

                resultRowPointers.add(resultRowPointers.get(resultRowPointers.size() - 1) + nonZeroCount);
            }

            double[] resultValuesArray = resultValues.stream().mapToDouble(Double::doubleValue).toArray();
            int[] resultColumnIndicesArray = resultColumnIndices.stream().mapToInt(Integer::intValue).toArray();
            int[] resultRowPointersArray = resultRowPointers.stream().mapToInt(Integer::intValue).toArray();

            return new CSRMatrix(resultValuesArray, resultColumnIndicesArray, resultRowPointersArray, this.rows, B.cols);
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CSR Matrix:\n");
            sb.append("Values: ");
            for (double value : values) {
                sb.append(value).append(" ");
            }
            sb.append("\nColumn Indices: ");
            for (int col : columnIndices) {
                sb.append(col).append(" ");
            }
            sb.append("\nRow Pointers: ");
            for (int rowPtr : rowPointers) {
                sb.append(rowPtr).append(" ");
            }
            return sb.toString();
        }

    }

    public static CSRMatrix convertToCSR(double[][] matrix) {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> columnIndicesList = new ArrayList<>();
        List<Integer> rowPointersList = new ArrayList<>();

        int rows = matrix.length;
        int cols = matrix[0].length;

        rowPointersList.add(0);

        for (int i = 0; i < rows; i++) {
            int nonZeroCount = 0;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    valuesList.add(matrix[i][j]);
                    columnIndicesList.add(j);
                    nonZeroCount++;
                }
            }
            rowPointersList.add(rowPointersList.get(rowPointersList.size() - 1) + nonZeroCount);
        }

        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] columnIndices = columnIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] rowPointers = rowPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new CSRMatrix(values, columnIndices, rowPointers, rows, cols);
    }
}
