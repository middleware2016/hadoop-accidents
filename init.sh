#!/usr/bin/env bash

UNZIP="unzip"
PATH=`pwd`

#Download data
curl http://home.deib.polimi.it/guinea/Materiale/Middleware/NYPD_Motor_Vehicle_Collisions.zip -o  data/NYPD_Motor_Vehicle_Collisions.zip

#unzip them
$UNZIP data/NYPD_Motor_Vehicle_Collisions.zip -d data

#create run script
echo "#!/usr/bin/env bash

SCRIPTS=(\"ContributingFactors\" \"LethalPerWeek\" \"WeekBorough\")


usage (){
echo \"Usage: \$0 [\"\${SCRIPTS[*]} \"]| runAll\"
exit 1
}

if [ \$# -lt 1 ]; then
    usage
fi

rm -rf $PATH/data/output

if [ \$1 == \"runAll\" ]; then
    for i in \$SCRIPTS; do
         hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.\$i file:///$PATH/data/NYPD_Motor_Vehicle_Collisions.csv file:///$PATH/data/output/\$i
    done
else
    for i in \$@; do
         hadoop jar build/libs/hadoop-accidents-1.0-SNAPSHOT.jar mw.hadoop.queries.\$i file:///$PATH/data/NYPD_Motor_Vehicle_Collisions.csv file:///$PATH/data/output/\$i
    done
fi


" > run.sh

/bin/chmod +x run.sh