import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

//Jpanel class that will hold the UI for the user to adjust their draft settings
//will have to add additional AI settings as more AI options are developed
public class SettingsPanel extends JPanel{
	//UI elements
	JSpinner totalTeams;
	JTable setTeams;
	JToolBar moveTeams;
	JButton moveRight;
	JButton moveLeft;
	JButton moveUp;
	JButton moveDown;
	JButton acceptSettings;
	
	JFrame parent;//pointer to parent frame to be passed to other methods that will later close it
	
	//Jtable variables
	String[] colNames = {"Pick #", "Human", "AI"};
	Object[][] data = new Object [12][3];
	boolean[][] editable = new boolean[12][3];
	
	boolean launched = false;//switch to indicate if window is closed by launching or user
	
	//constructor
	public SettingsPanel(JFrame parent)
	{
		
		this.parent = parent;
		this.setLayout(new BorderLayout());
		
		//init editable table; protects from bad input
		for(int i = 0; i < 12; i++)
		{
			editable[i][0] = false;
			editable[i][1] = false;
			editable[i][2] = true;
		}
		//init default team names
		for(int i = 0; i < 10; i++)
		{
			data[i][0] = i + 1;
			data[i][1] =  "-";
			data[i][2] = "Team " + (i+1);
		}
		
		//init table and model, set editability
		setTeams = new JTable(data, colNames);
		DefaultTableModel model = new DefaultTableModel(data, colNames){
			 @Override
			    public boolean isCellEditable(int row, int column) {

			       return editable[row][column];
			 }
		};
		setTeams.setModel(model);
		//set filter to hide rows for teams that will not be used(user wants to init draft for less than max teams)
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
		RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter(".", 1);//team data beyond desired number will be null
		setTeams.setRowSorter(sorter);
		sorter.setRowFilter(rf);
				
		//init spinner
		SpinnerModel spinModel = new SpinnerNumberModel(10,8,12,1);
		totalTeams = new JSpinner(spinModel);
		//set listener to add/remove teams to table upon input
		totalTeams.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				//set values above team# to null
				for(int i = 11; i >= (int)totalTeams.getValue(); i--)
				{
					model.setValueAt(null,  i, 0);
					model.setValueAt(null,  i, 1);
					model.setValueAt(null,  i, 2);
				}
				//init default table values for newly added teams
				for(int i = 0; i < (int)totalTeams.getValue(); i++)
				{
					model.setValueAt(i + 1,  i, 0);
					if(data[i][1] == null || data[i][2] == null)
					{
						model.setValueAt("Team " + (i + 1),  i, 1);
						model.setValueAt("-",  i, 2);
					}
				}
				sorter.setRowFilter(rf);
			}
			
		});
		//create tool bar and buttons
		moveTeams = new JToolBar();
		//each button has listeners that appropriately rearrange the data on the table
		//per user input and sets the editablitly to protect from bad input
		moveLeft  = new JButton("Left");
		moveLeft.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int tmp = setTeams.getSelectedRow();
				if(model.getValueAt(tmp, 1).equals("-"))
				{
					editable[tmp][1] = true;
					editable[tmp][2] = false;
					model.setValueAt(model.getValueAt(tmp, 2), tmp, 1);
					model.setValueAt("-", tmp, 2);
					model.fireTableDataChanged();
					setTeams.changeSelection(tmp, 1, true, false);
				}
				
			}
			
		});
		
		moveUp = new JButton("Up");
		moveUp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int tmp = setTeams.getSelectedRow();
				if(tmp > 0)
				{
					Boolean[] tmpBoolean = {editable[tmp][1], editable[tmp][2]};
					editable[tmp][1] = editable[tmp - 1][1];
					editable[tmp][2] = editable[tmp - 1][2];
					editable[tmp - 1][1] = tmpBoolean[0];
					editable[tmp - 1][2] = tmpBoolean[1];
					String[] tmpData = {(String) model.getValueAt(tmp, 1), (String) model.getValueAt(tmp, 2)};
					model.setValueAt(model.getValueAt(tmp - 1, 1), tmp, 1);
					model.setValueAt(model.getValueAt(tmp - 1, 2), tmp, 2);
					model.setValueAt(tmpData[0], tmp - 1, 1);
					model.setValueAt(tmpData[1], tmp - 1, 2);
					model.fireTableDataChanged();
					setTeams.changeSelection(tmp - 1, 1, true, false);
				}
			}
			
		});
		
		moveDown = new JButton("Down");
		moveDown.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int tmp = setTeams.getSelectedRow();
				if (tmp != (int)totalTeams.getValue() - 1)
				{
					Boolean[] tmpBoolean = {editable[tmp][1], editable[tmp][2]};
					editable[tmp][1] = editable[tmp + 1][1];
					editable[tmp][2] = editable[tmp + 1][2];
					editable[tmp + 1][1] = tmpBoolean[0];
					editable[tmp + 1][2] = tmpBoolean[1];
					String[] tmpData = {(String) model.getValueAt(tmp, 1), (String) model.getValueAt(tmp, 2)};
					model.setValueAt(model.getValueAt(tmp + 1, 1), tmp, 1);
					model.setValueAt(model.getValueAt(tmp + 1, 2), tmp, 2);
					model.setValueAt(tmpData[0], tmp + 1, 1);
					model.setValueAt(tmpData[1], tmp + 1, 2);
					model.fireTableDataChanged();
					setTeams.changeSelection(tmp + 1, 1, true, false);
				}
			}
			
		});
		
		moveRight  = new JButton("Right");
		moveRight.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int tmp = setTeams.getSelectedRow();
				if(model.getValueAt(tmp, 2).equals("-"))
				{
					editable[tmp][2] = true;
					editable[tmp][1] = false;
					model.setValueAt(model.getValueAt(tmp, 1), tmp, 2);
					model.setValueAt("-", tmp, 1);
					model.fireTableDataChanged();
					setTeams.changeSelection(tmp, 1, true, false);
				}
				
			}
			
		});
		
		moveTeams.add(moveLeft);
		moveTeams.add(moveUp);
		moveTeams.add(moveDown);
		moveTeams.add(moveRight);
		
		//add accept settings button, and method to launch draft
		acceptSettings = new JButton("Accept");
		acceptSettings.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				launched = true;//window will now be closed without exiting program
				//init teams for draft based on user settings
				Team[] teamArr = new Team[(int)totalTeams.getValue()];
				for(int i = 0; i < (int)totalTeams.getValue(); i++)
				{
					boolean ai = false;
					if(!model.getValueAt(i, 2).equals("-"))
					{
						ai = true;
						teamArr[i] = new Team((String)model.getValueAt(i, 2), ai);
					}
					else
						teamArr[i] = new Team((String)model.getValueAt(i, 1), ai);
					
				}
				draft initiator = new draft(teamArr, 13);
				
				//init draft frame and tell it to start the draft(frame.init)
				DraftFrame frame = new DraftFrame("FF Drafter");
				frame.setSize(1400,800);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.init(parent);
				
				
				
			}
			
		});
		
		//add elements to pane
		JScrollPane scroll = new JScrollPane(setTeams);
	
		add(scroll, BorderLayout.CENTER);
		add(totalTeams, BorderLayout.PAGE_START);
		add(acceptSettings, BorderLayout.EAST);
		add(moveTeams,BorderLayout.PAGE_END);
		
	}
	//method to set window closing method to behave differently per context when closed
	//will end program if closed without launching a draft(user wishes to exit program)
	public void setClose()
	{
		SwingUtilities.getWindowAncestor(this).addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                if(launched)
                	e.getWindow().setVisible(false);
                else
                	System.exit(UNDEFINED_CONDITION);
            }
        });
	}
	
}
