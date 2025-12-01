public class Problem4 {
    
    public static String transformText(String text) {
        // UCID: ao453
        // Date: 12/01/2025
        // Summary: Solution for transformText - clean and format text
        // Plan:
        // 1. Remove non-alphanumeric characters except spaces using regex
        // 2. Convert text to Title Case (first letter of each word uppercase)
        // 3. Trim leading/trailing spaces and remove duplicate spaces
        // 4. Assign final result to placeholderForModifiedPhrase
        // 5. Return the modified phrase
        
        String placeholderForModifiedPhrase = text;
        
        // Challenge 1: Remove non-alphanumeric characters except spaces
        placeholderForModifiedPhrase = placeholderForModifiedPhrase.replaceAll("[^a-zA-Z0-9 ]", "");
        
        // Challenge 2: Convert to Title Case
        String[] words = placeholderForModifiedPhrase.split(" ");
        StringBuilder titleCase = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                // Capitalize first letter, lowercase the rest
                String capitalized = word.substring(0, 1).toUpperCase() + 
                                   word.substring(1).toLowerCase();
                titleCase.append(capitalized).append(" ");
            }
        }
        
        placeholderForModifiedPhrase = titleCase.toString();
        
        // Challenge 3: Trim and remove duplicate spaces
        placeholderForModifiedPhrase = placeholderForModifiedPhrase.trim();
        placeholderForModifiedPhrase = placeholderForModifiedPhrase.replaceAll("\\s+", " ");
        
        return placeholderForModifiedPhrase;
    }
    
    public static void main(String[] args) {
        // Test cases
        String test1 = "hello@world!  this  is#a$test";
        String test2 = "  MULTIPLE   SPACES   HERE  ";
        String test3 = "mix3d_numb3rs&symbols!!!";
        String test4 = "already Title Case";
        
        System.out.println("Test 1: \"" + test1 + "\"");
        System.out.println("Result: \"" + transformText(test1) + "\"");
        System.out.println();
        
        System.out.println("Test 2: \"" + test2 + "\"");
        System.out.println("Result: \"" + transformText(test2) + "\"");
        System.out.println();
        
        System.out.println("Test 3: \"" + test3 + "\"");
        System.out.println("Result: \"" + transformText(test3) + "\"");
        System.out.println();
        
        System.out.println("Test 4: \"" + test4 + "\"");
        System.out.println("Result: \"" + transformText(test4) + "\"");
    }
}
