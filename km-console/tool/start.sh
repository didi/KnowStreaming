#!/bin/sh
set -ex
# rm -rf node_modules package-lock.json packages/*/node_modules packages/*/package-lock.json yarn.lock packages/*/yarn.lock

#检测node版本
echo "node version: " `node -v`
echo "npm version: " `npm -v`

pwd=`pwd`
echo "start develop"
npm run i

echo "本地开发请打开 http://localhost:8000"

lerna run start
echo "start success"

