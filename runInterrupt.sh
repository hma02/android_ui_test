

/usr/bin/adb -s 192.168.1.127:5555 shell am instrument -w -r \
  -e action interrupt \
  -e class com.example.uiautomator.UiActionTest \
  com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

