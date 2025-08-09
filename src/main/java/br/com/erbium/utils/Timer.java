package br.com.erbium.utils;

import java.time.Duration;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

public class Timer {
    private Long timeout;
    
    private Long expired;
    private Long start;
    private Long elapsedTime;

    public Timer() {
    }

    
    public Timer(Duration duration) {
        this(duration.toMillis());
    }

    
    public Timer(long timeout) {
        this.timeout = timeout;
        this.start = System.currentTimeMillis();
        this.expired = this.start + timeout;
    }

    
    public void start() {
        this.start = System.currentTimeMillis();
    }

    
    public Timer stop() {
        this.elapsedTime = getElapsedTime().toMillis();
        return this;
    }

    
    public Long getStartTime() {
        if (start == null)
            start = System.currentTimeMillis();
        return start;
    }

    public boolean isNotExpired() {
        return !isExpired();
    }

    
    public boolean isExpired() {
        if (timeout == null)
            return true;
        if (start == null) {
            start = System.currentTimeMillis();
            return true;
        }
        return System.currentTimeMillis() < this.expired;
    }

    
    public void reset() {
        this.start = System.currentTimeMillis();
        if (timeout == null)
            return;
        this.expired = this.start + this.timeout;
    }

    
    public Duration getElapsedTime() {
        if (start == null) {
            start = System.currentTimeMillis();
            return Duration.ofMillis(0);
        }
        if (elapsedTime != null)
            return Duration.ofMillis(elapsedTime);
        return Duration.ofMillis(System.currentTimeMillis() - this.start);
    }

    
    public static void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    
    
    public void sleep(Duration duration) {
        sleep(duration.toMillis());
    }
}
