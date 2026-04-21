package com.tracker.client;

import com.tracker.engine.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Reads X-Current-User header on each request and stores the value in
 * UserContextHolder (thread-local). CommandLog reads it to stamp each command.
 * Cleared after the request completes to avoid leaking between threads.
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String user = request.getHeader("X-Current-User");
        UserContextHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContextHolder.clear();
    }
}
