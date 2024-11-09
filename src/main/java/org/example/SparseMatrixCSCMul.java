package org.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparseMatrixCSCMul {

    public static class CSCMatrix {
        double[] values;
        int[] rowIndices;
        int[] colPointers;

        int rows, cols;

        CSCMatrix(double[] values, int[] rowIndices, int[] colPointers, int rows, int cols) {
            this.values = values;
            this.rowIndices = rowIndices;
            this.colPointers = colPointers;
            this.rows = rows;
            this.cols = cols;
        }

        public void printCSCDetails() {
            System.out.println("CSC Representation:");
            System.out.println("Values: " + Arrays.toString(values));
            System.out.println("Row Indices: " + Arrays.toString(rowIndices));
            System.out.println("Column Pointers: " + Arrays.toString(colPointers));
        }

        public void printDenseMatrix() {
            double[][] denseMatrix = new double[rows][cols];

            for (int j = 0; j < cols; j++) {
                for (int i = colPointers[j]; i < colPointers[j + 1]; i++) {
                    denseMatrix[rowIndices[i]][j] = values[i];
                }
            }

            System.out.println("Dense Matrix:");
            for (double[] row : denseMatrix) {
                System.out.println(Arrays.toString(row));
            }
        }

        public CSCMatrix multiply(CSCMatrix B) {
            if (this.cols != B.rows) {
                throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
            }

            List<Double> resultValues = new ArrayList<>();
            List<Integer> resultRowIndices = new ArrayList<>();
            List<Integer> resultColPointers = new ArrayList<>();
            resultColPointers.add(0);

            double[] colResult = new double[this.rows];

            for (int jB = 0; jB < B.cols; jB++) {
                Arrays.fill(colResult, 0.0);

                for (int k = B.colPointers[jB]; k < B.colPointers[jB + 1]; k++) {
                    int rowB = B.rowIndices[k];
                    double valB = B.values[k];

                    for (int i = this.colPointers[rowB]; i < this.colPointers[rowB + 1]; i++) {
                        int rowA = this.rowIndices[i];
                        double valA = this.values[i];
                        colResult[rowA] += valA * valB;
                    }
                }

                int nonZeroCount = 0;
                for (int i = 0; i < this.rows; i++) {
                    if (colResult[i] != 0.0) {
                        resultValues.add(colResult[i]);
                        resultRowIndices.add(i);
                        nonZeroCount++;
                    }
                }

                resultColPointers.add(resultColPointers.get(resultColPointers.size() - 1) + nonZeroCount);
            }

            double[] resultValuesArray = resultValues.stream().mapToDouble(Double::doubleValue).toArray();
            int[] resultRowIndicesArray = resultRowIndices.stream().mapToInt(Integer::intValue).toArray();
            int[] resultColPointersArray = resultColPointers.stream().mapToInt(Integer::intValue).toArray();

            return new CSCMatrix(resultValuesArray, resultRowIndicesArray, resultColPointersArray, this.rows, B.cols);
        }
    }

    public static CSCMatrix convertToCSC(double[][] matrix) {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> rowIndicesList = new ArrayList<>();
        List<Integer> colPointersList = new ArrayList<>();

        int rows = matrix.length;
        int cols = matrix[0].length;

        colPointersList.add(0);

        for (int j = 0; j < cols; j++) {
            int nonZeroCount = 0;
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] != 0) {
                    valuesList.add(matrix[i][j]);
                    rowIndicesList.add(i);
                    nonZeroCount++;
                }
            }
            colPointersList.add(colPointersList.get(colPointersList.size() - 1) + nonZeroCount);
        }

        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] rowIndices = rowIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] colPointers = colPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new CSCMatrix(values, rowIndices, colPointers, rows, cols);
    }
}
