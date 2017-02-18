import matplotlib.pyplot as plt
import os
import AnnoteFinder

xlabels = []# contributing factors name
accidents = []      # accidents
deaths = []     # average number of deaths
width = 0.35

# TODO tooltips!
def picked(event):
    index = event.ind
    print('--------------')
    print(index)
    for i in index:
        print(xlabels[i]) # prints to the console the contributing factor associated with the point

def plot( file ):
    with file as row:
        for i, value in enumerate(row):
            tmp = value.split("\t")
            if( len(tmp) == 3):
                xlabels.append(tmp[0])
                deaths.append(float(tmp[1].replace(',', '.')))
                accidents.append(float(tmp[2].replace(',', '.')))

    fig, ax = plt.subplots()
    ax.scatter(accidents, deaths, c="b", alpha=0.5,
            label="Cause", picker=True)
    ax.semilogx()

    ax.set_ylim(ymin=-0.001)
    plt.xlabel("Num. accidents")
    plt.ylabel("Avg. deaths")
    plt.grid(True)
    fig.canvas.mpl_connect('pick_event', picked) # "button_press_event"
    plt.show()


try:
    file = open(os.path.dirname(os.path.abspath(__file__))+"/../data/output/ContributingFactors/part-r-00000", "r")
    plot(file)
except IOError as e:
    print("LethalPerWeek not generated")
