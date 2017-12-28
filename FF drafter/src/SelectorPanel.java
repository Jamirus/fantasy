import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;


public class SelectorPanel extends JPanel{

	JTextField playerSearch;
	static JLabel selectedPlayer;
	JButton select;
	String userPick = "Jon Sabotage";
	boolean pickSelected;
	CountDownLatch latch;
	
	public SelectorPanel()
	{
		latch = new CountDownLatch(1);
		//Picking team indicator

		//create search result box ******************************************************************************
		selectedPlayer = new JLabel("Jon Sabotage");
		
		//create jtextfield ************************************************************************************
		JTextField playerSearch = new JTextField("enter player name");
		playerSearch.setColumns(20);
		
		//add key listener and search function
		playerSearch.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				for(int i = 0; i < draft.availablePlayers.size(); i++)
				{
					String tmpName = playerSearch.getText();
					
					if(tmpName.toLowerCase().equals(draft.availablePlayers.get(i).name.toLowerCase().substring(0,tmpName.length())) && draft.availablePlayers.get(i).available)
					{
						selectedPlayer.setText(draft.availablePlayers.get(i).name);
						break;
					}
				}
				
				if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
				{
					System.out.println("it happened");
					userPick = selectedPlayer.getText();
					playerSearch.setText("");
					latch.countDown();
				}
            }
		});
		
		//create select player button ****************************************************************************
		select = new JButton("Select");
		select.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				userPick = selectedPlayer.getText();
				playerSearch.setText("");
				latch.countDown();
			}
			
		});
		
		//create layout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = .5;
		gc.weighty = 1;
		//add elements
		gc.gridx = 0;
		gc.gridy = 1;
		add(playerSearch,gc);
		gc.gridx = 1;
		add(selectedPlayer, gc);
		gc.gridx = 2;
		add(select, gc);
	}
	
	//Get User Pick function
	public CountDownLatch getPick()
	{
		return latch;
	}
	public void reset()
	{
		latch = new CountDownLatch(1);
	}
	
}
