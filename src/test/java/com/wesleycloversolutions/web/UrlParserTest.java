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

    @ParameterizedTest
    @ValueSource(strings = {
            "www.normal.com",
            "www.!$&'()*+,;=-~.com",
            "www.spaced%20url.com",
            "www.%41.com"})
    public void UrlParser_HostnameRegex_ShouldMatchValidHostnames(String hostname) {
        Matcher matcher = UrlParser.hostname.matcher(hostname);

        boolean found = matcher.find();

        assertTrue(found);
        assertEquals(hostname, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ":80",
            ":65535",
            ":22"})
    public void UrlParser_PortRegex_ShouldMatchValidPort(String port) {
        Matcher matcher = UrlParser.port.matcher(port);

        boolean found = matcher.find();

        assertTrue(found);
        assertEquals(port, matcher.group());
        assertEquals(port.substring(1), matcher.group(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "?validQueryString",
            "?yet%20another%20valid%20query%20string",
            "?query=true&params=true",
            "?",
            "?!$&'()*+,;="})
    public void UrlParser_QueryStringRegex_ShouldMatchValidQueryString(String queryString) {
        Matcher matcher = UrlParser.queryString.matcher(queryString);

        boolean found = matcher.find();

        assertTrue(found);
        assertEquals(queryString, matcher.group());
        assertEquals(queryString.substring(1), matcher.group(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "#main",
            "#summary",
            "#",
            "#?random-stuff",
            "#article%20conclusion",
            "#!$&'()*+,;="})
    public void UrlParser_PageFragmentRegex_ShouldMatchValidPageFragment(String pageFragment) {
        Matcher matcher = UrlParser.pageFragment.matcher(pageFragment);

        boolean found = matcher.find();

        assertTrue(found);
        assertEquals(pageFragment, matcher.group());
        assertEquals(pageFragment.substring(1), matcher.group(1));
    }
}
