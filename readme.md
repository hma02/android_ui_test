## Objective:
- need a way to send adb shell command to android device to perform actions like zoom out (pinch close) and multi-point swipe gesture  on screen
- since no existing android broadcast for this purpose, so we need to build an app that can accepts adb instrument requests
- the resulting actions performed in this way is way smoother than using third party apps, e.g., MacroDroid

## Dependencies
- gradle-8.3-bin.zip
- jdk 17
- wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip
- sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "extras;android;m2repository"

## How to Build
```bash
gradle wrapper
yes | sdkmanager --licenses
sdkmanager "platforms;android-33" "build-tools;34.0.0"
./gradlew build
bash build.sh
```

## Pinch

- Note: you need to identify the UI object resource_id in order to pinch, for example, when using an photos app, resource_id ="com.sec.android.gallery3d:id/photo_view"

- if you don't know what is the resource_id, try locate it using below command

```bash
# below command is for finding out what ui to be pinched on, feed this output to chatGPT to let it figure out
/usr/bin/adb shell uiautomator dump /sdcard/ui.xml
/usr/bin/adb pull /sdcard/ui.xml .
```
