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
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.GenericOptionsParser;
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
    Returns the number of lethal accidents for each week in the data set.
 */
public class LethalPerWeek extends Configured implements Tool {

    /*
        Generate tuples <Text "YYYY-weeknum", 1> for lethal accidents per week
        weeknum is the number of the week in the year (1-52, approximately)
     */
    public static class LethalWeekMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static Log LOG = LogFactory.getLog(LethalWeekMapper.class);
        private final static IntWritable one = new IntWritable(1);

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            CSVParser parser = new CSVParser(new StringReader(value.toString()), CSVFormat.DEFAULT);
            CSVRecord record;
            try {
                record = parser.getRecords().get(0);
            } catch (IOException e) {
                // CSV record is corrupted. Continuing.
                LOG.warn("CSV record corrupted.");
                return;
            }

            //skip header
            if(key.toString().equals("0"))
                return;

            try {
                String killedStr = record.get(NYPD_Keys.getIndex("NUMBER OF PERSONS KILLED"));
                //empty string became 0 killed
                if(killedStr.equals(""))
                    killedStr = "0";

                int killed = Integer.parseInt(killedStr);

                if (killed > 0) {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    String dateStr = record.get(NYPD_Keys.getIndex("DATE"));
                    Date date = formatter.parse(dateStr);
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(date);
                    String newkey = String.format("%d-%d", cal.get(YEAR), cal.get(Calendar.WEEK_OF_YEAR));

                    context.write(new Text(newkey), one);
                }
            } catch (NumberFormatException | ParseException e) {
                LOG.warn("Parsing error", e);
            }

        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionParser.getRemainingArgs();
        if (remainingArgs.length != 2) {
            System.err.println("Usage: LethalPerWeek <infile.csv> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, this.getClass().toString());

        job.setJarByClass(LethalPerWeek.class);
        job.setMapperClass(LethalWeekMapper.class);

        // Built-in combiner/reducer by Hadoop, which simply sums the values for each key.
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        Path in = new Path(remainingArgs[0]);
        Path out = new Path(remainingArgs[1]);

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        // Let ToolRunner handle generic command-line options
        int res = ToolRunner.run(new Configuration(), new LethalPerWeek(), args);

        System.exit(res);
    }
}