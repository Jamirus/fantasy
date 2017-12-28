import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.*;

public class Driver {

	public static void main(String[] args)
	{
		
		//init jframe to hold settings panel
		JFrame frame = new JFrame("FF Drafter");
		frame.setLayout(new BorderLayout());
		Container c = frame.getContentPane();
		SettingsPanel settings = new SettingsPanel(frame);
		c.add(settings, BorderLayout.CENTER);
		//call setclose method to make window behave right upon being closed
		settings.setClose();
		frame.pack();
		frame.setVisible(true);

	}
	

}
