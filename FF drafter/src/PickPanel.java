import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

//Jpanel class to display data aiding the user in making a draft pick
public class PickPanel extends JPanel{
	
	//UI elements
	JCheckBox[] showPos = new JCheckBox[6];
	teamPanel teamDisplay;
	JComboBox<Team> pickTeam;
	JTable displayTable;
	JTextArea playerData;
	
	//Table variables
	Object[][] displayData;
	PlayerTable model;
	TableRowSorter<PlayerTable> sorter;
	String[] colNames = {"Pos","Name","Team","Projection","Drafted", "Player"};
	
	public PickPanel(Team[] teams)
	{	
		//Init table with player data
		displayData = new Object[draft.availablePlayers.size()][5];
		
		for(int i = 0; i < draft.availablePlayers.size(); i++)
		{
			displayData[i][0] = draft.availablePlayers.get(i).position;
			displayData[i][1] = draft.availablePlayers.get(i).name;
			displayData[i][2] = draft.availablePlayers.get(i).team;
			displayData[i][3] = new Double(draft.availablePlayers.get(i).proj);
			displayData[i][4] = i;
		}
		
		//set model and sorter(player class is comparable by name)
		displayTable = new JTable(displayData, colNames);
		model = new PlayerTable(draft.availablePlayers.size(), 5);
		displayTable.setModel(model);
		displayTable.setAutoCreateRowSorter(true);
		//add listener to set user selection as the potential user pick(selected player is what is used upon 
		//user hitting select in selector panel class)
		displayTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(displayTable.getSelectedRow() > -1)
				{
					SelectorPanel.selectedPlayer.setText((String) displayTable.getValueAt(displayTable.getSelectedRow(), 1));
					String tmp = (String) displayTable.getValueAt(displayTable.getSelectedRow(), 1);
					for(int i = 0; i < draft.availablePlayers.size();i++)
					{
						if(draft.availablePlayers.get(i).name.equals(tmp))
						{
							updatePlayerData(draft.availablePlayers.get(i));
							break;
						}
					}
				}
			}
			
		});
		//remove column visibility, this column is used for filtering flags and not user information
		displayTable.removeColumn(displayTable.getColumnModel().getColumn(4));
		sorter = new TableRowSorter<PlayerTable>(model);
		displayTable.setRowSorter(sorter);
		//add table to panel
		JScrollPane tableScroll = new JScrollPane(displayTable);
		
		//add filtering options
		showPos[0] = new JCheckBox("QB");
		showPos[1] = new JCheckBox("RB");
		showPos[2] = new JCheckBox("WR");
		showPos[3] = new JCheckBox("TE");
		showPos[4] = new JCheckBox("DST");
		showPos[5] = new JCheckBox("K");
		
		//init all as selected and add filter call on press
		for(int i = 0; i < showPos.length; i ++)
		{
			showPos[i].setSelected(true);
			showPos[i].addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					filterTable();
					
				}
				
			});
		}
		
		
		//init team panel
		teamDisplay = new teamPanel(draft.rosterSize, draft.rbs, draft.wrs, draft.flex);
		
		//add jcombo box to allow user to change what team roster is shown in team panel
		pickTeam = new JComboBox<Team>(teams);
		pickTeam.addActionListener(new ActionListener (){

			@Override
			public void actionPerformed(ActionEvent e) {
				Team tmp = (Team)pickTeam.getSelectedItem();
				teamDisplay.setTeam(tmp.roster);	
			}
			
		});
		
		//add elements to layout NEED TO MAKE THIS LOOK BETTER 
		teamDisplay.setBorder(BorderFactory.createLineBorder(Color.red));
		JPanel teamBox = new JPanel();
		teamBox.setBorder(BorderFactory.createLineBorder(Color.black));
		teamBox.setLayout(new BoxLayout(teamBox, BoxLayout.Y_AXIS));
		teamBox.add(pickTeam);
		teamBox.add(teamDisplay);
		
		add(teamBox);
		
		
		
		JPanel tableAndButtons = new JPanel();
		tableAndButtons.setLayout(new BoxLayout(tableAndButtons, BoxLayout.PAGE_AXIS));	
		tableAndButtons.add(tableScroll);
		
		JPanel filterButtons = new JPanel();
		filterButtons.setLayout(new BoxLayout(filterButtons, BoxLayout.LINE_AXIS));
		for(int i = 0; i < showPos.length; i++)
		{
			filterButtons.add(showPos[i]);
		}
		
		tableAndButtons.add(filterButtons);
		
		add(tableAndButtons);
		
		playerData = new JTextArea("Select a Player", 20, 20);
		playerData.setEditable(false);
		
		add(playerData);
	}
	//method to update playerdata with player....data
	public void updatePlayerData(Player p)
	{
		playerData.setText(p.name + ", " + p.position + "\n");
		switch(p.position)
		{
		case 'Q':
			playerData.append("Pass attempts: " + p.passAtt + "\n");
			playerData.append("Pass Completion" + p.passComp + "\n");
			playerData.append("Pass yds: " + p.passYds+ "\n");
			playerData.append("Pass tds: " + p.passTds + "\n");
			playerData.append("INTS: " + p.INTs + "\n");
			playerData.append("Rush attempts: " + p.rushAtt + "\n");
			playerData.append("Rush yds: " + p.rushYds + "\n");
			playerData.append("Rush tds: "+ p.rushTds + "\n");
			playerData.append("Projected fpts: " + p.proj);
			break;
		case 'R':
			playerData.append("Rush attempts: " + p.rushAtt + "\n");
			playerData.append("Rush yds: " + p.rushYds + "\n");
			playerData.append("Rush tds: "+ p.rushTds + "\n");
			playerData.append("Projected fpts: " + p.proj);
			break;
		case 'W':
			playerData.append("Receptions: " + p.receptions + "\n");
			playerData.append("Recieving yds: " + p.recYds + "\n");
			playerData.append("Recieving tds: " + p.recTds + "\n");
			playerData.append("Projected fpts: " + p.proj);
			break;
		case 'T':
			playerData.append("Receptions: " + p.receptions + "\n");
			playerData.append("Recieving yds: " + p.recYds + "\n");
			playerData.append("Recieving tds: " + p.recTds + "\n");
			playerData.append("Projected fpts: " + p.proj);
			break;
		default:
			playerData.append("no projections available");
			break;
			
		}
	}
	
	//method to flag player to be filtered and hide him
	public void Remove(int index)
	{
		displayData[index][4] = "no";
		filterTable();
	}
	
	//method to filter players by positions desired, and by availability.
	public void filterTable()
	{
		List<RowFilter<PlayerTable,Object>> filters = new ArrayList<RowFilter<PlayerTable,Object>>();
		
		filters.add(RowFilter.regexFilter("\\d",4));
		
		for(int i = 0; i < showPos.length; i++)
		{
			if(!showPos[i].isSelected())
				filters.add(RowFilter.notFilter(RowFilter.regexFilter(Character.toString(showPos[i].getText().charAt(0)),0)));
		}
		
		RowFilter<PlayerTable, Object> rf = RowFilter.andFilter(filters);
		sorter.setRowFilter(rf);
		
	}
	//Table model to get desired functionality from default table methods
	class PlayerTable extends AbstractTableModel
	{
		 private boolean[][] editable_cells; // 2d array to represent rows and columns
		 
		 private PlayerTable(int rows, int cols) { // constructor
		        super();
		        this.editable_cells = new boolean[rows][cols];
		    }

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 5;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return draft.availablePlayers.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			// TODO Auto-generated method stub
			
			return displayData[row][col];
		}
		
		//get class to allow sorting of comparable classes
		public Class<?> getColumnClass(int columnIndex) {
	  
	        return getValueAt(0, columnIndex).getClass();
	    }
		public String getColumnName(int column) {
			  return colNames[column];
			}
		@Override
	    public boolean isCellEditable(int row, int column) { // custom isCellEditable function
	        return this.editable_cells[row][column];
	    }

	    public void setCellEditable(int row, int col, boolean value) {
	        this.editable_cells[row][col] = value; // set cell true/false
	        this.fireTableCellUpdated(row, col);
	    }
		
	}

}
