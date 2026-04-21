package com.tracker.engine;

/**
 * Thread-local holder for the currently acting user (Change 3).
 * Populated per-request by UserInterceptor from the X-Current-User header.
 * Read by CommandLog to stamp each command with the acting user's name.
 */
public class UserContextHolder {

    private static final ThreadLocal<String> current = new ThreadLocal<>();
    private static final String DEFAULT_USER = "staff";

    public static String get() {
        String u = current.get();
        return (u != null && !u.isBlank()) ? u : DEFAULT_USER;
    }

    public static void set(String username) { current.set(username); }

    public static void clear() { current.remove(); }

    private UserContextHolder() {}
}
