#!/usr/bin/env bash

python3 plots/LethalPerWeek.py </dev/null  2>&1 &
python3 plots/ContributingFactors_scatter.py </dev/null  2>&1 &
python3 plots/WeekBorough.py </dev/null  2>&1 &