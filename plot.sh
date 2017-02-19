#!/usr/bin/env bash

python plots/LethalPerWeek.py </dev/null  2>&1 &
python plots/ContributingFactors.py </dev/null  2>&1 &
python plots/ContributingFactors_scatter.py </dev/null  2>&1 &
python plots/WeekBorough.py </dev/null  2>&1 &