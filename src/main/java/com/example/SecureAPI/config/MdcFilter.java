package com.example.SecureAPI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр для добавления MDC (Mapped Diagnostic Context) в логи.
 * Добавляет уникальный requestId и userId к каждому запросу
 * для удобства отладки, аудита и трассировки.
 */
@Component
public class MdcFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MdcFilter.class);

    // Константы для ключей MDC — лучшая практика для предотвращения опечаток [[4]]
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();

        try {
            // Установка уникального ID запроса в MDC
            MDC.put(REQUEST_ID, requestId);
            logger.trace("Assigned request ID: {}", requestId);

            // Добавление userId из Spring Security, если доступен
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName(); // Может быть username, email, ID и т.д.
                MDC.put(USER_ID, userId);
                logger.trace("Added userId={} to MDC", userId);
            }

            // Добавляем ID запроса в заголовок ответа для трассировки — рекомендуется при микросервисной архитектуре [[1]]
            response.setHeader("X-Request-ID", requestId);

            // Продолжаем цепочку фильтров
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            logger.error("Error occurred in MDC filter for request ID: {}", requestId, ex);
            throw ex; // Перебрасываем дальше, чтобы обработать глобально
        } finally {
            logger.trace("Clearing MDC context for request ID: {}", requestId);
            MDC.clear(); // Очистка MDC после завершения запроса — важно для повторного использования потока [[9]]
        }
    }
}