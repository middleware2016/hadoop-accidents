package mw.hadoop.queries;

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
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.io.StringReader;

/*
    For each "contributing factor" in car accidents (e.g. driver distraction), return:
    1) The total number of accidents
    2) Average number of deaths per accident
 */
public class ContributingFactors extends Configured implements Tool {

    public static class ContributingFactorMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static Log LOG = LogFactory.getLog(ContributingFactorMapper.class);
        private final static IntWritable one = new IntWritable(1);

        /*
            Generates tuples <Text ContributionFactor, IntWritable Deaths>
         */
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            //skip header
            if(key.toString().equals("0"))
                return;

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

                int cf_initial_index = NYPD_Keys.getIndex("CONTRIBUTING FACTOR VEHICLE 1");
                // iterate over 5 contribution factors
                for(int i = cf_initial_index; i<cf_initial_index+5; i++) {
                    String contribFactor = record.get(i);
                    if (contribFactor.length() == 0) {
                        LOG.info("Empty contributing factor.");
                        continue;
                    }

                    int deaths = Integer.parseInt(record.get(NYPD_Keys.getIndex("NUMBER OF PERSONS KILLED")));
                    context.write(new Text(contribFactor), new IntWritable(deaths));

                }
            } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                LOG.warn("Parsing error", e);
            }

        }
    }

    /*
        Returns tuples <Text ContributingFactor, "avgDeaths: %f; numAccidents: %d">
     */
    public static class ContributingFactorReducer
            extends Reducer<Text,IntWritable,Text,Text> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int deaths = 0;
            int numAccidents = 0;
            for (IntWritable val : values) {
                deaths += val.get();
                numAccidents++;
            }
            float avgDeaths = (float)deaths / numAccidents;

            String res = String.format("avgDeaths: %f; numAccidents: %d", avgDeaths, numAccidents);
            context.write(key, new Text(res));
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionParser.getRemainingArgs();
        if (remainingArgs.length != 2) {
            System.err.println("Usage: ContributingFactors <infile.csv> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, this.getClass().toString());

        job.setJarByClass(ContributingFactors.class);
        job.setMapperClass(ContributingFactorMapper.class);
        job.setReducerClass(ContributingFactorReducer.class);

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
        int res = ToolRunner.run(new Configuration(), new ContributingFactors(), args);

        System.exit(res);
    }
}
