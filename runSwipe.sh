  /usr/bin/adb shell am instrument -w -r \
  -e class com.example.uiautomator.UiActionTest \
  -e action swipe \
  -e du 650 \
  -e startx 220 -e starty 1300 \
  -e x1 280 -e y1 1279 \
  -e x2 450 -e y2 1200 \
  -e x3 683 -e y3 853 \
  -e x4 50 -e y4 1289 \
  -e x5 147 -e y5 1403 \
  -e x6 400 -e y6 1200 \
  -e x7 900 -e y7 20 \
  com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner


