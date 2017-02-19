import matplotlib.pyplot as plt
import os
import numpy as np

x = []
xlabels = []
y = []

def processFile(file):
    with file as f:
        for i, row in enumerate(f):
            tmp = row.split("\t")
            if( len(tmp) == 2):
                xlabels.append(tmp[0])
                x.append(i)
                y.append(float(tmp[1]))

def plot():
    fig, ax = plt.subplots()
    ax.plot(x, y, 'k-', marker='o', markerfacecolor='r')
    #ax.set_xticks(x) #they are too many

    numOfLabels = 5
    subxLabels = []
    subx = np.linspace(0, len(x)-1, num=numOfLabels)
    for value in subx:
        subxLabels.append(xlabels[int(value)])
    ax.set_xticks(subx)
    ax.set_xticklabels(subxLabels)


    fig.canvas.set_window_title('Lethal per Week')
    plt.title('Lethal per Week')
    plt.ylabel('Lethal')
    plt.xlabel('Week')

    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/LethalPerWeek/part-r-00000", "r")
    processFile(file)
    plot()

except IOError as e:
    print("LethalPerWeek not generated")
