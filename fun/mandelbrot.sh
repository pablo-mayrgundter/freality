#!/bin/zsh
export TERM=linux
COLUMNS=${COLUMNS:-$1}
LINES=${LINES:-$2}
# z_n+1 = z_n^2 + c
N=0;
function mand {
  c1=$1
  c2=$2
  z1=$c1
  z2=$c2
  for (( n=0; n < 100 ; n++ )) ; do
    t=$(( (z1 ** 2) - (z2 ** 2) + c1 ))
    z2=$(( (2 * z1 * z2) + c2 ))
    z1=$t
    if (( (z1**2 + z2**2) > 4 )) ; then
      N=$n;
      return;
    fi
  done
  N=$n
  return;
}

width=$COLUMNS
height=$LINES
for (( h=1; h <= height; h++ )) ; do
  for (( w=1; w <= width; w++ )) ; do
    W=$(( w + 0.0 )) # to float
    H=$(( h + 0.0 ))
    c1=$(( -2.0 + (( $W / $width) * 3.0) ))
    c2=$(( 1.2 - (( $H / $height) * 2.4) ))
    mand $c1 $c2
    # clamp colors
    if (( N > 9 )) ; then
      N=9
    fi
    color=$(( 40 + N ))
    echo -en "\033[${h};${w}f\033[1;${color}m "
  done
done
