package br.com.erbium.utils;

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

public class ThreadUtil {

    ThreadLocal<Thread> firstThreadHere;

    public static Thread createThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setName(name);
        return thread;
    }

    public static Thread createThread(Runnable runnable) {
        return createThread(runnable, "Thread-" + System.currentTimeMillis());
    }

    public synchronized void funnelRunnable(Runnable runnable) throws InterruptedException {


        if (firstThreadHere == null) {
            firstThreadHere = new ThreadLocal<>();
            firstThreadHere.set(Thread.currentThread());
            System.out.println("Thread: " + Thread.currentThread().getName());
            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();
        }
    }
}
