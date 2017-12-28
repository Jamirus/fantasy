import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class DraftGrid extends JPanel{
	JPanel[][] grid; //x,y
	JLabel [][] readout;
	int rounds;
	
	public DraftGrid(Team[] teams, int rounds)
	{
		setBorder(BorderFactory.createLineBorder(Color.red));
		//set layout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		
		gc.weightx = 1/(teams.length + 1);
		gc.weighty = 1/(rounds + 1);
		
		//init dimensions
		grid = new JPanel[teams.length + 1][rounds + 1];
		readout = new JLabel[teams.length + 1][rounds + 1];
		
		
		//populate grid
		grid[0][0] = new JPanel();
		for(int i = 0; i < teams.length + 1; i++)
		{
			for(int j = 0; j < rounds + 1;j ++)
			{
				grid[i][j] = new JPanel(new BorderLayout());
				grid[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
			}
		}
		readout[0][0] = new JLabel("");
		//add team names
		for(int i = 0; i < teams.length; i ++)
		{
			this.readout[i + 1][0] = new JLabel(teams[i].name, JLabel.CENTER);
		}
		//add rd indicators
		for(int i = 0; i < rounds; i++)
		{
			readout[0][i + 1] = new JLabel("rd " + (i+1));
		}
		//create labels that will hold drafted player names
		for(int i = 0; i < teams.length; i++)
		{
			for(int j = 0; j <rounds; j++)
			{
				readout[i+1][j+1] = new JLabel("-", JLabel.CENTER);
			}
		}
		
		//add components to grid of appropriate size
		for(int i = 0; i < grid.length; i++)
		{
			gc.gridx = i;
			
			for(int j = 0; j < grid[0].length; j++)
			{	
				grid[i][j].add(readout[i][j],BorderLayout.CENTER);
				gc.gridy=j;
				add(grid[i][j], gc);
			}
	
		}
		
		
	}
}
