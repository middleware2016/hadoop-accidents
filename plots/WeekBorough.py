import matplotlib.pyplot as plt
import os
import numpy as np

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
    for value in keys:
        ytmp = toList(data, value)
        ax.plot(np.arange(len(ytmp)), ytmp, 'o-')

    xlabels = data[keys[0]].keys()

    #ax.set_xticks(x) #they are too many
    ax.set_xticklabels(xlabels)
    plt.title('Number of Accidents per Week')
    plt.ylabel('Accidents')
    plt.xlabel('Week')

    ax = plt.subplot(2, 1, 2)
    keys = data2.keys()
    for value in keys:
        ytmp = toList(data2, value)
        ax.plot(np.arange(len(ytmp)), ytmp, 'o-')

    xlabels = data2[keys[0]].keys()

    #ax.set_xticks(x) #they are too many
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
