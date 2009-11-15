#!/bin/bash
export TERM=linux
COLUMNS=$1
LINES=$2
while true ; do
    x=$((RANDOM%COLUMNS))
    y=$((RANDOM%LINES))
    c=$((40+(RANDOM%10)))
    echo -en "\033[$y;${x}f\033[1;${c}m "
done