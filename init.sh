#!/usr/bin/env bash

UNZIP="unzip"
CURRENT_PATH=`pwd`

#Download data
curl http://home.deib.polimi.it/guinea/Materiale/Middleware/NYPD_Motor_Vehicle_Collisions.zip -o data/NYPD_Motor_Vehicle_Collisions.zip

#unzip them
$UNZIP data/NYPD_Motor_Vehicle_Collisions.zip -d data

#create run script
echo "#!/usr/bin/env bash

SCRIPTS=(\"ContributingFactors\" \"LethalPerWeek\" \"WeekBorough\")


usage (){
    echo \"Usage: \$0 [\"\${SCRIPTS[*]} \"] | runAll\"
    exit 1
}

if [ \$# -lt 1 ]; then
    usage
fi

rm -rf $CURRENT_PATH/data/output

if [ \$1 == \"runAll\" ]; then
    for i in \"\${SCRIPTS[@]}\"; do
         hadoop jar \$i/build/libs/\$i.jar file:///$CURRENT_PATH/data/NYPD_Motor_Vehicle_Collisions.csv file:///$CURRENT_PATH/data/output/\$i
    done
else
    for i in \$@; do
         hadoop jar \$i/build/libs/\$i.jar file:///$CURRENT_PATH/data/NYPD_Motor_Vehicle_Collisions.csv file:///$CURRENT_PATH/data/output/\$i
    done
fi


" > run.sh

chmod +x run.sh