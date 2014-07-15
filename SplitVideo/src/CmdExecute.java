import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;

public class CmdExecute {

	public void execute(String command) throws MadsnotepadException {
		try {
			List<InputStream> streams = executeNReturnStream(command);
			if (streams == null || streams.size() != 2) {
				throw new MadsnotepadException("Failed to execute command "
						+ command);
			}
			InputStream is = streams.get(0);
			byte[] b = new byte[1024];
			int size = 0;
			while ((size = is.read(b)) != -1) {
				System.out.println(new String(b, 0, size));
			}
			InputStream err = streams.get(1);
			size = 0;
			while ((size = err.read(b)) != -1) {
				System.out.println(new String(b, 0, size));
			}
		} catch (Exception e) {
			throw new MadsnotepadException(e);
		}
	}

	public List<InputStream> executeNReturnStream(String command)
			throws MadsnotepadException {
		try {
			Process pr = Runtime.getRuntime().exec(command);
			InputStream is = pr.getInputStream();
			List<InputStream> ioStreams = new ArrayList<InputStream>();
			ioStreams.add(is);
			InputStream err = pr.getErrorStream();
			ioStreams.add(err);
			return ioStreams;
		} catch (Exception e) {
			throw new MadsnotepadException(e);
		}
	}
}