package com.wesleycloversolutions.web;

import java.util.regex.Pattern;

public class UrlParser {
    // Regex based on https://tools.ietf.org/html/rfc3986
    static String hexDig = "[a-fA-F0-9]";
    static String unreserved = "[-\\w\\.~]";
    static String subDelims = "[!$&'()*+,;=]";
    static String pctEncoded = "%" + hexDig + hexDig;
    static String pchar = unreserved  + "|" + pctEncoded +"|"+ subDelims + "|:|@";
    static String query = "(?:" + pchar + "|/|\\?)*";
    static String fragment = query;
    static String regName = "(?:" + unreserved + "|" + pctEncoded + "|" + subDelims + ")*";
    
    static Pattern scheme = Pattern.compile("^[a-zA-Z][-a-zA-Z+\\.]*:");
    static Pattern hostname = Pattern.compile(regName);
    static Pattern port = Pattern.compile(":(\\d{1,5})");
    static Pattern queryString = Pattern.compile("\\?(" + query + ")");
    static Pattern pageFragment = Pattern.compile("#(" + fragment + ")");
}
