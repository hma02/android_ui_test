# /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w   -r -e debug false -e class com.example.uiautomator.PinchTest   com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

# /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w \
#   -r -e debug false \
#   -e class com.example.uiautomator.PinchTest \
#   -e resId "me.underw.hp:id/hl_mbmap_mapview" \
#   com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
# am instrument -w -r -e action pinch -e class com.example.uiautomator.UiActionTest -e resId "{resource_id}" com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

  /usr/bin/adb -s 192.168.1.127:5555 shell am instrument -w -r -e class com.example.uiautomator.UiActionTest \
	  -e action tap_and_swipe \
	  -e du 650 -e tap_delay 150 -e tap_hold 200 \
	  -e x0 280 -e x1 240 -e x2 361 -e x3 158 -e x4 82 \
	  -e x5 479 -e x6 639 -e x7 163 -e x8 185 -e x9 361 \
	  -e x10 530 -e x11 148 -e x12 120 -e x13 296 -e x14 451 \
	  -e x15 193 -e x16 269 -e x17 422 -e x18 597 \
	  -e y0 1170 -e y1 753 -e y2 369 -e y3 1401 -e y4 961 \
	  -e y5 754 -e y6 962 -e y7 1054 -e y8 533 -e y9 641 \
	  -e y10 534 -e y11 1240 -e y12 722 -e y13 668 -e y14 413 \
	  -e y15 887 -e y16 409 -e y17 669 -e y18 721 \
	  com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner


  #  /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w -r \
  # -e class com.example.uiautomator.UiActionTest \
  # -e action swipe \
  # -e du 650 \
  # -e startx 300 -e starty 1300 \
  # -e x1 410 -e y1 1283 \
  # -e x2 406 -e y2 1279 \
  # -e x3 37  -e y3 853 \
  # -e x4 573 -e y4 1289 \
  # -e x5 557 -e y5 1403 \
  # -e x6 117 -e y6 1603 \
  # -e x7 250 -e y7 13 \
  # com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner



  #  /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w -r \
  # -e class com.example.uiautomator.UiActionTest \
  # -e action swipe \
  # -e du 650 \
  # -e startx 300 -e starty 1300 \
  # -e x1 310 -e y1 1281 \
  # -e x2 312 -e y2 1279 \
  # -e x3 681 -e y3 851 \
  # -e x4 145 -e y4 1287 \
  # -e x5 161 -e y5 1401 \
  # -e x6 601 -e y6 1601 \
  # -e x7 490 -e y7 11 \
  # com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner


  # /usr/bin/adb -s 192.168.1.111:5555 shell am instrument -w -r \
  # -e class com.example.uiautomator.UiActionTest \
  # -e action swipe \
  # -e du 650 \
  # -e startx 300 -e starty 1300 \
  # -e x1 310 -e y1 1285 \
  # -e x2 316 -e y2 1279 \
  # -e x3 685 -e y3 855 \
  # -e x4 149 -e y4 1291 \
  # -e x5 165 -e y5 1405 \
  # -e x6 605 -e y6 1605 \
  # -e x7 450 -e y7 15 \
  # com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
