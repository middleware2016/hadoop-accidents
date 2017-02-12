-- SQL queries to double-check the results.

-- To import the data
load data infile 'prova.csv' into table nypd fields terminated by ',' optionally enclosed by '"' ignore 1 lines;
update nypd set DATE_OK = STR_TO_DATE(DATE, "%m/%d/%Y")

-- LethalPerWeek
SELECT YEARWEEK(DATE_OK, 3), count(*) FROM `nypd`
WHERE `NUMBER OF PERSONS KILLED` >= 1
GROUP BY YEARWEEK(DATE_OK, 3)

-- Contributing factors
select cf, count(*), avg(`NUMBER OF PERSONS KILLED`) from (
SELECT `CONTRIBUTING FACTOR VEHICLE 1` as cf, `NUMBER OF PERSONS KILLED`
FROM `nypd` as n1
UNION ALL
SELECT `CONTRIBUTING FACTOR VEHICLE 2` as cf, `NUMBER OF PERSONS KILLED`
FROM `nypd` as n2
UNION ALL
SELECT `CONTRIBUTING FACTOR VEHICLE 3` as cf, `NUMBER OF PERSONS KILLED`
FROM `nypd` as n3
UNION ALL
SELECT `CONTRIBUTING FACTOR VEHICLE 4` as cf, `NUMBER OF PERSONS KILLED`
FROM `nypd` as n4
UNION ALL
SELECT `CONTRIBUTING FACTOR VEHICLE 5` as cf, `NUMBER OF PERSONS KILLED`
FROM `nypd` as n5
) as t
where cf not like "Unspecified" and cf not like "" and cf is not null
group by cf

-- WeekBorough
select YEARWEEK(DATE_OK, 3) as YearWeek, `BOROUGH`, count(*) as Accidents, SUM(CASE WHEN `NUMBER OF PERSONS KILLED` > 0 THEN 1 ELSE 0 END) as LethalAccidents
from nypd
group by YearWeek, `BOROUGH`
order by YearWeek, `BOROUGH`
