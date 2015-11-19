#! /bin/bash

rm -rf ./build
mkdir build
cp ./a4-asset-manager/target/a4-asset-manager-1.0-SNAPSHOT.jar ./build

docker build -t sbv_am .
