package com.example.SecureAPI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Фильтр для добавления MDC (Mapped Diagnostic Context) в логи.
 * Добавляет userId к каждому запросу для удобства отладки и аудита.
 */
@Component
public class MdcFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MdcFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Object userId = request.getAttribute("userId");

            if (userId != null) {
                MDC.put("userId", userId.toString());
                logger.debug("Added userId={} to MDC", userId);
            }

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
            logger.trace("Cleared MDC context after request processing");
        }
    }
}