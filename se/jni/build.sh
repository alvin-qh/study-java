#!/bin/sh

set -e

if [ ! -d "build" ]; then
  mkdir build
fi

if [ ! -d "target" ]; then
  mkdir target
fi

cd build
cmake ..
make
