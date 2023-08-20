package ru.klokov.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import ru.klokov.exception.SessionExpiredException;
import ru.klokov.model.Session;
import ru.klokov.service.AuthService;

import java.io.IOException;

@WebFilter(urlPatterns = {"/", "/search", "/add-location", "/delete-location", "/sign-out", "/error"})
public class AuthFilter implements Filter {
    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        authService = (AuthService) filterConfig.getServletContext().getAttribute("authService");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("FILTER DOFILTER!");

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
