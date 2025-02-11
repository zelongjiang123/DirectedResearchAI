public class MathUtils {
     /**
     * transpose a given matrix
     * @param matrix
     * @return
     */
    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows]; // Swap dimensions

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j]; // Swap elements
            }
        }
        return transposed;
    }


    /**
     * returns the result of A * B
     * 
     * @param A
     * @param B
     * @return
     */
    public static double[][] matrixMultiplication(double[][] A, double[][] B) {
        int m = A.length; // Rows in A
        int n = A[0].length; // Columns in A (must match rows in B)
        int p = B[0].length; // Columns in B

        if (B.length != n) {
            throw new IllegalArgumentException("Invalid matrix dimensions for multiplication.");
        }

        double[][] C = new double[m][p]; // Result matrix

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                C[i][j] = 0.0; // Initialize cell
                for (int k = 0; k < n; k++) {
                    // if(A[i][k] == Double.NEGATIVE_INFINITY || B[k][j] == Double.NEGATIVE_INFINITY){
                    //     C[i][j] = Double.NEGATIVE_INFINITY;
                    //     break;
                    // }
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }
}
