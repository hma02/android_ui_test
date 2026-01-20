package com.example.uiautomator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btn = new Button(this);
        btn.setText("This is UIAutomator Test App");
        setContentView(btn);
    }
}
