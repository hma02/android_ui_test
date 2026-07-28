#!/usr/bin/env bash
set -e

######################################
# Defaults
######################################
SCREEN_WIDTH=720

DEVICE_KEY="device1"
DEVICE="192.168.1.111:5555"

DIRECTION="right"
SHORT="false"

######################################
# Parse arguments (order-independent)
######################################
for arg in "$@"; do
    case "$arg" in
        device1)
            DEVICE_KEY="device1"
            DEVICE="192.168.1.111:5555"
            ;;
        device2)
            DEVICE_KEY="device2"
            DEVICE="192.168.1.127:5555"
            ;;
        device3)
            DEVICE_KEY="device3"
            DEVICE="192.168.1.220:5555"
            ;;
        device4)
            DEVICE_KEY="device4"
            DEVICE="192.168.1.221:5555"
            ;;
        left|right)
            DIRECTION="$arg"
            ;;
        short)
            SHORT="true"
            ;;
        *)
            echo "[WARN] Unknown argument ignored: $arg"
            ;;
    esac
done

######################################
# Common params
######################################
DU=650
STARTX=220
STARTY=1300

######################################
# Coordinate definitions
######################################
if [[ "$SHORT" == "true" ]]; then
    echo "[INFO] Using SHORT swipe pattern"

    # 6x repeat of (startx, starty) followed by (320, 200)
    X1=$STARTX ; Y1=$STARTY
    X2=$STARTX ; Y2=$STARTY
    X3=$STARTX ; Y3=$STARTY
    X4=$STARTX ; Y4=$STARTY
    X5=$STARTX ; Y5=$STARTY
    X6=$STARTX ; Y6=$STARTY
    X7=270     ; Y7=550
else
    echo "[INFO] Using NORMAL swipe pattern"

    X1=280  ; Y1=1279
    X2=450  ; Y2=1200
    X3=683  ; Y3=853
    X4=50   ; Y4=1289
    X5=147  ; Y5=1403
    X6=400  ; Y6=1200
    X7=900  ; Y7=20
fi

######################################
# Mirror X coordinates if direction=right
######################################
mirror_x () {
    echo $(( SCREEN_WIDTH - $1 ))
}

if [[ "$DIRECTION" == "left" ]]; then
    echo "[INFO] Mirroring coordinates for LEFT swipe"

    STARTX=$(mirror_x "$STARTX")

    X1=$(mirror_x "$X1")
    X2=$(mirror_x "$X2")
    X3=$(mirror_x "$X3")
    X4=$(mirror_x "$X4")
    X5=$(mirror_x "$X5")
    X6=$(mirror_x "$X6")
    X7=$(mirror_x "$X7")
fi

######################################
# Summary
######################################
echo "[INFO] Device key : $DEVICE_KEY"
echo "[INFO] Device     : $DEVICE"
echo "[INFO] Direction  : $DIRECTION"
echo "[INFO] Short mode : $SHORT"

######################################
# Run adb instrumentation
######################################
exec /usr/bin/adb -s "$DEVICE" shell am instrument -w -r \
    -e class com.example.uiautomator.UiActionTest \
    -e action swipe \
    -e du "$DU" \
    -e startx "$STARTX" -e starty "$STARTY" \
    -e x1 "$X1" -e y1 "$Y1" \
    -e x2 "$X2" -e y2 "$Y2" \
    -e x3 "$X3" -e y3 "$Y3" \
    -e x4 "$X4" -e y4 "$Y4" \
    -e x5 "$X5" -e y5 "$Y5" \
    -e x6 "$X6" -e y6 "$Y6" \
    -e x7 "$X7" -e y7 "$Y7" \
    com.example.uiautomator.test/androidx.test.runner.AndroidJUnitRunner
