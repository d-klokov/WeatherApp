package ru.klokov.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.commons.validator.routines.EmailValidator;
import ru.klokov.exception.InvalidParameterException;

public class ValidatorUtil {
    public static boolean emailIsNotValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean passwordsNotMatch(String password, String encryptedPassword) {
        return !BCrypt.verifyer().verify(password.toCharArray(), encryptedPassword).verified;
    }

    public static void validateAuthParameters(String email, String password) {
        if (email == null || email.isBlank() || !ValidatorUtil.emailIsNotValid(email))
            throw new InvalidParameterException("Valid email required!");
        if (password == null || password.isBlank())
            throw new InvalidParameterException("Password required!");
    }
}
