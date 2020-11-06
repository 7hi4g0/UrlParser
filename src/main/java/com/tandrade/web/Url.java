package com.tandrade.web;

import java.util.Map;

public class Url {
    private String protocol;
    private String username;
    private String password;
    private String hostname;
    private int port;
    private String path;
    private Map<String, String> arguments;
    private String documentPart;

    protected Url(String protocol, String username, String password, String hostname, int port, String path, Map<String, String> arguments, String documentPart) {
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.path = path;
        this.arguments = arguments;
        this.documentPart = documentPart;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public String getDocumentPart() {
        return documentPart;
    }
}
