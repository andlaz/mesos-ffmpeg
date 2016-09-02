#!/bin/bash
echo $NC_HOST
echo $VIDEO_URL
echo $NC_PORT
exec ffmpeg -i $VIDEO_URL -f mpegts - | nc -l $NC_HOST $NC_PORT
