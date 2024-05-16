#!/usr/bin/env bash

find . -name target | xargs rm -rf

find . -name build | xargs rm -rf

rm -rf $HOME/logs $HOME/nacos
