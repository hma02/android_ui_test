# /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w   -r -e debug false -e class com.example.uiautomator.PinchTest   com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

# /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w \
#   -r -e debug false \
#   -e class com.example.uiautomator.PinchTest \
#   -e resId "me.underw.hp:id/hl_mbmap_mapview" \
#   com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
# am instrument -w -r -e action pinch -e class com.example.uiautomator.UiActionTest -e resId "{resource_id}" com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

  /usr/bin/adb -s 192.168.1.127:5555 shell am instrument -w -r \
  -e action pinch \
  -e resId "com.nianticlabs.pokemongo:id/unitySurfaceView" \
  com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner


  #   /usr/bin/adb -s 192.168.1.127:5555 shell am instrument -w -r \
  # -e action pinch \
  # -e resId  "android:id/android.widget.LinearLayout" \
  # com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
