#!/usr/bin/env bash

find . -name target | xargs rm -rf

find . -name .data | xargs rm -rf

find . -name build | xargs rm -rf

find . -name *.log | xargs rm -f

rm -rf $HOME/logs $HOME/nacos
