"""
Demo of spines using custom bounds to limit the extent of the spine.
"""
import numpy as np
import matplotlib.pyplot as plt
import os

def plot( file ):
    data = file.read().split("\n")

    x = []
    xlabels = []
    y = []

    i = 0
    for value in data:
        tmp = value.split("\t")
        if( len(tmp) == 2):
            xlabels.append(tmp[0])
            x.append(i)
            i+=1
            y.append(tmp[1])

    fig, ax = plt.subplots()
    ax.plot(x, y, 'o-')
    #ax.set_xticks(x) #they are too many
    ax.set_xticklabels(xlabels)

    fig.canvas.set_window_title('Lethal per Week')
    plt.title('Lethal per Week')
    plt.ylabel('Lethal')
    plt.xlabel('Week')

    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/LethalPerWeek/part-r-00000", "r")
    plot(file)
except IOError as e:
    print("LethalPerWeek not generated")
