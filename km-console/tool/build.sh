#!/bin/sh
set -ex
#检测node版本
echo "node version: " `node -v`
echo "npm version: " `npm -v`

pwd=`pwd`
echo "start install"
# npm run clean
npm run i
echo "install success"

echo "start build"
rm -rf pub/
lerna run build
echo "build success"