#!/usr/bin/env bash

sbt clean compile scalastyle coverage test it:test coverageOff dependencyUpdates coverageReport
python dependencyReport.py national-insurance-des-stub