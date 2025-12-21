package com.example.uiautomator;

import android.graphics.Point;
import android.os.Bundle;
import android.graphics.Rect;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.By;
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
        device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation()
        );
        args = InstrumentationRegistry.getArguments();
    }

    @Test
    public void runAction() {

        String action = args.getString("action");

        if (action == null) {
            if (args.getString("resId") != null) {
                action = "pinch";   // backward compatibility
            } else {
                log("ERROR: action not provided");
                return;
            }
        }

        switch (action) {
            case "pinch":
                doPinch();
                break;

            case "swipe":
                doCurveSwipe();
                break;

            default:
                log("ERROR: unknown action: " + action);
        }
    }

    // ---------------- PINCH ----------------

    private void doPinch() {
        String resId = args.getString("resId");

        if (resId == null || resId.isEmpty()) {
            log("ERROR: resId missing for pinch");
            return;
        }

        UiObject2 target = device.wait(
                Until.findObject(By.res(resId)),
                5000
        );

        if (target != null) {
            // Get the bounds of the original target

            int speed = parseInt(args.getString("speed"), 65); // you can tweak for speed, pixels per second

            // Perform pinchClose scaled to safe rectangle
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

        List<Point> points = new ArrayList<>();

        // start point
        points.add(new Point(startX, startY));

        // curve points x1,y1 ...
        for (int i = 1; ; i++) {
            String xs = args.getString("x" + i);
            String ys = args.getString("y" + i);
            if (xs == null || ys == null) break;

            points.add(new Point(
                    Integer.parseInt(xs),
                    Integer.parseInt(ys)
            ));
        }

        if (points.size() < 2) {
            log("ERROR: swipe requires at least 2 points");
            return;
        }

        // UiAutomator expects Point[]
        Point[] path = points.toArray(new Point[0]);

        // steps roughly maps to duration
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
