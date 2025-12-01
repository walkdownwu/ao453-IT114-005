public class Problem3 {
    
    public static int[] bePositive(int[] arr) {
        // UCID: ao453
        // Date: 12/01/2025
        // Summary: Solution for bePositive - convert all values to positive
        // Plan:
        // 1. Create output array with same length as input array
        // 2. Loop through input array
        // 3. Make each value positive using Math.abs()
        // 4. Convert back to int (already int type)
        // 5. Assign to corresponding index in output array
        // 6. Return the output array
        
        int[] output = new int[arr.length]; // Create output array
        
        for (int i = 0; i < arr.length; i++) {
            // Challenge 1: Make value positive using Math.abs()
            int positiveValue = Math.abs(arr[i]);
            
            // Challenge 2: Convert back to original type (int) and assign to output
            output[i] = positiveValue;
        }
        
        return output;
    }
    
    public static void main(String[] args) {
        Problem3 demo = new Problem3();
        
        // Test cases
        int[] test1 = {-1, -2, -3, -4};
        int[] test2 = {1, -2, 3, -4, 5};
        int[] test3 = {-10, 0, 10};
        int[] test4 = {5, 10, 15};
        
        System.out.println("Test 1: [-1, -2, -3, -4]");
        int[] result1 = bePositive(test1);
        printArray(result1);
        
        System.out.println("\nTest 2: [1, -2, 3, -4, 5]");
        int[] result2 = bePositive(test2);
        printArray(result2);
        
        System.out.println("\nTest 3: [-10, 0, 10]");
        int[] result3 = bePositive(test3);
        printArray(result3);
        
        System.out.println("\nTest 4: [5, 10, 15] (already positive)");
        int[] result4 = bePositive(test4);
        printArray(result4);
    }
    
    // Helper method to print arrays
    public static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
