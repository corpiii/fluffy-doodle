package com.clean_light.server.global.config.filter.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public CustomRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            this.customHeaders.put(headerName, request.getHeader(headerName));
        });
    }

    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return this.customHeaders.getOrDefault(name, super.getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(customHeaders.keySet());
    }
}
