package ru.yandex.account.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class HeaderRemovingRequestWrapper extends HttpServletRequestWrapper {

    private final String headerToRemove;

    public HeaderRemovingRequestWrapper(HttpServletRequest request, String headerToRemove) {
        super(request);
        this.headerToRemove = headerToRemove.toLowerCase();
    }

    @Override
    public String getHeader(String name) {
        return name.equalsIgnoreCase(headerToRemove) ? null : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return name.equalsIgnoreCase(headerToRemove)
                ? Collections.emptyEnumeration()
                : super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headerNames = Collections.list(super.getHeaderNames());
        headerNames.removeIf(name -> name.equalsIgnoreCase(headerToRemove));
        return Collections.enumeration(headerNames);
    }
}
