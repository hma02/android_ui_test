export ANDROID_SDK_ROOT=$HOME/android-sdk

./gradlew clean :app:assembleDebug :app:assembleAndroidTest
/usr/bin/adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
/usr/bin/adb install -r app/build/outputs/apk/debug/app-debug.apk

# below command is for finding out what ui to be pinched on, feed this output to chatGPT to let it figure out
#/usr/bin/adb -s 192.168.1.111:5555 shell uiautomator dump /sdcard/ui.xml
#/usr/bin/adb -s 192.168.1.111:5555 pull /sdcard/ui.xml .

/usr/bin/adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
/usr/bin/adb install -r app/build/outputs/apk/debug/app-debug.apk

export ANDROID_SDK_ROOT=/usr/lib/android-sdk

#bash runPinch.sh
#bash runSwipe.sh


