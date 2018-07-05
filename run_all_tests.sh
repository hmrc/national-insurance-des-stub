#!/usr/bin/env bash

sbt clean compile coverage test it:test coverageOff coverageReport
python dependencyReport.py national-insurance-des-stub