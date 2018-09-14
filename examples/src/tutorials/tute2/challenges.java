package tutorials.tute2;

public class challenges {

    public static void main(String[] args) {
        int[] a = {1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        int n = a.length;
        int v = 30;

        quadraticImplementation(a, n, v);
        linearLogImplementation(a, n, v);
    }

    public static boolean quadraticImplementation(int[] A, int n, int v) {
        for(int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (A[i] + A[j] == v) {
                    System.out.println("True for (" + A[i] + ", " + A[j] + ")");
                    return true;
                }
            }
        }
        System.out.println("False");
        return false;
    }

    public static boolean linearLogImplementation(int[] A, int n, int v) {
        for(int i = 0; i < n; i++) {
            //Binary Search for possible second index j
            int leftBound = 0;
            int rightBound = n - 1;
            int j, attempt;

            while(leftBound + 1 < rightBound) {
                j = (leftBound + rightBound) / 2;
                attempt = A[i] + A[j];

                System.out.println("(" + i + ", " + leftBound + ", " + j + ", " + rightBound + ")");
                if (attempt > v) {
                    rightBound = j;
                } else if (attempt < v) {
                    leftBound = j;
                } else {
                    System.out.println("True for (" + A[i] + ", " + A[j] + ")");
                    return true;
                }
            }
        }
        System.out.println("False");
        return false;
    }

    public static boolean linearImplementation(int[] A, int n, int v) {
        int leftIndex = 0;
        int rightIndex = 0;
        int attempt;

        while ((rightIndex < n) && (leftIndex < n)) {
            attempt = A[leftIndex] + A[rightIndex];
            if(attempt > v) {
                leftIndex++;
            } else if(attempt < v) {
                rightIndex++;
            } else {
                System.out.println("True for (" + leftIndex + ", " + rightIndex + ")");
                return true;
            }
        }
        System.out.println("False");
        return false;
    }
}