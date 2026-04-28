package com.eventmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utility class for validating and sanitizing user input.
 * Provides loop-based input correction until valid data is entered.
 */
public class InputValidator {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-z\\s'-]{2,50}$");

    // Private constructor prevents instantiation
    private InputValidator() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Prompts user for a non-empty string input.
     * 
     * @param prompt The message to display
     * @return Trimmed, non-empty string
     */
    public static String getNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠ Input cannot be empty. Please try again.");
            } else if (input.length() > 255) {
                System.out.println("⚠ Input is too long (max 255 characters). Please try again.");
                input = "";
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Prompts user for a validated name (letters, spaces, hyphens, apostrophes only).
     * 
     * @param prompt The message to display
     * @return Validated name string
     */
    public static String getName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("⚠ Name cannot be empty. Please try again.");
                continue;
            }
            
            if (input.length() < 2 || input.length() > 50) {
                System.out.println("⚠ Name must be between 2 and 50 characters. Please try again.");
                continue;
            }
            
            if (!NAME_PATTERN.matcher(input).matches()) {
                System.out.println("⚠ Name can only contain letters, spaces, hyphens, and apostrophes. Please try again.");
                continue;
            }
            
            return input;
        }
    }

    /**
     * Prompts user for an integer input with validation.
     * 
     * @param prompt The message to display
     * @return Valid integer
     */
    public static int getInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid input. Please enter a valid whole number.");
            }
        }
    }

    /**
     * Prompts user for a positive integer (greater than zero).
     * 
     * @param prompt The message to display
     * @return Positive integer
     */
    public static int getPositiveInt(String prompt) {
        while (true) {
            int value = getInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("⚠ Value must be greater than zero. Please try again.");
        }
    }
    
    /**
     * Prompts user for a non-negative integer (zero or greater).
     * 
     * @param prompt The message to display
     * @return Non-negative integer
     */
    public static int getNonNegativeInt(String prompt) {
        while (true) {
            int value = getInt(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("⚠ Value cannot be negative. Please try again.");
        }
    }

    /**
     * Prompts user for a date in YYYY-MM-DD format.
     * Validates that the date is not in the past.
     * 
     * @param prompt The message to display
     * @return Valid LocalDate not in the past
     */
    public static LocalDate getDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input);
                
                // Check if date is in the past
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("⚠ Date cannot be in the past. Please enter a future date.");
                    continue;
                }
                
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Invalid date format. Please use YYYY-MM-DD (e.g., 2024-12-31).");
            }
        }
    }
    
    /**
     * Prompts user for any date (past or future) in YYYY-MM-DD format.
     * 
     * @param prompt The message to display
     * @return Valid LocalDate
     */
    public static LocalDate getAnyDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Invalid date format. Please use YYYY-MM-DD (e.g., 2024-12-31).");
            }
        }
    }

    /**
     * Prompts user for a validated email address.
     * 
     * @param prompt The message to display
     * @return Valid email address
     */
    public static String getEmail(String prompt) {
        while (true) {
            String input = getNonEmptyString(prompt);
            if (EMAIL_PATTERN.matcher(input).matches()) {
                return input.toLowerCase(); // Normalize email to lowercase
            }
            System.out.println("⚠ Invalid email format. Example: user@example.com");
        }
    }
    
    /**
     * Prompts user for a menu choice within a valid range.
     * 
     * @param min Minimum valid choice
     * @param max Maximum valid choice
     * @param prompt The message to display
     * @return Valid menu choice
     */
    public static int getMenuChoice(int min, int max, String prompt) {
        while (true) {
            int choice = getInt(prompt);
            if (choice >= min && choice <= max) {
                return choice;
            }
            System.out.println("⚠ Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }
    
    /**
     * Prompts user for confirmation (Y/N).
     * 
     * @param prompt The message to display
     * @return true if user confirms, false otherwise
     */
    public static boolean getConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            } else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.println("⚠ Please enter Y (Yes) or N (No).");
        }
    }
}
