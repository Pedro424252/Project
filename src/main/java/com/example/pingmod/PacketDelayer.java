package com.example.pingmod;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketDelayer {
    public static volatile boolean enabled = false;
    public static volatile int delayMs = 5000;

    public static final ThreadLocal<Boolean> BYPASS = ThreadLocal.withInitial(() -> false);
    private static final AtomicInteger pending = new AtomicInteger();
    private static long lastDue = 0;

    private static final ScheduledExecutorService EXEC = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "pingmod-delay");
        t.setDaemon(true);
        return t;
    });

    public static boolean shouldDelay() {
        if (BYPASS.get()) return false;
        if (pending.get() > 0) return true; // sirayi bozmamak icin
        return enabled && delayMs > 0 && MinecraftClient.getInstance().player != null;
    }

    public static synchronized void schedule(Runnable send) {
        long now = System.currentTimeMillis();
        long due = Math.max(now + (enabled ? delayMs : 0), lastDue); // paket sirasi korunur
        lastDue = due;
        pending.incrementAndGet();
        EXEC.schedule(() -> {
            try {
                BYPASS.set(true);
                send.run();
            } finally {
                BYPASS.set(false);
                pending.decrementAndGet();
            }
        }, Math.max(0, due - now), TimeUnit.MILLISECONDS);
    }
}
