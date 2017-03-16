import javax.swing.*;

/**
 * Created by Lovi on 2017. 03. 09. @ 0:22.
 */
public class ClientTest {
	public static void main(String[] args) {
		Client clio = new Client();
		clio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clio.startRunning();
	}
}
