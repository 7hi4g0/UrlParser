package com.wesleycloversolutions.web;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlParser {
    // Regex based on https://tools.ietf.org/html/rfc3986
    private static String hexDig = "[a-fA-F0-9]";
    private static String unreserved = "[-\\w\\.~]";
    private static String subDelims = "[!$&'()*+,;=]";
    private static String pctEncoded = "%" + hexDig + hexDig;
    private static String pchar = unreserved  + "|" + pctEncoded +"|"+ subDelims + "|:|@";
    private static String query = "(?:" + pchar + "|/|\\?)*";
    private static String fragment = query;
    private static String regName = "(?:" + unreserved + "|" + pctEncoded + "|" + subDelims + ")*";
    private static String segmentChar = "(?:" + pchar + ")";


    public static Pattern urlComponents = Pattern.compile("(?:([^:/?#]+):)(?://([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");
    public static Pattern scheme = Pattern.compile("^[a-zA-Z][-a-zA-Z+\\.]*");
    public static Pattern hostname = Pattern.compile(regName);
    public static Pattern port = Pattern.compile("\\d{1,5}");
    public static Pattern pathWithAuthority = Pattern.compile("(?:/" + segmentChar + "*)*");
    public static Pattern pathRootless = Pattern.compile("(?:" + segmentChar + "+(?:" + pathWithAuthority + ")*)");
    public static Pattern pathNoAuthority = Pattern.compile("(?:/" + pathRootless + "?|" + pathRootless + ")?");
    public static Pattern queryString = Pattern.compile(query);
    public static Pattern queryParam = Pattern.compile("(?:(" + query + ")(?:=(" + query + "))?)");
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

        HashMap<String, String> queryParams = null;


        // Validate scheme, a.k.a protocol
        if (scheme != null && !UrlParser.scheme.matcher(scheme).matches()) {
            throw new MalformedURLException("Invalid protocol specified: " + scheme);
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

        return new Url(scheme, null, null, null, 0, path, queryParams, fragment);
    }
}
