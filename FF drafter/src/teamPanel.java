import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

//Jpanel class to display a team roster of varying size
public class teamPanel extends JPanel{

	//ui elements will be labels, player data loaded for later
	//implementation(I want to give users details about players on their teams
	//for their review)
	Player[] players;
	JLabel[] playerLabels;
	Player selected = new Player();
	
	
	public teamPanel(int rosterSize, int rb, int wr, int flex)
	{
		//set up simple grid to indicate positions and
		//coresponding names
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		players = new Player[rosterSize];
		playerLabels = new JLabel[rosterSize];
		
		gc.weightx = .5;
		gc.weighty = 1/rosterSize;
		gc.fill = GridBagConstraints.BOTH;
		
		gc.gridx = 0;
		gc.gridy = 0;
		
		//add labels depending on roster size variables
		add(new JLabel("QB: "), gc);
		
		for(int i = 0; i < rb; i++)
		{
			gc.gridy = 1 + i;
			add(new JLabel("WR: "), gc);
		}
		
		for(int i = 0; i < wr; i++)
		{
			gc.gridy = 1 + rb + i;
			add(new JLabel("RB: "), gc);
		}
		
		gc.gridy = 1 + rb + wr;
		add(new JLabel("TE: "), gc);
		
		for(int i = 0; i < flex; i++)
		{
			gc.gridy = 2 + rb + wr;
			add(new JLabel("FLX: "), gc);
		}
		
		gc.gridy = 2 + rb + wr+ flex;
		add(new JLabel("DST: "), gc);
		
		gc.gridy = 3 + rb + wr+ flex;
		add(new JLabel("K: "), gc);
		
		for(int i = 4 + rb + wr + flex; i < rosterSize; i++)
		{
			gc.gridy = i;
			add(new JLabel("B: "), gc);
		}
		
		//add player name labels, set listener(does nothing for now)
		gc.gridx = 1;
		for(int i = 0; i < rosterSize; i++)
		{
			gc.gridy = i;
			playerLabels[i] = new JLabel("     -");
			playerLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) 
                {
                    JLabel tmp = (JLabel)e.getComponent();
                    for(int i = 0; i < players.length; i++)
                    {
                    	if(tmp.getText().equals(players[i].name))
                    		selected = players[i];
                    }
                    //System.out.println(selected);
                }});
			add(playerLabels[i], gc);
		}
		
	}
	
	//method to populate the display table with roster names
	public boolean setTeam(Player[] team)
	{
		Boolean ret = false;
		
		for(int i = 0; i < team.length; i ++)
		{
			players[i] = team[i];

			if(team[i] != null)
			{
				playerLabels[i].setText(team[i].name);
			}
			else
				playerLabels[i].setText("     -");
		}
		
		return ret;
	}
}
