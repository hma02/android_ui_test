

#!/bin/bash

DEVICES=(
    "192.168.1.111:5555"
    "192.168.1.127:5555"
    "192.168.1.220:5555"
    "192.168.1.221:5555"
)

read -p "Select device (0=all, 1-4=specific device): " DEVICE_NUM

if [[ "$DEVICE_NUM" == "0" ]]; then
    SELECTED_DEVICES=("${DEVICES[@]}")
elif [[ "$DEVICE_NUM" =~ ^[1-4]$ ]]; then
    SELECTED_DEVICES=("${DEVICES[$((DEVICE_NUM-1))]}")
else
    echo "Invalid selection. Please enter 0, 1, 2, 3, or 4."
    exit 1
fi

for DEVICE in "${SELECTED_DEVICES[@]}"; do
    echo
    echo "========================================"
    echo "Processing device: $DEVICE"
    echo "========================================"

    adb -s "$DEVICE" shell pm grant \
        com.example.uiautomator \
        android.permission.POST_NOTIFICATIONS

    adb -s "$DEVICE" shell cmd appops set \
        com.example.uiautomator POST_NOTIFICATION allow

    adb -s "$DEVICE" shell cmd appops set \
        com.example.uiautomator TOAST_WINDOW allow

    adb -s "$DEVICE" shell dumpsys package \
        com.example.uiautomator | grep -A30 "runtime permissions:"

    adb -s "$DEVICE" shell dumpsys notification | \
        grep -A2 -B2 "AppSettings: com.example.uiautomator"

    adb -s "$DEVICE" shell am instrument -w -r \
        -e action show_toast \
        -e text "Hello_test_toast" \
        com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
done



