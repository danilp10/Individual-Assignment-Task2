package org.example;

public class LoopMatrixMultiplication {
    public static double[][] matrixMultiplyOptimized(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                double r = A[i][k];
                for (int j = 0; j < n; j += 4) {
                    C[i][j] += r * B[k][j];
                    if (j + 1 < n) C[i][j + 1] += r * B[k][j + 1];
                    if (j + 2 < n) C[i][j + 2] += r * B[k][j + 2];
                    if (j + 3 < n) C[i][j + 3] += r * B[k][j + 3];
                }
            }
        }
        return C;
    }
}

