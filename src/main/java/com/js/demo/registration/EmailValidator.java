package com.js.demo.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    private static final String EMAIL_VERIFICATION_REGEX = "[a-zA-Z.0-9]+@[a-z]+\\.[a-z]+";

    @Override
    public boolean test(String s) {
        return s.matches(EMAIL_VERIFICATION_REGEX);
    }
}
