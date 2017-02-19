import matplotlib.pyplot as plt
import os
import numpy as np
from matplotlib.patches import Rectangle

xlabels = []    # weeks
data = {}       # dict: {borough: (week, numAccidents, numLethal)}

def processFile(file):
    with file as f:
        for value in f:
            tmp = value.split("\t")
            if( len(tmp) == 3):
                week, borough = tmp[0].split('_')
                if borough not in data:
                    data[borough] = []
                data[borough].append((week, int(tmp[1]), int(tmp[2])))
                if week not in xlabels:
                    xlabels.append(week)

def genLabelTicks(numTicks, labels):
    subxLabels = []
    subx = np.linspace(0, len(labels)-1, num=numTicks)
    subxLabels = [labels[int(i)] for i in subx]
    return subx, subxLabels

def plot():
    global xlabels, data

    # Common part
    fig, ax = plt.subplots()
    subx, subxLabels = genLabelTicks(10, xlabels)
    fig.canvas.set_window_title('WeekBorough')


    # First graph: accidents per week
    ax = plt.subplot(2, 1, 1)

    x = np.arange(len(xlabels))
    values = [[x[1] for x in data[borough]] for borough in data.keys()]
    stack_coll = ax.stackplot(x, values)

    ax.set_xticks(subx)
    ax.set_xticklabels(subxLabels)

    plt.title('Number of Accidents per Week')
    plt.ylabel('Accidents')
    plt.xlabel('Week')


    # Second graph: lethal per week
    ax = plt.subplot(2, 1, 2)

    x = np.arange(len(xlabels))
    values = [[x[2] for x in data[borough]] for borough in data.keys()]
    stack_coll = ax.stackplot(x, values)
    ax.set_xticks(subx)
    ax.set_xticklabels(subxLabels)

    # Legend
    proxy_rects = [Rectangle((0, 0), 1, 1, fc=pc.get_facecolor()[0]) for pc in stack_coll]
    ax.legend(proxy_rects, data.keys(), loc='lower center', bbox_to_anchor=(0.5, -0.5), ncol=6)

    plt.title('Lethal accidents per Week')
    plt.ylabel('Lethal accidents')
    plt.xlabel('Week')

    plt.tight_layout()
    fig.subplots_adjust(bottom=0.2)

    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/WeekBorough/part-r-00000", "r")
    processFile(file)
    plot()
except IOError as e:
    print("WeekBorough not generated")
