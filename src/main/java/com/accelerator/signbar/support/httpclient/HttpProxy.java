package com.accelerator.signbar.support.httpclient;

public class HttpProxy {

    private String host;

    private int port = 8087;

    private String username;

    private String password;

    public HttpProxy(String ipAndPort) {
        super();
        String[] ipAndPorts = ipAndPort.split(":");
        this.host = ipAndPorts[0];
        this.port = Integer.parseInt(ipAndPorts[1]);
    }

    public HttpProxy(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    public HttpProxy(String host, int port, String username, String password) {
        super();
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
