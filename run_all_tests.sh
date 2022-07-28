#!/usr/bin/env bash

sbt clean scalafmt compile scalastyle coverage test it:test coverageOff dependencyUpdates coverageReport
