import java.util.ArrayList;


public class Team {
	//Class that defines a Roster of players
	String name;
	boolean ai = true;
	int draftPosition = 0;
	
	/*
	 * Set of positions that a roster can contain
	 * 0 = qb
	 * 1 = wr1
	 * 2 = wr2
	 * 3 = rb1
	 * 4 = rb2
	 * 5 = te
	 * 6 = flex
	 * 7 = team d
	 * 8 = kicker
	 * 9-13 = bench players
	
	*/
	Player[] roster = new Player[13];
	//!!!!!!!!!!!!!!!!!!!!!!!!ADD TEAM RANKINGNS
	//Variables to measure a teams positional needs
	Double qNeed = -130.0;
	Double wNeed = 64.0;
	Double rNeed = 64.0;
	Double tNeed = -20.0;
	Double dNeed = 0.0;
	Double kNeed = 0.0;
	
	int qNum;
	int wNum;
	int rNum;
	int tNum;
	int dNum;
	int kNum;
	//Quality of life variables to indicate if positions are filled or not; will be natively false
	boolean isQB;
	boolean isWR1;
	boolean isWR2;
	boolean isRB1;
	boolean isRB2;
	boolean isTE;
	boolean isFlex;
	boolean isD;
	boolean isK;
	boolean[] isbench= new boolean[4];
	
	//constructor
	public Team(String teamName, boolean ai)
	{
		name = teamName;
		this.ai = ai;
		updateNeed();
	}
	public String toString()
	{
		return this.name;
	}
	//diagnostic function to display roster; ONLY USE ON A FULL ROSTER
	public void printRoster()
	{
		System.out.println("QB  - " + roster[0].name);
		System.out.println("WR1 - " + roster[1].name);
		System.out.println("WR2 - " + roster[2].name);
		System.out.println("RB1 - " + roster[3].name);
		System.out.println("RB2 - " + roster[4].name);
		System.out.println("TE  - " + roster[5].name);
		System.out.println("D   - " + roster[6].name);
		System.out.println("K   - " + roster[7].name);
		System.out.println("FLEX   - " + roster[8].name);
		for(int i = 9; i < roster.length; i++)
		{
			if(isbench[i-9])
				System.out.println("b   - " + roster[i].name);
		}
	}
	public double getNeed(char pos)
	{
		double ret = 99;
		if(pos == 'Q')
			ret = qNeed;
		else if (pos == 'R') 
		{
			ret = rNeed;
		}
		else if (pos == 'W') 
		{
			ret = wNeed;
		}
		else if (pos == 'T')
		{
			ret = tNeed;
		}
		return ret;
	}
	//Function to add a player to the correct position of the roster
	public boolean addPlayer(Player x)
	{
		boolean ret = false;
		
		//iterate through roster until correct position is filled
		switch(x.position)
		{
		case 'Q':
			qNum++;
			if(!isQB)
			{
				roster[0] = x;
				isQB = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		case 'W':
			wNum++;
			if(!isWR1)
			{
				roster[1] = x;
				isWR1 = true;
				ret = true;
			}
			else if(!isWR2)
			{
				roster[2] = x;
				isWR2 = true;
				ret = true;
			}
			else if(!isFlex)
			{
				roster[6] = x;
				isFlex = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		case 'R':
			rNum++;
			if(!isRB1)
			{
				roster[3] = x;
				isRB1 = true;
				ret = true;
			}
			else if(!isRB2)
			{
				roster[4] = x;
				isRB2 = true;
				ret = true;
			}
			else if(!isFlex)
			{
				roster[6] = x;
				isFlex = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		case 'T':
			tNum++;
			if(!isTE)
			{
				roster[5] = x;
				isTE = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		case 'D':
			dNum++;
			if(!isD)
			{
				roster[7] = x;
				isD = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		case 'K':
			kNum++;
			if(!isK)
			{
				roster[8] = x;
				isK = true;
				ret = true;
			}
			else
				ret = addBench(x);
			break;
		default:
			System.out.println("THIS IS WEIRD");
			break;
	
		}
		//add roster.revalue to change need modifiers
		return ret;
	}
	public boolean addBench(Player x)
	{
		boolean ret = false;
		for(int i = 0; i < isbench.length; i++)
		{
			if(!isbench[i])
			{
				roster[i+9] = x;
				isbench[i] = true;
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	//function to return number of bench players
	int bench()
	{
		int ret = 0;
		for(int i = 0; i < isbench.length; i++)
		{
			if(isbench[i])
				ret++;
		}
		
		return ret;
	}

	//Function to reevaluate team need values
	void updateNeed()
	{	
		qNeed = -170.0 - (90 * qNum) + (5 * draft.currentRound);
		rNeed = 64.0 - (16 * rNum);
		wNeed = 64.0 - (16 * wNum);
		tNeed = -50.0 - (999 * tNum) + (8 * draft.currentRound);
		dNeed = -999.9;
		kNeed = -999.0;
	}
	
	//BASIC AI, NOT VERY SMART ******************************************************************************************************
	//possible additions - better evaluate teams picking before own next pick, reach for sleepers, anticipate draft tendecies,
	//better data set evaluation.
	//Ultimately the ai picks based off of projections made by people and the points they assign to them.  I would like to try and
	//collect prjocetions of player stats and favor certain players by metrics of workload, touchdowns, passing usage, preojection variance, etc,
	//depending on what round it is.
	public int pick(int picksToNext)
	{
		ArrayList<Player> players = draft.availablePlayers;
		int qBPA = -1;
		int rBPA = -1;
		int wBPA = -1;
		int tBPA = -1;
		int dBPA = -1;
		int kBPA = -1;
		int pick = -1;
		int rTier = 3;
		int wTier = 3;
		int qTier = 3;
		int tTier = 3;
		double qVal = 0.00;
		double rVal = 0.00;
		double wVal = 0.00;
		double tVal = 0.00;
		double dVal = 0.00;
		double kVal = 0.00;
		double pickVal = 0.00;
		
		//get bpa/position, get bpa tiers, apply scarcity, (apply opportunity cost?)
		boolean q = false;
		boolean r = false;
		boolean w = false;
		boolean t = false;
		boolean d = false;
		boolean k = false;
		for(int i = 0; i < players.size(); i ++)
		{
			if(players.get(i).available)
			{
				switch(players.get(i).position)
				{
				case 'Q':
					if(qBPA < 0)
					{
						qBPA = i;
						qVal = players.get(i).proj;
						if(qVal > (draft.meanQB1 - 36))
						{
							draft.assignScarcity('Q', draft.meanQB1 - 36, 999999999);
							qTier = 1;
						}
						q = true;
					}
					break;
				case 'W':
					if(wBPA < 0)
					{
						wBPA = i;
						wVal = players.get(i).proj;
						if(wVal > (draft.meanWR1 - 24))
						{
							draft.assignScarcity('W', draft.meanWR1 - 24, 999999999);
							wTier = 1;
						}
						else if(wVal > (draft.meanWR2 - 24))
						{
							draft.assignScarcity('W', draft.meanWR2 - 24, 999999999);
							wTier = 2;
						}
						w = true;
					}
					break;
				case 'R':
					if(rBPA < 0)
					{
						rBPA = i;
						rVal = players.get(i).proj;
						if(rVal > (draft.meanRB1 - 24))
						{
							draft.assignScarcity('R', draft.meanRB1 - 24, 999999999);
							rTier = 1;
						}
						else if(rVal > (draft.meanRB2 - 24))
						{
							draft.assignScarcity('R', draft.meanRB2 - 24, 999999999);
							rTier = 2;
						}
						r = true;
					}
					break;
				case 'T':
					if(tBPA < 0)
					{
						tBPA = i;
						tVal = players.get(i).proj;
						t = true;
					}
					break;
				case 'D':
					if(dBPA < 0)
					{
						dBPA = i;
						dVal = players.get(i).proj;
						d = true;
					}
					break;
				case 'K':
					if(kBPA < 0)
					{
						kBPA = i;
						kVal = players.get(i).proj;
						k = true;
					}
					break;
				}
			}
			
			if(q && r && w && t && d && k)
				break;
		}
		
		System.out.println(" " + qTier + " " + qVal + " "+ wTier + " " + wVal + " " + rTier + " " + rVal);
		//apply scarcity multipliers
		double tmp = 0;
		tmp = qVal;
		qVal += (tmp - draft.getNextRank(picksToNext, 'Q').proj);
		qVal += qNeed;
		qVal /= draft.meanQB1;
		if(qVal > pickVal)
		{
			pick = qBPA;
			pickVal = qVal;
		}
		
		tmp = rVal;
		rVal += (tmp - draft.getNextRank(picksToNext, 'R').proj);
		rVal += rNeed;
		if(tmp > draft.RB1floor)
			rVal /= draft.meanRB1;
		else if (tmp > draft.RB2floor) 
			rVal /= draft.meanRB2;
		else
			rVal /= draft.meanRB3;
		if(rNum > wNum)
			rVal -= .1 * (rNum - wNum);
		if(rVal > pickVal)
		{
			pick = rBPA;
			pickVal = rVal;
		}
		
		tmp = wVal;
		wVal += (tmp - draft.getNextRank(picksToNext, 'W').proj);
		wVal += wNeed;
		if(tmp > draft.WR1floor)
			wVal /= draft.meanWR1;
		else if (tmp > draft.WR2floor) 
			wVal /= draft.meanWR2;
		else
			wVal /= draft.meanWR3;
		if(wNum > rNum)
			wVal -= .1 * (wNum -rNum);
		if(wVal > pickVal)
		{
			pick = wBPA;
			pickVal = wVal;
		}
		
		tmp = tVal;
		//tVal += (tmp - draft.getNextRank(picksToNext, 'T').proj);
		tVal += tNeed;
		tVal /= draft.meanTE1;
		if(tVal > pickVal)
		{
			pick = tBPA;
			pickVal = tVal;
		}
		
		if(draft.currentRound == draft.rounds && !isTE)
			pick = tBPA;
		else if(draft.currentRound == draft.rounds)
		{
			pick = dBPA;
		}
		else if(draft.currentRound == draft.rounds + 1)
		{
			pick = kBPA;
		}
		
		System.out.println(draft.availablePlayers.get(qBPA).name + ": " + qVal);
		System.out.println(draft.availablePlayers.get(rBPA).name + ": " + rVal);
		System.out.println(draft.availablePlayers.get(wBPA).name + ": " + wVal);
		System.out.println(draft.availablePlayers.get(tBPA).name + ": " + tVal);
		
		addPlayer(players.get(pick));
		updateNeed();
			
		return pick;
	}

	

}
