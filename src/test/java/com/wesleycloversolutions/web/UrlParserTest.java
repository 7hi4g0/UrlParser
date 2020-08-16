package com.wesleycloversolutions.web;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "http",
            "scheme-test",
            "scheme+test",
            "scheme.test",
            "ScHeMe-TeSt"})
    public void UrlParser_SchemeRegex_ShouldMatchValidSchemes(String scheme) {
        String completeScheme = scheme + ":";
        Matcher matcher = UrlParser.scheme.matcher(completeScheme);

        boolean found = matcher.find();

        assertTrue(found);
        assertEquals(completeScheme, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "scheme_test",
            "scheme%test"})
    public void UrlParser_SchemeRegex_ShouldNotMatchInvalidSchemes(String scheme) {
        String completeScheme = scheme + ":";
        final Matcher matcher = UrlParser.scheme.matcher(completeScheme);

        boolean found = matcher.find();

        assertFalse(found);
        assertThrows(IllegalStateException.class, () -> matcher.group());
    }
}
