package com.example.strassenmatrix;

public class Strassen {

    public static int[][] multiplyHybrid(int[][] A, int[][] B, int threshold) {

        int n = A.length;

        if (n <= threshold) return normal(A, B);

        if (n == 1)
            return new int[][]{{A[0][0] * B[0][0]}};

        int size = n / 2;

        int[][] A11 = new int[size][size];
        int[][] A12 = new int[size][size];
        int[][] A21 = new int[size][size];
        int[][] A22 = new int[size][size];

        int[][] B11 = new int[size][size];
        int[][] B12 = new int[size][size];
        int[][] B21 = new int[size][size];
        int[][] B22 = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                A11[i][j] = A[i][j];
                A12[i][j] = A[i][j + size];
                A21[i][j] = A[i + size][j];
                A22[i][j] = A[i + size][j + size];

                B11[i][j] = B[i][j];
                B12[i][j] = B[i][j + size];
                B21[i][j] = B[i + size][j];
                B22[i][j] = B[i + size][j + size];
            }

        int[][] M1 = multiplyHybrid(add(A11, A22), add(B11, B22), threshold);
        int[][] M2 = multiplyHybrid(add(A21, A22), B11, threshold);
        int[][] M3 = multiplyHybrid(A11, subtract(B12, B22), threshold);
        int[][] M4 = multiplyHybrid(A22, subtract(B21, B11), threshold);
        int[][] M5 = multiplyHybrid(add(A11, A12), B22, threshold);
        int[][] M6 = multiplyHybrid(subtract(A21, A11), add(B11, B12), threshold);
        int[][] M7 = multiplyHybrid(subtract(A12, A22), add(B21, B22), threshold);

        int[][] C = new int[n][n];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {

                C[i][j] = M1[i][j] + M4[i][j] - M5[i][j] + M7[i][j];
                C[i][j + size] = M3[i][j] + M5[i][j];
                C[i + size][j] = M2[i][j] + M4[i][j];
                C[i + size][j + size] = M1[i][j] - M2[i][j] + M3[i][j] + M6[i][j];
            }

        return C;
    }

    private static int[][] normal(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];

        return C;
    }

    private static int[][] add(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];

        return C;
    }

    private static int[][] subtract(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];

        return C;
    }
}