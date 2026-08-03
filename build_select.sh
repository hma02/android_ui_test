export ANDROID_SDK_ROOT=$HOME/android-sdk

echo "Select device to install:"
echo "0) All devices"
echo "1) 192.168.1.111:5555"
echo "2) 192.168.1.127:5555"
echo "3) 192.168.1.220:5555"
echo "4) 192.168.1.221:5555"

read -p "Enter choice [0-4]: " DEVICE_CHOICE

case "$DEVICE_CHOICE" in
    0)
        DEVICES=(
            "192.168.1.111:5555"
            "192.168.1.127:5555"
            "192.168.1.220:5555"
            "192.168.1.221:5555"
        )
        ;;
    1) DEVICES=("192.168.1.111:5555") ;;
    2) DEVICES=("192.168.1.127:5555") ;;
    3) DEVICES=("192.168.1.220:5555") ;;
    4) DEVICES=("192.168.1.221:5555") ;;
    *)
        echo "Invalid choice."
        exit 1
        ;;
esac

#export ANDROID_SDK_ROOT=~/android-sdk
#rm -rf ~/.gradle/caches/
#./gradlew build --refresh-dependencies

# Note: since nanopi-m6 is a arm64 device, so during apk build, the AAPT2 binary is specially prepared and suggested to use below config in gradle.properties:
# android.aapt2FromMavenOverride=/home/pi/software/apktool/aapt2

WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"

echo "Checking for Gradle wrapper..."

if [ -f "$WRAPPER_JAR" ]; then
    echo "✓ gradle-wrapper.jar exists."
else
    echo "✗ gradle-wrapper.jar NOT found."

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


for DEVICE in "${DEVICES[@]}"; do
    echo "Installing to $DEVICE..."
    /usr/bin/adb -s "$DEVICE" uninstall com.example.uiautomator.test
    /usr/bin/adb -s "$DEVICE" uninstall com.example.uiautomator
    /usr/bin/adb -s "$DEVICE" install -r \
        app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

    /usr/bin/adb -s "$DEVICE" install -r \
        app/build/outputs/apk/debug/app-debug.apk
done

export ANDROID_SDK_ROOT=/usr/lib/android-sdk


