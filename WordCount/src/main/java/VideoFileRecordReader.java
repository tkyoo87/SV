import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class VideoFileRecordReader extends RecordReader<Text, BytesWritable> {

	private long start;
	private long pos;
	private long end;
	private int maxLineLength;
	private String fileName;
	//private DataInputStream dis;
	private Text key = new Text();
	private BytesWritable value = new BytesWritable();
	FSDataInputStream fileIn;
	FileSplit split;
	boolean isImplemented = false;
			
	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {

		split = (FileSplit) genericSplit;

		// Retrieve configuration, and Max allowed
		// bytes for a single record
		Configuration job = context.getConfiguration();
		this.maxLineLength = job.getInt("mapred.linerecordreader.maxlength",
				Integer.MAX_VALUE);

		// Split "S" is responsible for all records
        // starting from "start" and "end" positions
        start = split.getStart();
        end = start + split.getLength();
 
        
        // Retrieve file containing Split "S"
        final Path file = split.getPath();
        fileName = file.getName();
        FileSystem fs = file.getFileSystem(job);
        fileIn = fs.open(split.getPath());
        
       
        
        
         
        /*
     // If Split "S" starts at byte 0, first line will be processed
        // If Split "S" does not start at byte 0, first line has been already
        // processed by "S-1" and therefore needs to be silently ignored
        boolean skipFirstLine = false;
        if (start != 0) {
            skipFirstLine = true;
            // Set the file pointer at "start - 1" position.
            // This is to make sure we won't miss any line
            // It could happen if "start" is located on a EOL
            --start;
            fileIn.seek(start);
        }
        
        in = new LineReader(fileIn, job);
 
        // If first line needs to be skipped, read first line
        // and stores its content to a dummy Text
        if (skipFirstLine) {
            Text dummy = new Text();
            // Reset "start" to "start + line offset"
            start += in.readLine(dummy, 0,
                    (int) Math.min(
                            (long) Integer.MAX_VALUE, 
                            end - start));
        }
 
        // Position is the actual start
        this.pos = start;
		*/
	}
	
	

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		
		if(!isImplemented){
			
			byte[] data = new byte[(int)split.getLength()];
	        
	        fileIn.read(data);
	        
	        value.set(data,0,data.length);
	        key.set(fileName+"/"+start);
	        
	        isImplemented = true;
	        
			return true;
		}
		
		return false;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
