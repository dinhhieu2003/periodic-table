package com.periodic.backend.util;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int PASSWORD_LENGTH = 12;

    private static final Random RANDOM = new SecureRandom();

    public String generateNewPassword() {
        StringBuilder password = new StringBuilder();
        
        // Step 1: Ensure at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARACTERS));

        // Step 2: Add the remaining random characters
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomChar(ALL_CHARACTERS));
        }

        // Step 3: Shuffle the password to make it unpredictable
        return shuffleString(password.toString());
    }

    private static char getRandomChar(String characters) {
        return characters.charAt(RANDOM.nextInt(characters.length()));
    }

    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }
}
