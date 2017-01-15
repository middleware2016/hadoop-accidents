# hadoop-accidents
The goal of this project is to infer qualitative data regarding the car accidents in New York City.

## TODO
- [ ] charts

## Input file

The input dataset can be downloaded from [here](http://home.deib.polimi.it/guinea/Materiale/Middleware/index.html).

## How to build

### To automatically set env and download sample data
Execute `init.sh`, this will download csv file and will create a `run.sh` script explained in the next section

### To compile and fetch dependencies
`gradle build`

## How to run
### Automatic way
Execute `run.sh` passing as argument `runAll` to run all queries or you can select only some of them (passing them as separated arguments) from the following list:
* ContributingFactors
* LethalPerWeek
* WeekBorough

for example `./run.sh LethalPerWeek WeekBorough`.

## Manual way    
    # To run the queries on the cluster
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.ContributingFactors file:///path/to/NYPD_Motor_Vehicle_Collisions.csv file:///path/to/output1
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.LethalPerWeek file:///path/to/NYPD_Motor_Vehicle_Collisions.csv file:///path/to/output2
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.WeekBorough file:///path/to/NYPD_Motor_Vehicle_Collisions.csv file:///path/to/output3
