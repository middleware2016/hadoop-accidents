package mw.hadoop.queries;

import org.apache.commons.csv.CSVFormat;

/**
 * CSV format to use with CSVParser.
 * The header row will be sent to only one mapper, so we need to report it here.
 */
public class NYPD_Keys {
    public static CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(
            "DATE",
            "TIME",
            "BOROUGH",
            "ZIP CODE",
            "LATITUDE",
            "LONGITUDE",
            "LOCATION",
            "ON STREET NAME",
            "CROSS STREET NAME",
            "OFF STREET NAME",
            "NUMBER OF PERSONS INJURED",
            "NUMBER OF PERSONS KILLED",
            "NUMBER OF PEDESTRIANS INJURED",
            "NUMBER OF PEDESTRIANS KILLED",
            "NUMBER OF CYCLIST INJURED",
            "NUMBER OF CYCLIST KILLED",
            "NUMBER OF MOTORIST INJURED",
            "NUMBER OF MOTORIST KILLED",
            "CONTRIBUTING FACTOR VEHICLE 1",
            "CONTRIBUTING FACTOR VEHICLE 2",
            "CONTRIBUTING FACTOR VEHICLE 3",
            "CONTRIBUTING FACTOR VEHICLE 4",
            "CONTRIBUTING FACTOR VEHICLE 5",
            "UNIQUE KEY",
            "VEHICLE TYPE CODE 1",
            "VEHICLE TYPE CODE 2",
            "VEHICLE TYPE CODE 3",
            "VEHICLE TYPE CODE 4",
            "VEHICLE TYPE CODE 5"
    );
}

