public class Problem2 {
    
    public void sumValues(double[] arr) {
        // UCID: ao453
        // Date: 11/30/2025
        // Summary: Solution for Task #1 - sum array values and format to 2 decimal places
        // Plan:
        // 1. Initialize total variable to 0
        // 2. Loop through array and add each value to total
        // 3. Format total to exactly 2 decimal places using String.format
        // 4. Print the formatted result
        
        double total = 0.0; // Challenge 1: variable to hold sum
        
        // Loop through array and sum all values
        for (double n : arr) {
            total += n; // add each value to total
        }
        
        // Challenge 2: Format to exactly 2 decimal places
        String formatted = String.format("%.2f", total);
        System.out.println(formatted);
    }
    
    public static void main(String[] args) {
        Problem2 demo = new Problem2();
        
        // Test cases
        double[] test1 = {1.0, 2.0, 3.0}; // should print 6.00
        double[] test2 = {0.1, 0.2, 0.3}; // should print 0.60
        double[] test3 = {5.5, 2.25, 1.75}; // should print 9.50
        double[] test4 = {1}; // should print 1.00
        
        System.out.print("Test 1 (expected 6.00): ");
        demo.sumValues(test1);
        
        System.out.print("Test 2 (expected 0.60): ");
        demo.sumValues(test2);
        
        System.out.print("Test 3 (expected 9.50): ");
        demo.sumValues(test3);
        
        System.out.print("Test 4 (expected 1.00): ");
        demo.sumValues(test4);
    }
}
