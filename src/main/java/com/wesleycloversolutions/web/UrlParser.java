package com.wesleycloversolutions.web;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// All based on RFC 3986
public class UrlParser {
    // Regex based on https://tools.ietf.org/html/rfc3986
    private static final String decOctet = "(?:(?:[1-9]|1\\d|2[0-4])\\d|25[0-5])";
    private static final String hexDig = "[a-fA-F0-9]";
    private static final String hex16 = hexDig + "{1,4}";
    private static final String unreserved = "[-\\w\\.~]";
    private static final String subDelims = "[!$&'()*+,;=]";
    private static final String subDelimsNoEq = "[!$&'()*+,;]";
    private static final String pctEncoded = "%" + hexDig + hexDig;
    private static final String pchar = unreserved  + "|" + pctEncoded +"|"+ subDelims + "|:|@";
    private static final String pcharNoEq = unreserved  + "|" + pctEncoded +"|"+ subDelimsNoEq + "|:|@";
    private static final String username = "(?:" + unreserved + "|" + pctEncoded + "|" + subDelims + ")*";
    private static final String password = "(?:" + unreserved + "|" + pctEncoded + "|" + subDelims + "|:)*";
    private static final String regName = "(?:" + unreserved + "|" + pctEncoded + "|" + subDelims + ")*";
    private static final String ipv4 = "(?:" + decOctet + "(?:\\." + decOctet + "){3})";
    private static final String ls32 = "(?:" + hex16 + ":" + hex16 +"|" + ipv4 + ")";
    private static final String ipv6Part = "(?:" + hex16 + ":)";
    private static final String ipv6 = "(?:"
                                                + ipv6Part + "{6}" + ls32 + "|"
                                         + "::" + ipv6Part + "{5}" + ls32 + "|"
                                 + hex16 + "::" + ipv6Part + "{4}" + ls32 + "|"
            + ipv6Part + "?"     + hex16 + "::" + ipv6Part + "{3}" + ls32 + "|"
            + ipv6Part + "{0,2}" + hex16 + "::" + ipv6Part + "{2}" + ls32 + "|"
            + ipv6Part + "{0,3}" + hex16 + "::" + ipv6Part         + ls32 + "|"
            + ipv6Part + "{0,4}" + hex16 + "::"                    + ls32 + "|"
            + ipv6Part + "{0,5}" + hex16 + "::"                    + hex16 + "|"
            + ipv6Part + "{0,6}" + hex16 + "::)";
    private static final String segmentChar = "(?:" + pchar + ")";
    private static final String query = "(?:" + pchar + "|/|\\?)*";
    private static final String queryParamName = "(?:" + pcharNoEq + "|/|\\?)*";
    private static final String fragment = query;


    public static Pattern urlComponents = Pattern.compile("(?:([^:/?#]+):)(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");
    public static Pattern scheme = Pattern.compile("^[a-zA-Z][-a-zA-Z+\\.]*");
    public static Pattern authority = Pattern.compile("(?:([^@]*)@)?([^:]*)(?::(.*))?");
    public static Pattern userinfo = Pattern.compile("(" + username + ")(?::(" + password + "))?");
    public static Pattern hostname = Pattern.compile("(?:" + regName + "|" + ipv4 + "|\\[" + ipv6 + "\\])");
    public static Pattern port = Pattern.compile("\\d{1,5}");
    public static Pattern pathWithAuthority = Pattern.compile("(?:/" + segmentChar + "*)*");
    public static Pattern pathRootless = Pattern.compile("(?:" + segmentChar + "+(?:" + pathWithAuthority + ")*)");
    public static Pattern pathNoAuthority = Pattern.compile("(?:/" + pathRootless + "?|" + pathRootless + ")?");
    public static Pattern queryString = Pattern.compile(query);
    public static Pattern queryParam = Pattern.compile("(?:(" + queryParamName + ")(?:=(" + query + "))?)");
    public static Pattern pageFragment = Pattern.compile(fragment);

    public static Url parse(String url) throws MalformedURLException {
        Matcher urlMatcher = urlComponents.matcher(url);

        if (!urlMatcher.matches()) {
            throw new MalformedURLException("Bad URL format");
        }

        String scheme = urlMatcher.group(1);
        String authority = urlMatcher.group(2);
        String path = urlMatcher.group(3);
        String query = urlMatcher.group(4);
        String fragment = urlMatcher.group(5);

        String username = null;
        String password = null;
        String hostname = null;
        int port = 0;
        HashMap<String, String> queryParams = null;


        // Validate scheme, a.k.a protocol
        if (scheme != null) {
            if (!UrlParser.scheme.matcher(scheme).matches()) {
                throw new MalformedURLException("Invalid protocol specified: " + scheme);
            }

            switch (scheme) {
                case "ftp":
                    port = 21;
                    break;
                case "http":
                case "ws":
                    port = 80;
                    break;
                case "https":
                case "wss":
                    port = 443;
                    break;
                default:
            }
        }

        // Validate authority
        if (authority != null) {
            Matcher authorityMatcher = UrlParser.authority.matcher(authority);

            if (!authorityMatcher.matches()) {
                throw new MalformedURLException("Bad authority format: " + authority);
            }

            String userinfo = authorityMatcher.group(1);
            hostname = authorityMatcher.group(2);
            String portNumber = authorityMatcher.group(3);

            // Validate userinfo
            if (userinfo != null) {
                Matcher userinfoMatcher = UrlParser.userinfo.matcher(userinfo);

                if (!userinfoMatcher.matches()) {
                    throw new MalformedURLException("Invalid userinfo specified: " + userinfo);
                }

                username = userinfoMatcher.group(1);
                password = userinfoMatcher.group(2);
            }

            // Validate host
            if (hostname != null && !UrlParser.hostname.matcher(hostname).matches()) {
                throw new MalformedURLException("Invalid host specified: " + hostname);
            }

            // Validate port
            if (portNumber != null) {
                if (!UrlParser.port.matcher(portNumber).matches()) {
                    throw new MalformedURLException("Invalid path specified: " + port);
                }

                port = Integer.parseInt(portNumber);
            }
        }

        // Validate path
        if (path != null) {
            if (authority != null) {
                if (!UrlParser.pathWithAuthority.matcher(path).matches()) {
                    throw new MalformedURLException("Invalid path specified: " + path);
                }
            } else {
                if (!UrlParser.pathNoAuthority.matcher(path).matches()) {
                    throw new MalformedURLException("Invalid path specified: " + path);
                }
            }
        }

        // Validate and parses query
        if (query != null) {
            if (!UrlParser.queryString.matcher(query).matches()) {
                throw new MalformedURLException("Invalid query string specified: " + query);
            }

            if (!query.trim().isEmpty()) {
                queryParams = new HashMap<>();

                for (String param : query.trim().split("&")) {
                    Matcher queryParamMatcher = UrlParser.queryParam.matcher(param);

                    if (!queryParamMatcher.matches()) {
                        continue;
                    }

                    String argument = queryParamMatcher.group(1);
                    String value = queryParamMatcher.group(2);

                    queryParams.put(argument, value);
                }
            }
        }

        // Validate fragment
        if (fragment != null && !UrlParser.pageFragment.matcher(fragment).matches()) {
            throw new MalformedURLException("Invalid fragment specified: " + fragment);
        }

        return new Url(scheme, username, password, hostname, port, path, queryParams, fragment);
    }
}
