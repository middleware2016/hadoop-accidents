# hadoop-accidents
The goal of this project is to infer qualitative data regarding the car accidents in New York City.

## How to build

    # To compile and fetch dependencies
    gradle build
    
    # To run the queries on the cluster
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.ContributingFactor
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.LethalPerWeek
    hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.WeekBorough
