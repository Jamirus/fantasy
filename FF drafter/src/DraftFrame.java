import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

//Class that forms the base Jframe for the drafting GUI
public class DraftFrame extends JFrame{

	//UI elements
	DraftGrid grid;
	SelectorPanel select;
	PickPanel picker;
	JTabbedPane overview;
	JTextArea draftRecordView;
	
	public DraftFrame(String name)
	{
		super(name);
		setLayout(new BorderLayout());
		Container c = getContentPane();
		
		
		//init and add UI elements for desired layout
		draftRecordView = new JTextArea();
		draftRecordView.setEditable(false);
		JScrollPane scroll = new JScrollPane(draftRecordView);
		scroll.setPreferredSize(new Dimension(250, 450));
		//add selector panel
		select = new SelectorPanel();
		c.add(select, BorderLayout.PAGE_START);
		pack();
		
		grid = new DraftGrid(draft.teams, draft.rounds);
		picker = new PickPanel(draft.teams);
		picker.add(scroll);
		
		JPanel display = new JPanel();
		display.setLayout(new BoxLayout(display, BoxLayout.PAGE_AXIS));
		display.add(grid);
		display.add(picker);
		
		c.add(display, BorderLayout.CENTER);
		
	   
	}
	
	//Method to add a draft pick to the draft grid, and set the color
	//to indicate the position of the player picked
	public void updateGrid(int x, int y, String str, char pos)
	{
		grid.grid[x][y].setOpaque(true);
		grid.readout[x][y].setText(str);
		if(pos == 'Q')
			grid.grid[x][y].setBackground(Color.WHITE);
		else if (pos == 'R') 
			grid.grid[x][y].setBackground(new Color(255,153,153));
		else if (pos == 'W') 
			grid.grid[x][y].setBackground(new Color(255,255,153));
		else if (pos == 'T') 
			grid.grid[x][y].setBackground(new Color(204,255,204));
		else if (pos == 'D') 
			grid.grid[x][y].setBackground(new Color(255,229,204));
		else if (pos == 'K') 
			grid.grid[x][y].setBackground(new Color(255,204,229));
		
	}
	
	//public method to call background thread
	public void init(JFrame settings)
	{
		Drafter d = new Drafter(this, settings);
		d.execute();
	}
	
	//private class to execute draft methods without asking swing to wait
	//for their completion(would result in program freezing upon draft methods 
	//needing user input).  This class also closes the settings window before
	//starting the draft.
	class Drafter extends SwingWorker<String, String>
	{		
		DraftFrame frame;
		JFrame settings;
		public Drafter(DraftFrame frame, JFrame settings)
		{
			this.frame = frame;
			this.settings = settings;
		}
		@Override
		protected String doInBackground() throws Exception {
			// TODO Auto-generated method stub
			WindowEvent wev = new WindowEvent(settings, WindowEvent.WINDOW_CLOSING);
		    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		    settings.setVisible(false);
		    settings.dispose();
			draft.init(frame);
			return null;
		}
		
	}

	//add resizing?
}
