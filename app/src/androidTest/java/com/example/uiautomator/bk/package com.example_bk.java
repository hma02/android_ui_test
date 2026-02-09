package com.example.uiautomator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class UiActionTest {

    private UiDevice device;
    private Bundle args;
    private static volatile boolean shouldStop = false;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        args = InstrumentationRegistry.getArguments();
    }

    @Test
    public void runAction() {
        String action = args.getString("action");

        if (action == null) {
            log("ERROR: action not provided");
            return;
        }

        switch (action) {
            case "quick_tap":
                shouldStop = false;
                doQuickTap();
                break;
            case "interrupt":
                shouldStop = true;
                log("Received interrupt signal");
                break;

            case "pinch":
                doPinch();
                break;

            case "swipe":
                doCurveSwipe();
                break;

            case "show_toast":
                showToast();
                break;
            case "long_swipe":
                doLongSwipe();
                break;
            case "quick_tap_area":
                quickTapArea();
                break;
            case "tap_and_swipe":
                tapAndSwipeNew();
                break;

            default:
                log("ERROR: unknown action: " + action);
        }
    }

    // ---------------- TOAST ----------------
    private void showToast() {
        final String text = args.getString("text", "Hello!");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(
                    InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    text,
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Keep process alive long enough for toast to display
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private void showToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(
                    InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        });

        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {}
    }

// ---------------- QUICK TAP ----------------
    // private void doQuickTap() {
    // int x = parseInt(args.getString("x"), -1);
    // int y = parseInt(args.getString("y"), -1);

    // if (x < 0 || y < 0) {
    //     log("ERROR: x/y not provided for quick_tap");
    //     return;
    // }

    // // NEW: number of taps (default = 8)
    // int taps = parseInt(args.getString("n"), 16);

    // int intervalMs = 250;         // 4 taps per second

    // for (int i = 0; i < taps; i++) {
    //     device.click(x, y);
    //     try {
    //         Thread.sleep(intervalMs);
    //     } catch (InterruptedException ignored) {}
    // }
    // }

    // private void doQuickTap() {
    //     int x = parseInt(args.getString("x"), -1);
    //     int y = parseInt(args.getString("y"), -1);

    //     if (x < 0 || y < 0) {
    //         log("ERROR: x/y not provided for quick_tap");
    //         return;
    //     }

    //     int taps = parseInt(args.getString("n"), 16);
    //     int intervalMs = 150;

    //     Random rand = new Random();

    //     for (int i = 0; i < taps; i++) {
    //         int offsetX = rand.nextInt(5) - 2;  // -2 to +2
    //         int offsetY = rand.nextInt(5) - 2;

    //         // device.click(x + offsetX, y + offsetY);
    //         device.swipe(x+ offsetX, y+ offsetY, x+ offsetX+3, y+ offsetY+3, 20);

    //         try {
    //             Thread.sleep(intervalMs);
    //         } catch (InterruptedException ignored) {}
    //     }
    // }

    // ---------------- PINCH ----------------
    private void doPinch() {
        String resId = args.getString("resId");
        if (resId == null || resId.isEmpty()) {
            log("ERROR: resId missing for pinch");
            return;
        }

        UiObject2 target = device.wait(Until.findObject(By.res(resId)), 5000);
        if (target != null) {
            int speed = parseInt(args.getString("speed"), 65);
            // NEW: optional ratio argument (default = 0.8f)
            float ratio = parseFloat(args.getString("ratio"), 0.8f);

            target.pinchClose(ratio, speed);
        } else {
            log("ERROR: target not found: " + resId);
        }
    }

    // ---------------- CURVED SWIPE ----------------
    private void doCurveSwipe() {
        int duration = parseInt(args.getString("du"), 650);
        int startX = parseInt(args.getString("startx"), -1);
        int startY = parseInt(args.getString("starty"), -1);

        if (startX < 0 || startY < 0) {
            log("ERROR: startx/starty not provided");
            return;
        }

        List<android.graphics.Point> points = new ArrayList<>();
        points.add(new android.graphics.Point(startX, startY));

        for (int i = 1; ; i++) {
            String xs = args.getString("x" + i);
            String ys = args.getString("y" + i);
            if (xs == null || ys == null) break;
            points.add(new android.graphics.Point(Integer.parseInt(xs), Integer.parseInt(ys)));
        }

        if (points.size() < 2) {
            log("ERROR: swipe requires at least 2 points");
            return;
        }

        android.graphics.Point[] path = points.toArray(new android.graphics.Point[0]);
        int steps = Math.max(10, duration / 60);
        device.swipe(path, steps);
    }
    // ---------------- LONG SWIPE MIRRORED ----------------
    private void doLongSwipe() {

        // int targetPoints = 80;       // total points in swipe
        int screenWidth = 720;        // phone screen width for mirroring

        // --- Collect base points ---
        List<android.graphics.Point> basePoints = new ArrayList<>();
        int targetPoints = parseInt(args.getString("du"), 100);  // 5 seconds
        int startX = parseInt(args.getString("startx"), -1);
        int startY = parseInt(args.getString("starty"), -1);
        if (startX < 0 || startY < 0) {
            log("ERROR: startx/starty not provided for longSwipe");
            return;
        }
        basePoints.add(new android.graphics.Point(startX, startY));

        for (int i = 1; i <= 7; i++) {
            String xs = args.getString("x" + i);
            String ys = args.getString("y" + i);
            if (xs == null || ys == null) {
                log("ERROR: missing x" + i + "/y" + i + " for longSwipe");
                return;
            }
            basePoints.add(new android.graphics.Point(Integer.parseInt(xs), Integer.parseInt(ys)));
        }

        // --- Expand path with horizontal mirroring ---
        List<android.graphics.Point> longPath = new ArrayList<>();
        boolean useMirror = false;
        int index = 0;

        while (longPath.size() < targetPoints) {
            for (android.graphics.Point p : basePoints) {
                int x = useMirror ? screenWidth - p.x : p.x;
                longPath.add(new android.graphics.Point(x, p.y));
                if (longPath.size() >= targetPoints) break;
            }
            useMirror = !useMirror;  // flip for next sequence
        }

        android.graphics.Point[] path = longPath.toArray(new android.graphics.Point[0]);

        // --- Compute steps for swipe ---
        int steps = Math.max(8, 8);

        device.swipe(path, steps);

    }

    private void quickTapArea() {
        int n_loop = parseInt(args.getString("n_loop"), 1);


        // Collect 8 coordinates
        List<android.graphics.Point> points = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int x = parseInt(args.getString("x" + i), -1);
            int y = parseInt(args.getString("y" + i), -1);
            if (x < 0 || y < 0) {
                log("ERROR: x" + i + "/y" + i + " not provided for quickTapArea");
                return;
            }
            points.add(new android.graphics.Point(x, y));
        }

        // Minimal delay between taps for "bombing" effect
        int intervalMs = parseInt(args.getString("interval"), 100);// 50ms between taps, can reduce further if needed

        for (int loop = 0; loop < n_loop; loop++) {
            for (android.graphics.Point p : points) {
                device.click(p.x, p.y);
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private void humanTap(int x, int y, int holdMs) {
        int steps = Math.max(2, holdMs / 16);
        device.swipe(x, y, x+1, y+2, steps);
    }

    // private final ExecutorService tapExecutor1 =
    //         Executors.newSingleThreadExecutor();

    // private final ExecutorService tapExecutor2 =
    //         Executors.newSingleThreadExecutor();

    private final ThreadPoolExecutor tapExecutor1 = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()
    );
    private final ThreadPoolExecutor tapExecutor2 = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()
    );
    private volatile boolean useFirstExecutor = true;

    private void runAsyncHumanTap(int x, int y, int swipeDuration) {

        ExecutorService executor;

        if (tapExecutor1.getQueue().size() > 5 ||
            tapExecutor2.getQueue().size() > 5) {
            return; // drop this tap only
        }

        // Alternate executor
        if (useFirstExecutor) {
            executor = tapExecutor1;
        } else {
            executor = tapExecutor2;
        }

        useFirstExecutor = !useFirstExecutor;

        executor.submit(() -> {
            try {
                int endX = x + 3;
                int endY = y + 3;

                String cmd = String.format(
                        "input swipe %d %d %d %d %d",
                        x, y, endX, endY, swipeDuration
                );

                device.executeShellCommand(cmd);

            } catch (Exception ignored) {}
        });
    }

    // private void runAsyncSwipe(android.graphics.Point[] path, int steps) {
    //     new Thread(() -> {
    //         try {
    //             device.swipe(path, steps);
    //         } catch (Exception ignored) {}
    //     }).start();
    // }

    // ---------------- TAP AND SWIPE ----------------

    // private void tapAndSwipeNew() {

    //     int duration = parseInt(args.getString("du"), 500);
    //     int baseSteps = Math.max(6, duration / 40);
    //     int tapDelayMs = parseInt(args.getString("tap_delay"), 30);
    //     int tapHoldMs = Math.max(45, parseInt(args.getString("tap_hold"), 100));
    //     int nLoop = parseInt(args.getString("n_loop"), 1);

    //     List<android.graphics.Point> points = new ArrayList<>();

    //     // Collect points dynamically (x0/y0 = base)
    //     for (int i = 0; ; i++) {
    //         String xs = args.getString("x" + i);
    //         String ys = args.getString("y" + i);
    //         if (xs == null || ys == null) break;
    //         points.add(new android.graphics.Point(
    //                 Integer.parseInt(xs),
    //                 Integer.parseInt(ys)
    //         ));
    //     }

    //     if (points.size() < 3) {
    //         showToast("ERROR: Need at least 3 points (1 base + 2 leaf points)");
    //         return;
    //     }

    //     android.graphics.Point base = points.get(0);

    //     // After base, remaining points must be groups of 2
    //     int remaining = points.size() - 1;

    //     if (remaining % 2 != 0) {
    //         showToast("ERROR: Leaf points must be in groups of 2");
    //         return;
    //     }

    //     Random rand = new Random();
    //     int leafCount = remaining / 2;

    //     for (int loop = 0; loop < nLoop; loop++) {
    //         for (int l = 0; l < leafCount; l++) {

    //             int idx = 1 + l * 2;

    //             android.graphics.Point p1 = points.get(idx);   // left/side of leaf
    //             android.graphics.Point tip = points.get(idx + 1); // tip of leaf

    //             // ---- TAP at base ----
    //             int jitterX = rand.nextInt(5) - 2;
    //             int jitterY = rand.nextInt(5) - 2;
    //             humanTap(base.x + jitterX, base.y + jitterY, tapHoldMs + rand.nextInt(10));
    //             sleep(tapDelayMs + rand.nextInt(5));
    //             humanTap(base.x + jitterX, base.y + jitterY, tapHoldMs + rand.nextInt(10));
    //             sleep(tapDelayMs + rand.nextInt(5));
    //             // ---- Curve Swipe (base → p1 → tip → base) ----
    //             android.graphics.Point[] path = new android.graphics.Point[] {
    //                     new android.graphics.Point(base.x, base.y),
    //                     new android.graphics.Point(p1.x, p1.y),
    //                     new android.graphics.Point(tip.x, tip.y),
    //                     new android.graphics.Point(base.x, base.y)
    //             };

    //             device.swipe(path, baseSteps);

    //             sleep(tapDelayMs + rand.nextInt(5));
    //         }
    //     }
    // }

    // private Thread tapThread;
    // private boolean shouldStop = false;

    private void doQuickTap() {

        int x1 = parseInt(args.getString("x"), -1);
        int y1 = parseInt(args.getString("y"), -1);

        int x2 = parseInt(args.getString("x2"), -1);
        int y2 = parseInt(args.getString("y2"), -1);

        if (x1 < 0 || y1 < 0) {
            log("ERROR: x/y not provided for quick_tap");
            return;
        }

        boolean useSecondPoint = (x2 >= 0 && y2 >= 0);
        shouldStop = false;

        int intervalMs = 150; // interval between taps
        long durationMs = 60_000; // maximum duration (1 minute)
        long startTime = System.currentTimeMillis();

        Random rand = new Random();
        boolean toggle = false;

        while (!shouldStop &&
            (System.currentTimeMillis() - startTime) < durationMs) {

            // Determine which point to tap
            int baseX = x1;
            int baseY = y1;
            if (useSecondPoint && toggle) {
                baseX = x2;
                baseY = y2;
            }
            toggle = !toggle;

            // Add small random offset
            int offsetX = rand.nextInt(5) - 2; // -2 to +2
            int offsetY = rand.nextInt(5) - 2;

            int startX = baseX + offsetX;
            int startY = baseY + offsetY;
            int endX = startX + 3;
            int endY = startY + 3;
            int swipeDuration = 200;

            String cmd = String.format(
                    "input swipe %d %d %d %d %d",
                    startX, startY, endX, endY, swipeDuration
            );

            // Execute each tap asynchronously so loop doesn't block
            new Thread(() -> {
                try {
                    device.executeShellCommand(cmd);
                } catch (Exception ignored) {}
            }).start();

            // Wait interval between taps
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException ignored) {}
        }

        log("QuickTap stopped");

        // tapThread.start();
    }


    private void tapAndSwipeNew() {

        int tapDelayMs = parseInt(args.getString("tap_delay"), 150);
        int tapHoldMs = Math.max(45, parseInt(args.getString("tap_hold"), 200));

        List<android.graphics.Point> points = new ArrayList<>();

        // Collect points dynamically
        for (int i = 0; ; i++) {
            String xs = args.getString("x" + i);
            String ys = args.getString("y" + i);
            if (xs == null || ys == null) break;
            points.add(new android.graphics.Point(
                    Integer.parseInt(xs),
                    Integer.parseInt(ys)
            ));
        }

        if (points.size() < 3) {
            showToast("ERROR: Need at least 3 points (1 base + 2 leaf points)");
            return;
        }

        android.graphics.Point base = points.get(0);
        int remaining = points.size() - 1;
        if (remaining % 2 != 0) {
            showToast("ERROR: Leaf points must be in groups of 2");
            return;
        }

        Random rand = new Random();
        int leafCount = remaining / 2;

        shouldStop = false;
        long startTime = System.currentTimeMillis();
        long durationMs = 60_000; // 1 minute

        while (!shouldStop &&
            (System.currentTimeMillis() - startTime) < durationMs) {

            for (int l = 0; l < leafCount; l++) {

                if (shouldStop) break;

                int idx = 1 + l * 2;
                android.graphics.Point p1 = points.get(idx);
                android.graphics.Point tip = points.get(idx + 1);

                // Tap each point asynchronously with small jitter
                int offsetX = rand.nextInt(3) - 1;
                int offsetY = rand.nextInt(3) - 1;
                runAsyncHumanTap(tip.x + offsetX, tip.y + offsetY, tapHoldMs + rand.nextInt(2));
                sleep(tapDelayMs);
                offsetX = rand.nextInt(3) - 1;
                offsetY = rand.nextInt(3) - 1;
                runAsyncHumanTap(base.x + offsetX, base.y + offsetY, tapHoldMs + rand.nextInt(2));
                sleep(tapDelayMs + rand.nextInt(10));
            }
        }

        log("tapAndSwipeNew stopped");

        // tapExecutor1.shutdownNow();
        // tapExecutor2.shutdownNow();
    }

    // private void tapAndSwipeNew() {

    //     int duration = parseInt(args.getString("du"), 650);
    //     int baseSteps = 9; //Math.max(6, duration / 40);
    //     int tapDelayMs = parseInt(args.getString("tap_delay"), 300);
    //     int tapHoldMs = Math.max(45, parseInt(args.getString("tap_hold"), 300));

    //     List<android.graphics.Point> points = new ArrayList<>();

    //     // Collect points dynamically
    //     for (int i = 0; ; i++) {
    //         String xs = args.getString("x" + i);
    //         String ys = args.getString("y" + i);
    //         if (xs == null || ys == null) break;
    //         points.add(new android.graphics.Point(
    //                 Integer.parseInt(xs),
    //                 Integer.parseInt(ys)
    //         ));
    //     }

    //     if (points.size() < 3) {
    //         showToast("ERROR: Need at least 3 points (1 base + 2 leaf points)");
    //         return;
    //     }

    //     android.graphics.Point base = points.get(0);
    //     int remaining = points.size() - 1;
    //     if (remaining % 2 != 0) {
    //         showToast("ERROR: Leaf points must be in groups of 2");
    //         return;
    //     }

    //     Random rand = new Random();
    //     int leafCount = remaining / 2;

    //     shouldStop = false;
    //     long startTime = System.currentTimeMillis();
    //     long durationMs = 60_000; // 1 minute

    //     while (!shouldStop &&
    //         (System.currentTimeMillis() - startTime) < durationMs) {

    //         for (int l = 0; l < leafCount; l++) {

    //             if (shouldStop) break;

    //             int idx = 1 + l * 2;
    //             android.graphics.Point p1 = points.get(idx);
    //             android.graphics.Point tip = points.get(idx + 1);

    //             // ---- TAP at base asynchronously using humanTap ----
    //             int jitterX = rand.nextInt(5) - 2;
    //             int jitterY = rand.nextInt(5) - 2;
    //             int tapX = base.x + jitterX;
    //             int tapY = base.y + jitterY;

    //             runAsyncHumanTap(tapX, tapY, tapHoldMs + rand.nextInt(10));
    //             sleep(tapDelayMs + rand.nextInt(5));
    //             // runAsyncHumanTap(tapX, tapY);
    //             // sleep(tapDelayMs + rand.nextInt(5));

    //             // ---- Curve Swipe asynchronously (base → p1 → tip → base) ----
    //             android.graphics.Point[] path = new android.graphics.Point[]{
    //                     new android.graphics.Point(base.x, base.y),
    //                     new android.graphics.Point(p1.x, p1.y),
    //                     new android.graphics.Point(tip.x, tip.y),
    //                     new android.graphics.Point(tip.x, tip.y),
    //                     new android.graphics.Point(base.x, base.y)
    //             };

    //             runAsyncSwipe(path, baseSteps);
    //             // try {
    //             //     device.swipe(path, baseSteps); // can stay synchronous
    //             // } catch (Exception e) {
    //             //     log("Swipe failed: " + e.getMessage());
    //             // }

    //             sleep(duration + rand.nextInt(5));
    //         }
    //     }

    //     log("tapAndSwipeNew stopped");
    // }

    private float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
