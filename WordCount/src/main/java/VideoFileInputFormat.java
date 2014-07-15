import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class VideoFileInputFormat extends FileInputFormat<Text, BytesWritable> {


	

	

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		
		List<InputSplit> split = new ArrayList<InputSplit>();
		
		
		
		return super.getSplits(job);
	}

	@Override
	public RecordReader<Text, BytesWritable> createRecordReader(
			InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {

		return new VideoFileRecordReader();
	}

}


/*
private static final double SPLIT_SLOP = 1.1; // 10% slop
static final String NUM_INPUT_FILES = "mapreduce.input.num.files";

@Override
public List<InputSplit> getSplits(JobContext job) throws IOException {

	long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));
	long maxSize = getMaxSplitSize(job);

	// get servers in the cluster
	String[] servers = getActiveServersList(job);
	if (servers == null)
		return null;
	// generate splits
	List<InputSplit> splits = new ArrayList<InputSplit>();
	List<FileStatus> files = listStatus(job);
	int currentServer = 0;
	
	for (FileStatus file : files) {
	
		Path path = file.getPath();
		long length = file.getLen();
		
		if ((length != 0) && isSplitable(job, path)) {
		
			long blockSize = file.getBlockSize();
			long splitSize = computeSplitSize(blockSize, minSize, maxSize);

			long bytesRemaining = length;
			
			while (((double) bytesRemaining) / splitSize > SPLIT_SLOP) {
			
				splits.add(new FileSplit(path, length - bytesRemaining,
						splitSize, new String[] { servers[currentServer] }));
				currentServer = getNextServer(currentServer, servers.length);
				bytesRemaining -= splitSize;
			}

			if (bytesRemaining != 0) {
				splits.add(new FileSplit(path, length - bytesRemaining,
											bytesRemaining, new String[] { servers[currentServer] }));
				currentServer = getNextServer(currentServer, servers.length);
			}
			
		} else if (length != 0) {
			splits.add(new FileSplit(path, 0, length,
					new String[] { servers[currentServer] }));
			currentServer = getNextServer(currentServer, servers.length);
		} else {
			// Create empty hosts array for zero length files
			splits.add(new FileSplit(path, 0, length, new String[0]));
		}
	}

	// Save the number of input files in the job-conf
	job.getConfiguration().setLong(NUM_INPUT_FILES, files.size());

	return splits;
}

private String[] getActiveServersList(JobContext context) {

	String[] servers = null;
	try {
		JobClient jc = new JobClient((JobConf) context.getConfiguration());
		ClusterStatus status = jc.getClusterStatus(true);
		Collection<String> atc = status.getActiveTrackerNames();
		servers = new String[atc.size()];
		int s = 0;
		for (String serverInfo : atc) {
			StringTokenizer st = new StringTokenizer(serverInfo, ":");
			String trackerName = st.nextToken();
			StringTokenizer st1 = new StringTokenizer(trackerName, "_");
			st1.nextToken();
			servers[s++] = st1.nextToken();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}

	return servers;

}

private static int getNextServer(int current, int max) {

	current++;
	if (current >= max)
		current = 0;
	return current;
}
*/