package ru.klokov.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.commons.validator.routines.EmailValidator;

public class ValidatorUtil {
    public static boolean passwordsNotMatch(String password, String encryptedPassword) {
        return !BCrypt.verifyer().verify(password.toCharArray(), encryptedPassword).verified;
    }
}
