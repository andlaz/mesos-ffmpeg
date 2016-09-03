#!/bin/bash
echo $VIDEO_URL
exec ffmpeg -i $VIDEO_URL -f mpegts - | nc -l 0.0.0.0 8999
