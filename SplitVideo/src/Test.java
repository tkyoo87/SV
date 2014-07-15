
public class Test {

	public static void main(String[] args) throws MadsnotepadException {
		FFMPEGSplitVideo splitter = new FFMPEGSplitVideo();
		CmdExecute cmd = new CmdExecute();
		splitter.setCmdExecute(cmd);
		
		splitter.splitVideo("/home/ytk/big.mp4", "/home/ytk/bigsample1.mp4", "00:00:00", 1800);
		splitter.splitVideo("/home/ytk/big.mp4", "/home/ytk/bigsample2.mp4", "00:30:00", 1800);
		
		System.out.println("video split success");
		
	}

}
