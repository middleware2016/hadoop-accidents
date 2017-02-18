import matplotlib.pyplot as plt
import os

def plot( file ):
    x = []      # [1, 2, ..., n]: index of contributing factors
    x2 = []     # x + width
    xlabels = []# contributing factors name
    y = []      # accidents
    y2 = []     # average number of deaths
    width = 0.35

    with file as row:
        for i, value in enumerate(row):
            tmp = value.split("\t")
            if( len(tmp) == 3):
                xlabels.append(tmp[0])
                x.append(i)
                x2.append(i+width)
                y.append(float(tmp[1].replace(',', '.')))
                y2.append(float(tmp[2].replace(',', '.')))

    #X and Y are swapped
    fig, ax1 = plt.subplots()
    rects1 = ax1.barh(x, y, width, color='r')
    ax1.set_xlabel('Avg. Deaths', color='r')
    ax1.tick_params('x', colors='r')

    ax2 = ax1.twiny()
    rects2 = ax2.barh(x2, y2, width, color='b')
    ax2.set_xlabel('N. Accidents', color='b')
    ax2.tick_params('x', colors='b')

    ax1.set_yticks(x)
    ax1.set_yticklabels(xlabels)

    fig.canvas.set_window_title('Contributing Factors')

    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/ContributingFactors/part-r-00000", "r")
    plot(file)
except IOError as e:
    print("LethalPerWeek not generated")
