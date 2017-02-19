import matplotlib.pyplot as plt
import os
import numpy as np
from matplotlib.patches import Rectangle

def toList(dictionary, key):
    ret = []
    for value in dictionary[key].values():
        ret.append(int(value))
    #print ret
    return ret

def plot( file ):
    inputData = file.read().split("\n")

    xlabels = []
    data = {}
    data2 = {}


    i = 0
    for value in inputData:
        tmp = value.split("\t")
        if( len(tmp) == 3):
            keys = tmp[0].split('_')
            if keys[1] not in data:
                data[keys[1]] = {}
            if keys[1] not in data2:
                data2[keys[1]] = {}
            data[keys[1]][keys[0]] = tmp[1]
            data2[keys[1]][keys[0]] = tmp[2]


    fig, ax = plt.subplots()

    ax = plt.subplot(2, 1, 1)
    keys = data.keys()
    labels = []
    values = []
    for value in keys:
        ytmp = toList(data, value)
        labels.append(value)
        values.append(ytmp)

    x = np.arange(len(data[keys[0]].keys()))
    stack_coll = ax.stackplot(x, values)
    proxy_rects = [Rectangle((0, 0), 1, 1, fc=pc.get_facecolor()[0]) for pc in stack_coll]
    ax.legend(proxy_rects, labels)
    xlabels = data[keys[0]].keys()

    ax.set_xticklabels(xlabels)
    plt.title('Number of Accidents per Week')
    plt.ylabel('Accidents')
    plt.xlabel('Week')

    ax = plt.subplot(2, 1, 2)
    keys = data2.keys()
    labels = []
    values = []
    for value in keys:
        ytmp = toList(data2, value)
        labels.append(value)
        values.append(ytmp)

    x = np.arange(len(data[keys[0]].keys()))
    stack_coll = ax.stackplot(x, values)
    proxy_rects = [Rectangle((0, 0), 1, 1, fc=pc.get_facecolor()[0]) for pc in stack_coll]
    ax.legend(proxy_rects, labels)
    xlabels = data2[keys[0]].keys()

    ax.set_xticklabels(xlabels)
    plt.title('Lethal per Week')
    plt.ylabel('Lethal')
    plt.xlabel('Week')


    fig.canvas.set_window_title('Week Brough')



    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/WeekBorough/part-r-00000", "r")
    plot(file)
except IOError as e:
    print("LethalPerWeek not generated")
