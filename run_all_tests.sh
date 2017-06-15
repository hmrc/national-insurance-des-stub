#!/bin/bash

set -e

sbt test
sbt it:test
