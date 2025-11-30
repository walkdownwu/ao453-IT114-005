public class Problem1 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        boolean first = true;
        for (int n : arr) {
            if (n % 2 != 0) {
                if (!first) System.out.print(", ");
                System.out.print(n);
                first = false;
            }
        }
        System.out.println();
    }
}
