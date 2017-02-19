package mw.hadoop.queries;

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

/*
    Return the average number of accidents and average number of lethal accidents per week per borough.
    An "UNKNOWN" value is used when the borough field is empty.
 */
public class WeekBorough  extends Configured implements Tool {


    public static class WeekBoroughMapper
            extends Mapper<Object, Text, Text, BooleanWritable> {


        private static final Log LOG = LogFactory.getLog(WeekBoroughMapper.class);

        //Data management utility
        private SimpleDateFormat formatter;
        private Calendar cal;

        @Override
        public void setup(Context context){
            formatter = new SimpleDateFormat("MM/dd/yyyy");
            cal = new GregorianCalendar();
        }

        /*
            Generates tuples <Text "YEAR-WEEK-BOROUGH", BooleanWritable is_lethal>
         */
        @Override
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            //skip header
            if(key.toString().equals("0"))
                return;

            try {
                CSVParser parser = new CSVParser(new StringReader(value.toString()), NYPD_Keys.csvFormat);
                CSVRecord record;
                try {
                    record = parser.getRecords().get(0);
                    if(!record.isConsistent()) { // check if CSV record matches with header
                        throw new IOException();
                    }
                } catch(IOException e) {
                    // CSV record is corrupted. Continuing.
                    LOG.warn(String.format("Row %s: CSV record corrupted", key.toString()));
                    return;
                }

                Date date = formatter.parse(record.get("DATE"));
                cal.setTime(date);

                String borough = record.get("BOROUGH");
                if(borough.length() == 0) {
                    // Default value when the field is empty
                    borough = "UNKNOWN";
                }
                String newkey = String.format("%d-%02d_%s", cal.getWeekYear(), cal.get(Calendar.WEEK_OF_YEAR), borough);

                int killed = Integer.parseInt(record.get("NUMBER OF PERSONS KILLED"));
                boolean lethal = (killed > 0);

                context.write(new Text(newkey), new BooleanWritable(lethal));

            } catch(NumberFormatException | ParseException e) {
                LOG.warn(String.format("Row %s: %s", key.toString(), e.getMessage()));
            }

        }
    }

    /*
        Returns tuples <Text "YEAR-WEEK-BOROUGH", Number of accidents, Number of lethal accidents>
     */
    public static class WeekBoroughReducer
            extends Reducer<Text,BooleanWritable,Text,Text> {

        @Override
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

            String res = String.format("%d\t%d", numAccidents, numLethalAccidents);
            context.write(key, new Text(res));
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionParser.getRemainingArgs();
        if (remainingArgs.length != 2) {
            System.err.println("Usage: WeekBorough <infile.csv> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, this.getClass().toString());

        job.setJarByClass(WeekBorough.class);
        job.setMapperClass(WeekBoroughMapper.class);
        job.setReducerClass(WeekBoroughReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BooleanWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        Path in = new Path(remainingArgs[0]);
        Path out = new Path(remainingArgs[1]);

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        // Let ToolRunner handle generic command-line options
        int res = ToolRunner.run(new Configuration(), new WeekBorough(), args);

        System.exit(res);
    }
}
