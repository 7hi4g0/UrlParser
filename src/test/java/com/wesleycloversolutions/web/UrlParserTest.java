package com.wesleycloversolutions.web;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlParserTest {

    @ParameterizedTest
    @MethodSource("provideUrlComponents")
    public void UrlParser_parse_ShouldParseCorrectURLs(String url, String scheme, String _authority, String username, String password, String host, int port, String path, String query, String fragment) throws MalformedURLException {
        Url parsedUrl = UrlParser.parse(url);

        assertNotNull(parsedUrl);
        assertEquals(scheme, parsedUrl.getProtocol());
        assertEquals(username, parsedUrl.getUsername());
        assertEquals(password, parsedUrl.getPassword());
        assertEquals(host, parsedUrl.getHostname());
        assertEquals(port, parsedUrl.getPort());
        assertEquals(path, parsedUrl.getPath());
        //assertEquals(query, matcher.group(4));
        assertEquals(fragment, parsedUrl.getDocumentPart());
    }

    @ParameterizedTest
    @MethodSource("provideUrlComponents")
    public void UrlParser_ComponentsRegex_ShouldMatchComponents(String url, String scheme, String authority, String _username, String _password, String _host, int _port, String path, String query, String fragment) {
        Matcher matcher = UrlParser.urlComponents.matcher(url);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(scheme, matcher.group(1));
        assertEquals(authority, matcher.group(2));
        assertEquals(path, matcher.group(3));
        assertEquals(query, matcher.group(4));
        assertEquals(fragment, matcher.group(5));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http",
            "scheme-test",
            "scheme+test",
            "scheme.test",
            "ScHeMe-TeSt"})
    public void UrlParser_SchemeRegex_ShouldMatchValidSchemes(String scheme) {
        Matcher matcher = UrlParser.scheme.matcher(scheme);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(scheme, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "scheme_test",
            "scheme%test"})
    public void UrlParser_SchemeRegex_ShouldNotMatchInvalidSchemes(String scheme) {
        final Matcher matcher = UrlParser.scheme.matcher(scheme);

        boolean found = matcher.matches();

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
            "/pub/ietf/uri/",
            "/pub/ietf/uri",
            "//pub/ietf/uri/",
            "//pub/ietf/uri",
            "/pub/ietf/uri//",
            ""})
    public void UrlParser_PathWithAuthorityRegex_ShouldMatchValidPaths(String path) {
        Matcher matcher = UrlParser.pathWithAuthority.matcher(path);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(path, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pub/ietf/uri/",
            "pub"})
    public void UrlParser_PathWithAuthorityRegex_ShouldNotMatchInvalidPaths(String path) {
        Matcher matcher = UrlParser.pathWithAuthority.matcher(path);

        boolean found = matcher.matches();

        assertFalse(found);
        assertThrows(IllegalStateException.class, () -> matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/pub/ietf/uri/",
            "/pub/ietf/uri//",
            "/pub/ietf/uri",
            "pub/ietf/uri//",
            "pub/ietf/uri",
            "pub",
            ""})
    public void UrlParser_PathNoAuthorityRegex_ShouldMatchValidPaths(String path) {
        Matcher matcher = UrlParser.pathNoAuthority.matcher(path);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(path, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//pub/ietf/uri/",
            "///"})
    public void UrlParser_PathNoAuthorityRegex_ShouldNotMatchInvalidPaths(String path) {
        Matcher matcher = UrlParser.pathNoAuthority.matcher(path);

        boolean found = matcher.matches();

        assertFalse(found);
        assertThrows(IllegalStateException.class, () -> matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "80",
            "65535",
            "22"})
    public void UrlParser_PortRegex_ShouldMatchValidPort(String port) {
        Matcher matcher = UrlParser.port.matcher(port);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(port, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "validQueryString",
            "yet%20another%20valid%20query%20string",
            "query=true&params=true",
            "",
            "!$&'()*+,;="})
    public void UrlParser_QueryStringRegex_ShouldMatchValidQueryString(String queryString) {
        Matcher matcher = UrlParser.queryString.matcher(queryString);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(queryString, matcher.group());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "main",
            "summary",
            "",
            "?random-stuff",
            "article%20conclusion",
            "!$&'()*+,;="})
    public void UrlParser_PageFragmentRegex_ShouldMatchValidPageFragment(String pageFragment) {
        Matcher matcher = UrlParser.pageFragment.matcher(pageFragment);

        boolean found = matcher.matches();

        assertTrue(found);
        assertEquals(pageFragment, matcher.group());
    }

    private static Stream<Arguments> provideUrlComponents() {
        return Stream.of(
                //          | URL -------------------------------------------------------------| scheme ---| authority --------------------| user -| password -| hostname----------| port--| path ---------------------------------| query ----| fragment |
                Arguments.of("http://www.ics.uci.edu/pub/ietf/uri/#Related",                    "http",     "www.ics.uci.edu",              null,   null,       "www.ics.uci.edu",  80,     "/pub/ietf/uri/",                       null,       "Related"),
                Arguments.of("http://www.w3.org/Addressing/",                                   "http",     "www.w3.org",                   null,   null,       "www.w3.org",       80,     "/Addressing/",                         null,       null),
                Arguments.of("ftp://foo.example.com/rfc/",                                      "ftp",      "foo.example.com",              null,   null,       "foo.example.com",  21,     "/rfc/",                                null,       null),
                Arguments.of("http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING",     "http",     "www.ics.uci.edu",              null,   null,       "www.ics.uci.edu",  80,     "/pub/ietf/uri/historical.html",        null,       "WARNING"),
                Arguments.of("file:///C:/demo",                                                 "file",     "",                             null,   null,       "",                 0,      "/C:/demo",                             null,       null),
                Arguments.of("file:///C:/",                                                     "file",     "",                             null,   null,       "",                 0,      "/C:/",                                 null,       null),
                Arguments.of("file:///",                                                        "file",     "",                             null,   null,       "",                 0,      "/",                                    null,       null),
                Arguments.of("https://example.org",                                             "https",    "example.org",                  null,   null,       "example.org",      443,    "",                                     null,       null),
                Arguments.of("https://user:password@example.org/",                              "https",    "user:password@example.org",    "user", "password", "example.org",      443,    "/",                                    null,       null),
                Arguments.of("https://example.org/foo%20bar",                                   "https",    "example.org",                  null,   null,       "example.org",      443,    "/foo%20bar",                           null,       null),
                Arguments.of("https://example.com///",                                          "https",    "example.com",                  null,   null,       "example.com",      443,    "///",                                  null,       null),
                Arguments.of("https://example.com/foo",                                         "https",    "example.com",                  null,   null,       "example.com",      443,    "/foo",                                 null,       null),
                Arguments.of("https://example.org/",                                            "https",    "example.org",                  null,   null,       "example.org",      443,    "/",                                    null,       null),
                Arguments.of("https://example.com/example.org",                                 "https",    "example.com",                  null,   null,       "example.com",      443,    "/example.org",                         null,       null),
                Arguments.of("https://example.com/demo/",                                       "https",    "example.com",                  null,   null,       "example.com",      443,    "/demo/",                               null,       null),
                Arguments.of("https://example.com/example",                                     "https",    "example.com",                  null,   null,       "example.com",      443,    "/example",                             null,       null),
                Arguments.of("https://localhost:8000/search?q=text#hello",                      "https",    "localhost:8000",               null,   null,       "localhost",        8000,   "/search",                              "q=text",   "hello"),
                Arguments.of("urn:isbn:9780307476463",                                          "urn",      null,                           null,   null,       null,               0,      "isbn:9780307476463",                   null,       null),
                Arguments.of("file:///ada/Analytical%20Engine/README.md",                       "file",     "",                             null,   null,       "",                 0,      "/ada/Analytical%20Engine/README.md",   null,       null)
        );
    }
}
