export ANDROID_SDK_ROOT=$HOME/android-sdk

#export ANDROID_SDK_ROOT=~/android-sdk
#rm -rf ~/.gradle/caches/
#./gradlew build --refresh-dependencies



WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"

echo "Checking for Gradle wrapper..."

if [ -f "$WRAPPER_JAR" ]; then
    echo "✓ gradle-wrapper.jar exists."
else
    echo "✗ gradle-wrapper.jar NOT found."

    # Check if gradle is installed
    if ! command -v gradle >/dev/null 2>&1; then
        echo "ERROR: 'gradle' is not installed."
        echo "Install it first with:"
        echo "  sudo apt install gradle"
        exit 1
    fi

    echo "Running 'gradle wrapper' to regenerate wrapper..."
    gradle wrapper

    if [ -f "$WRAPPER_JAR" ]; then
        echo "✓ Wrapper successfully regenerated."
    else
        echo "✗ Failed to generate gradle-wrapper.jar"
        exit 1
    fi
fi


./gradlew clean :app:assembleDebug :app:assembleAndroidTest



/usr/bin/adb -s 192.168.1.111:5555 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
/usr/bin/adb -s 192.168.1.111:5555 install -r app/build/outputs/apk/debug/app-debug.apk
/usr/bin/adb -s 192.168.1.127:5555 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
/usr/bin/adb -s 192.168.1.127:5555 install -r app/build/outputs/apk/debug/app-debug.apk



export ANDROID_SDK_ROOT=/usr/lib/android-sdk

#bash runPinch.sh
#bash runSwipe.sh

# below command is for finding out what ui to be pinched on, feed this output to chatGPT to let it figure out
#/usr/bin/adb -s 192.168.1.111:5555 shell uiautomator dump /sdcard/ui.xml
#/usr/bin/adb -s 192.168.1.111:5555 pull /sdcard/ui.xml .



# adb -s 192.168.1.111:5555 shell settings put global enable_accessibility_global_gesture_enabled 1
# adb -s 192.168.1.111:5555 shell appops set com.example.uiautomator SYSTEM_ALERT_WINDOW allow
# adb -s 192.168.1.111:5555 shell pm grant com.example.uiautomator android.permission.SYSTEM_ALERT_WINDOW

# adb -s 192.168.1.111:5555 shell am start -a android.settings.action.MANAGE_OVERLAY_PERMISSION -d package:com.example.uiautomator
# adb -s 192.168.1.111:5555 shell am start -a android.settings.action.MANAGE_OVERLAY_PERMISSION -d package:com.example.uiautomator

# adb -s 192.168.1.111:5555 shell am start-foreground-service -n com.example.uiautomator/.OverlayService



