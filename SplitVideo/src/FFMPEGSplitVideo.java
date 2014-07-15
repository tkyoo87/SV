import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class FFMPEGSplitVideo implements SplitVideo {

	public static final String FFMPEG_SPLIT_CMD = "ffmpeg -ss %1$s -i %2$s -t %3$f -acodec copy -vcodec copy -async 1 -y %4$s";
	public static final String FFPROBE_KEYFRAME_CMD = "ffprobe -v quiet -print_format ini -select_streams 0:v:0 -show_entries frame=pkt_pts_time,pict_type %1$s";
	public static final String FFPROBE_DURATION_CMD = "ffprobe -v quiet -print_format ini -show_entries format=duration %1$s";

	private CmdExecute cmdExecute;

	public CmdExecute getCmdExecute() {
		return this.cmdExecute;
	}

	public void setCmdExecute(CmdExecute cmdExecute) {
		this.cmdExecute = cmdExecute;
	}

	public List<Double> getKeyframes(String videoFile, int nearestToSeconds)
			throws MadsnotepadException {
		try {
			if (getCmdExecute() == null) {
				throw new MadsnotepadException("CmdExecute not set");
			}
			List<Double> keyFramesList = new ArrayList<Double>();
			List<InputStream> streams = getCmdExecute().executeNReturnStream(
					String.format(FFPROBE_KEYFRAME_CMD, videoFile));
			if (streams == null || streams.size() != 2) {
				throw new MadsnotepadException(
						"Failed to execute command to retrieve keyframes");
			}
			InputStream is = streams.get(0);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			// breakAt will be the places in multiples of nereastToSeconds. If
			// nereastToSeconds
			// then breakAt will be 5, 10, 15 and so on. It is reset to the next
			// nereastToSeconds
			// after a keyframe is identified near nereastToSeconds
			int breakAt = nearestToSeconds;
			keyFramesList.add(0.0d);
			double dFrame = 0.0d;
			while ((line = br.readLine()) != null) {
				if (line.contains("pkt_pts_time")) {
					String frame = extractValue(line);
					dFrame = Double.parseDouble(frame);
				} else if (line.contains("pict_type")) {
					String frameType = extractValue(line);
					if (frameType.equals("I")) {
						if (dFrame > breakAt) {
							keyFramesList.add(dFrame);
							int numberOfKeyFramesIdentified = (int) dFrame
									/ nearestToSeconds;
							breakAt = nearestToSeconds
									* (1 + numberOfKeyFramesIdentified);
						}
					}
				}
			}
			InputStream err = streams.get(1);
			byte[] b = new byte[1024];
			int size = 0;
			while ((size = err.read(b)) != -1) {
				System.out.println(new String(b, 0, size));
			}
			return keyFramesList;
		} catch (Exception e) {
			throw new MadsnotepadException(e);
		}
	}

	public List<String> splitVideoAtKeyframes(String videoFile,
			List<Double> keyFrames) throws MadsnotepadException {
		double totalDurationInSeconds = getVideoDuration(videoFile);
		String outputFile = null;
		String startOffset = null;
		double splitDuration = 0.0d;
		int counter = 0;
		String fileExtn = getFileExtn(videoFile);
		List<String> outputFileList = new ArrayList<String>();
		for (double keyFrame : keyFrames) {
			outputFile = getOutputFileName((counter + 1), fileExtn);
			// check if we have got to the last keyframe and process the content
			// until the end of duration
			if (counter >= (keyFrames.size() - 1)) {
				if (keyFrames.get(counter) < totalDurationInSeconds) {
					startOffset = getTimeFormat(keyFrames.get(counter));
					splitDuration = totalDurationInSeconds
							- keyFrames.get(counter);
					splitVideo(videoFile, outputFile, startOffset,
							splitDuration);
				}
				break;
			}
			startOffset = getTimeFormat(keyFrames.get(counter));
			splitDuration = keyFrames.get(counter + 1) - keyFrames.get(counter);
			System.out.println("startOffset " + startOffset + " splitDuration "
					+ splitDuration);
			splitVideo(videoFile, outputFile, startOffset, splitDuration);
			counter++;
			outputFileList.add(outputFile);
		}
		return outputFileList;
	}

	public void splitVideo(String videoFile, String outputFile,
			String startOffset, double endDuration) throws MadsnotepadException {
		if (getCmdExecute() == null) {
			throw new MadsnotepadException("CmdExecute not set");
		}
		
		
		
		
		getCmdExecute().execute(
				String.format(FFMPEG_SPLIT_CMD,
						startOffset,
						videoFile,
						endDuration,
						outputFile));
	}

	private double getVideoDuration(String videoFile)
			throws MadsnotepadException {
		if (getCmdExecute() == null) {
			throw new MadsnotepadException("CmdExecute not set");
		}
		String line = null;
		double videoDuration = 0.0;
		try {
			List<InputStream> streams = getCmdExecute().executeNReturnStream(
					String.format(FFPROBE_KEYFRAME_CMD, videoFile));
			if (streams == null || streams.size() != 2) {
				throw new MadsnotepadException(
						"Failed to execute command to retrieve duration");
			}
			InputStream io = streams.get(0);
			BufferedReader br = new BufferedReader(new InputStreamReader(io));
			while ((line = br.readLine()) != null) {
				if (line.contains("duration")) {
					String durStr = line.substring(line.lastIndexOf("=") + 1,
							line.length());
					videoDuration = Double.parseDouble(durStr);
				}
			}
		} catch (Exception e) {
			throw new MadsnotepadException(e);
		}
		return videoDuration;
	}

	private String extractValue(String keyValuePair) {
		return keyValuePair.substring(keyValuePair.lastIndexOf("=") + 1,
				keyValuePair.length());
	}

	private String getOutputFileName(int counterSuffix, String fileExtn) {
		return String.format("%06d",
				Integer.parseInt(String.valueOf(counterSuffix)))
				+ fileExtn;
	}

	private String getFileExtn(String fileName) {
		if (fileName == null || fileName.trim().equals("")
				|| !fileName.contains(".")) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf("."), fileName.length());
	}

	private String getTimeFormat(long seconds) {
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hour = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds)
				- (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds)
				- (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		return String.format("%02d", hour) + ":"
				+ String.format("%02d", minute) + ":"
				+ String.format("%02d", second);
	}

	private String getTimeFormat(double seconds) {
		long secInLong = (long) seconds;
		String secStr = Double.toString(seconds);
		if (secStr.indexOf(".") > -1) {
			return getTimeFormat(secInLong)
					+ secStr.substring(secStr.indexOf("."), secStr.length());
		} else {
			return getTimeFormat(secInLong);
		}
	}

}
