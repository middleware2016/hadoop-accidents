package mw.hadoop.queries;

/**
 * Created by pietro on 2017-01-14.
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Calendar.YEAR;

/*
    Return the average number of accidents and average number of lethal accidents per week per borough.
    An "UNKNOWN" value is used when the borough field is empty.
 */
public class WeekBorough  extends Configured implements Tool {


    public static class WeekBoroughMapper
            extends Mapper<Object, Text, Text, BooleanWritable> {


        private static final Log LOG = LogFactory.getLog(WeekBoroughMapper.class);

        /*
            Generates tuples <Text "YEAR-WEEK-BOROUGH", BooleanWritable is_lethal>
         */
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {


            try {
                CSVParser parser = new CSVParser(new StringReader(value.toString()), CSVFormat.DEFAULT);
                CSVRecord record;
                try {
                    record = parser.getRecords().get(0);
                } catch(IOException e) {
                    // CSV record is corrupted. Continuing.
                    LOG.warn("CSV record corrupted.");
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                Date date = formatter.parse(record.get(NYPD_Keys.getIndex("DATE")));
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);

                String borough = record.get(NYPD_Keys.getIndex("BOROUGH"));
                if(borough.length() == 0) {
                    // Default value when the field is empty
                    LOG.info("Unknown borough.");
                    borough = "UNKNOWN";
                }
                String newkey = String.format("%d-%d-%s", cal.get(YEAR), cal.get(Calendar.WEEK_OF_YEAR), borough);

                int killed = Integer.parseInt(record.get(NYPD_Keys.getIndex("NUMBER OF PERSONS KILLED")));
                boolean lethal = (killed > 0);

                context.write(new Text(newkey), new BooleanWritable(lethal));

            } catch(NumberFormatException | ParseException e) {
                LOG.warn("Parsing error", e);
            }

        }
    }

    /*
        Returns tuples <Text "YEAR-WEEK-BOROUGH", "accidents: %f; lethalAccidents: %f">
     */
    public static class WeekBoroughReducer
            extends Reducer<Text,BooleanWritable,Text,Text> {

        public void reduce(Text key, Iterable<BooleanWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int numLethalAccidents = 0;
            int numAccidents = 0;
            for (BooleanWritable val : values) {
                numAccidents++;
                if (val.get()) {
                    numLethalAccidents++;
                }
            }

            String res = String.format("accidents: %d; lethalAccidents: %d", numAccidents, numLethalAccidents);
            context.write(key, new Text(res));
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, this.getClass().toString());

        job.setJarByClass(WeekBorough.class);
        job.setMapperClass(WeekBoroughMapper.class);
        job.setReducerClass(WeekBoroughReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BooleanWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("/data/NYPD_Motor_Vehicle_Collisions.csv"));
        FileOutputFormat.setOutputPath(job, new Path("file:///Users/pietro/tmp/hdp/WeekBorough"));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        // Let ToolRunner handle generic command-line options
        int res = ToolRunner.run(new Configuration(), new WeekBorough(), args);

        System.exit(res);
    }
}
