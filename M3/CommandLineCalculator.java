package M3;

public class CommandLineCalculator {
    
    public static void main(String[] args) {
        // UCID: ao453
        // Date: 12/01/2025
        // Summary: Command-line calculator that accepts two numbers and an operator
        // Handles both integers and floating-point numbers with proper decimal formatting
        
        // Challenge 1: Check if we have exactly 3 arguments (number operator number)
        if (args.length != 3) {
            System.out.println("Error: Please provide exactly 3 arguments (number operator number)");
            return;
        }
        
        try {
            // Parse the first number
            double num1 = Double.parseDouble(args[0]);
            
            // Get the operator
            String operator = args[1];
            
            // Parse the second number
            double num2 = Double.parseDouble(args[2]);
            
            // Perform the calculation
            double result = 0;
            boolean validOperation = true;
            
            if (operator.equals("+")) {
                result = num1 + num2;
            } else if (operator.equals("-")) {
                result = num1 - num2;
            } else {
                System.out.println("Error: Unsupported operator '" + operator + "'. Only + and - are supported.");
                validOperation = false;
            }
            
            // Challenge 2: Display result with appropriate decimal places
            if (validOperation) {
                // Determine max decimal places from inputs
                int decimals1 = getDecimalPlaces(args[0]);
                int decimals2 = getDecimalPlaces(args[2]);
                int maxDecimals = Math.max(decimals1, decimals2);
                
                // Format and display result
                if (maxDecimals == 0) {
                    // Both are integers, show as integer
                    System.out.println((int)result);
                } else {
                    // Show with appropriate decimal places
                    String format = "%." + maxDecimals + "f";
                    System.out.println(String.format(format, result));
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format. Please provide valid numbers.");
        }
    }
    
    // Helper method to count decimal places in a number string
    private static int getDecimalPlaces(String number) {
        if (number.contains(".")) {
            String[] parts = number.split("\\.");
            if (parts.length == 2) {
                return parts[1].length();
            }
        }
        return 0;
    }
}
