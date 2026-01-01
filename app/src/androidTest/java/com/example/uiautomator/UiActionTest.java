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
// ---------------- QUICK TAP ----------------
    private void doQuickTap() {
        int x = parseInt(args.getString("x"), -1);
        int y = parseInt(args.getString("y"), -1);

        if (x < 0 || y < 0) {
            log("ERROR: x/y not provided for quick_tap");
            return;
        }

        int taps = 8;                 // 8 taps
        int intervalMs = 250;         // 4 taps per second (1000 / 4)

        for (int i = 0; i < taps; i++) {
            device.click(x, y);
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
            target.pinchClose(0.8f, speed);
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

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
