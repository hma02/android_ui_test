# example: open the photo gallery app on an samsung phone first, then try issue this adb command to phone and see zoom out effect
/usr/bin/adb shell am instrument -w -r \
  -e action pinch \
  -e speed 50 \
  -e resId "com.sec.android.gallery3d:id/photo_view" \
  com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner

