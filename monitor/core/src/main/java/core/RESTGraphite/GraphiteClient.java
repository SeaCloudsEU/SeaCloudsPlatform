package core.RESTGraphite;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class GraphiteClient {
	private String host;
	private int port;
	
	public GraphiteClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void sendMetrics(Set<Metric> metrics) {
		try {
			Socket socket = new Socket(host, port);
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream, true);
			
			for (Metric metric : metrics) {
				System.out.printf("%s %f %d%n", metric.getName(), metric.getValue(), metric.getTimestamp());
				printWriter.printf("%s %f %d%n", metric.getName(), metric.getValue(), metric.getTimestamp());
			}
			
			printWriter.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
