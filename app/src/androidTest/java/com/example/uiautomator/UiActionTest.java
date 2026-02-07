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

@RunWith(AndroidJUnit4.class)
public class UiActionTest {

    private UiDevice device;
    private Bundle args;

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
                doQuickTap();
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

    private void doQuickTap() {
        int x = parseInt(args.getString("x"), -1);
        int y = parseInt(args.getString("y"), -1);

        if (x < 0 || y < 0) {
            log("ERROR: x/y not provided for quick_tap");
            return;
        }

        int taps = parseInt(args.getString("n"), 16);
        int intervalMs = 300;

        Random rand = new Random();

        for (int i = 0; i < taps; i++) {
            int offsetX = rand.nextInt(5) - 2;  // -2 to +2
            int offsetY = rand.nextInt(5) - 2;

            // device.click(x + offsetX, y + offsetY);
            device.swipe(x+ offsetX, y+ offsetY, x+ offsetX+1, y+ offsetY+1, 8);

            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException ignored) {}
        }
    }


    
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

    // ---------------- TAP AND SWIPE ----------------
    private void tapAndSwipe() {

        int duration = parseInt(args.getString("du"), 500);
        int tapDelayMs = parseInt(args.getString("tap_delay"), 80);

        // NEW: loop count
        int nLoop = parseInt(args.getString("n_loop"), 1);
        int tapHoldMs = parseInt(args.getString("tap_hold"), 32);  // 16ms = 1 step - > shortest tap

        // Collect points
        List<android.graphics.Point> points = new ArrayList<>();
        for (int i = 0; ; i++) {
            int x = parseInt(args.getString("x" + i), -1);
            int y = parseInt(args.getString("y" + i), -1);
            if (x < 0 || y < 0) break;
            points.add(new android.graphics.Point(x, y));
        }

        if (points.size() < 2) {
            log("ERROR: tap_and_swipe requires at least 2 points");
            return;
        }

        int steps = Math.max(5, duration / 60);

        // --- LOOP ---
        for (int loop = 0; loop < nLoop; loop++) {

            for (int i = 0; i < points.size() - 1; i++) {
                android.graphics.Point p1 = points.get(i);
                android.graphics.Point p2 = points.get(i + 1);

                // Tap at Pi
                humanTap(p1.x, p1.y, tapHoldMs);
                // device.click(p1.x, p1.y);
                sleep(tapDelayMs);

                // Swipe Pi -> P(i+1)
                device.swipe(
                        new android.graphics.Point[]{p1, p2},
                        steps
                );
            }

            // Final tap
            android.graphics.Point last = points.get(points.size() - 1);
            humanTap(last.x, last.y, tapHoldMs);
            sleep(tapDelayMs);
        }
    }


    private void tapAndSwipeOld() {

        int duration = parseInt(args.getString("du"), 500);
        int tapDelayMs = parseInt(args.getString("tap_delay"), 80);

        int nLoop = parseInt(args.getString("n_loop"), 1);

        // Samsung Note8: enforce minimum realistic hold
        int tapHoldMs = Math.max(45, parseInt(args.getString("tap_hold"), 60));

        List<android.graphics.Point> points = new ArrayList<>();
        for (int i = 0; ; i++) {
            int x = parseInt(args.getString("x" + i), -1);
            int y = parseInt(args.getString("y" + i), -1);
            if (x < 0 || y < 0) break;
            points.add(new android.graphics.Point(x, y));
        }

        if (points.size() < 2) {
            log("ERROR: tap_and_swipe requires at least 2 points");
            return;
        }

        Random rand = new Random();

        // More steps = more reliable on Samsung
        int baseSteps = Math.max(8, duration / 40);

        for (int loop = 0; loop < nLoop; loop++) {

            for (int i = 0; i < points.size() - 1; i++) {

                android.graphics.Point p1 = points.get(i);
                android.graphics.Point p2 = points.get(i + 1);

                // ---- TAP (with jitter) ----
                int jitterX = rand.nextInt(5) - 2;   // -2 to +2
                int jitterY = rand.nextInt(5) - 2;

                int holdVar = rand.nextInt(20);      // +0–20ms variation

                humanTap(
                    p1.x + jitterX,
                    p1.y + jitterY,
                    tapHoldMs + holdVar
                );

                sleep(tapDelayMs + rand.nextInt(30));

                // ---- SWIPE (with step jitter) ----
                int steps = baseSteps + rand.nextInt(4);  // slight variation

                device.swipe(
                    new android.graphics.Point[]{
                            new android.graphics.Point(p1.x, p1.y),
                            new android.graphics.Point(p2.x, p2.y)
                    },
                    steps
                );

                sleep(60 + rand.nextInt(40));
            }

            // ---- FINAL TAP ----
            android.graphics.Point last = points.get(points.size() - 1);

            int jitterX = rand.nextInt(5) - 2;
            int jitterY = rand.nextInt(5) - 2;

            humanTap(
                last.x + jitterX,
                last.y + jitterY,
                tapHoldMs + rand.nextInt(20)
            );

            sleep(tapDelayMs + rand.nextInt(30));
        }
    }

    private void tapAndSwipeNew() {

        int duration = parseInt(args.getString("du"), 500);
        int baseSteps = Math.max(6, duration / 40);
        int tapDelayMs = parseInt(args.getString("tap_delay"), 30);
        int tapHoldMs = Math.max(45, parseInt(args.getString("tap_hold"), 100));
        int nLoop = parseInt(args.getString("n_loop"), 1);

        List<android.graphics.Point> points = new ArrayList<>();

        // Collect points dynamically (x0/y0 = base)
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

        // After base, remaining points must be groups of 2
        int remaining = points.size() - 1;

        if (remaining % 2 != 0) {
            showToast("ERROR: Leaf points must be in groups of 2");
            return;
        }

        Random rand = new Random();
        int leafCount = remaining / 2;

        for (int loop = 0; loop < nLoop; loop++) {
            for (int l = 0; l < leafCount; l++) {

                int idx = 1 + l * 2;

                android.graphics.Point p1 = points.get(idx);   // left/side of leaf
                android.graphics.Point tip = points.get(idx + 1); // tip of leaf

                // ---- TAP at base ----
                int jitterX = rand.nextInt(5) - 2;
                int jitterY = rand.nextInt(5) - 2;
                humanTap(base.x + jitterX, base.y + jitterY, tapHoldMs + rand.nextInt(10));
                sleep(tapDelayMs + rand.nextInt(5));
                humanTap(base.x + jitterX, base.y + jitterY, tapHoldMs + rand.nextInt(10));
                sleep(tapDelayMs + rand.nextInt(5));
                // ---- Curve Swipe (base → p1 → tip → base) ----
                android.graphics.Point[] path = new android.graphics.Point[] {
                        new android.graphics.Point(base.x, base.y),
                        new android.graphics.Point(p1.x, p1.y),
                        new android.graphics.Point(tip.x, tip.y),
                        new android.graphics.Point(base.x, base.y)
                };

                device.swipe(path, baseSteps);

                sleep(tapDelayMs + rand.nextInt(5));
            }
        }
    }

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
