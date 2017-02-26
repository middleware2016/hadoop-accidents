import matplotlib.pyplot as plt
import os
from matplotlib.ticker import FuncFormatter

xlabels = []        # contributing factors name
accidents = []      # accidents
percLethal = []     # percentage of lethal accidents
width = 0.35

annotations = []

def clearAnnotations():
    """Delete all the existing tooltips"""

    global annotations
    for ann in annotations:
        ann.remove()
    annotations = []

def picked(event):
    """Display the tooltip for the selected point(s)"""

    clearAnnotations()

    indexes = event.ind
    # If multiple points are picked: join the labels
    desc = "\n".join([xlabels[i] for i in indexes])

    # The tooltip will be displayed in the position of the first selected point
    i = indexes[0]
    ann = plt.annotate(
        desc,
        xy=(accidents[i], percLethal[i]), xytext=(-20, 20),
        textcoords='offset points', ha='left', va='bottom',
        bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
        arrowprops=dict(arrowstyle = '->', connectionstyle='arc3,rad=0'))
    annotations.append(ann)
    plt.draw()

def processFile(file):
    with file as f:
        for row in f:
            tmp = row.split("\t")
            if( len(tmp) == 4):
                xlabels.append(tmp[0])
                accidents.append(int(tmp[2]))
                percLethal.append(float(tmp[3].replace(',', '.')))

def plot():
    fig, ax = plt.subplots()
    ax.scatter(accidents, percLethal, c="b", alpha=0.5,
            label="Cause", picker=True)

    ax.semilogx()
    ax.set_ylim(ymin=-0.001, ymax=0.013)
    plt.xlabel('Number of accidents')
    plt.ylabel('% of lethal accidents')

    percentageFormatter = FuncFormatter(lambda y, pos: '{:.2f}%'.format(y*100))
    ax.yaxis.set_major_formatter(percentageFormatter)

    fig.canvas.set_window_title('Contributing Factors for accidents')
    plt.title('Contributing Factors for accidents')
    plt.grid(True)

    fig.canvas.mpl_connect('pick_event', picked)
    plt.tight_layout()
    plt.show()

try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/ContributingFactors/part-r-00000", "r")
    processFile(file)
    plot()
except IOError as e:
    print("LethalPerWeek not generated")
