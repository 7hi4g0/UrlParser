package com.wesleycloversolutions.web;

import java.util.regex.Pattern;

public class UrlParser {
    static Pattern scheme = Pattern.compile("^[a-zA-Z][-a-zA-Z+\\.]*:");
}
