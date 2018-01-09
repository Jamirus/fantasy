import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

//class to contain data of one draft
//variable number of teams and rounds
public class draft 
{
	//draft data
	static Team[] teams;
	static ArrayList<Player> availablePlayers;
	static String[] draftRecord;
	static int rounds;
	static int currentRound = 1;
	static int rosterSize = 13;
	static int rbs = 2;
	static int wrs = 2;
	static int flex = 1;
	
	//player pool data for ai, currently not used :/
	double rPool = 0;
	double wPool = 0;
	double qPool = 0;
	double tPool = 0;
	//experimental ai data, see constructor
	static double meanRB1;
	static double meanRB2;
	static double meanRB3;
	static double meanWR1;
	static double meanWR2;
	static double meanWR3;
	static double meanQB1 = 200.0;
	static double meanTE1 = 110.0;
	static double RB1floor;
	static double RB2floor;
	static double WR1floor;
	static double WR2floor;
	static double QB1floor;
	static double TE1floor;
	//**************************************************CONSTRUCTOR*****************************************
	public draft(Team[] teams, int rounds)
	{
		draft.teams = teams;
		draft.rounds = rounds;
		availablePlayers = parseSeason();
		Collections.sort(availablePlayers);
		draftRecord = new String[(teams.length * rounds) + rounds];
		// experimental method of determining player pool from which to draw
		// mean data
		int rCount = 5 * teams.length;
		int wCount = 5 * teams.length;
		int qCount = 2 * teams.length;
		int tCount = (int) (1.2 * teams.length);
		
		//arrays to hold player data to get means
		Player[] tmpR = new Player[teams.length];
		Player[] tmpW = new Player[teams.length];
		//counting integers
		int r = 0;
		int w = 0;
		int q = 0;
		int t = 0;

		// iterate through player list to establish tier breakpoints
		for (int i = 0; i < availablePlayers.size(); i++) 
		{
			Player tmp = availablePlayers.get(i);
			double total = 0;
	
			if (tmp.position == 'R') 
			{
				if (r < teams.length) 
				{
					tmpR[r] = tmp;
				}
				else if (r < teams.length* 2) 
				{
					tmpR[r - teams.length] = tmp;
				}
				else if (r < teams.length * 3) 
				{
					tmpR[r - (2 * teams.length)] = tmp;
				}
				r++;
				if (r == teams.length)
				{
					total = 0;
					for (int z = 3; z < 8; z++) 
					{
						total += tmpR[z].proj;
					}
					meanRB1 = total / 5;
				} 
				else if (r == teams.length * 2) 
				{
					total = 0;
					for (int z = 3; z < 8; z++)
					{
						total += tmpR[z].proj;
					}
					meanRB2 = total / 5;
				}
				else if (r == teams.length * 3) 
				{
					total = 0;
					for (int z = 3; z < 8; z++)
					{
						total += tmpR[z].proj;
					}
					meanRB3 = total / 5;
				}
			} 
			else if (tmp.position == 'W')
			{
				if (w < teams.length) 
				{
					tmpW[w] = tmp;
				}
				else if (w < teams.length * 2) 
				{
					tmpW[w - teams.length] = tmp;
				}
				else if (w < teams.length * 3) 
				{
					tmpW[w - (2 * teams.length)] = tmp;
				}
				w++;
				if (w == teams.length)
				{
					total = 0;
					for (int z = 3; z < 8; z++) 
					{
						total += tmpW[z].proj;
					}
					meanWR1 = total / 5;
				} 
				else if (w == teams.length * 2)
				{
					total = 0;
					for (int z = 3; z < 8; z++)
					{
						total += tmpW[z].proj;
					}
					meanWR2 = total/ 5;
				}
				else if (r == teams.length * 3) 
				{
					total = 0;
					for (int z = 3; z < 8; z++)
					{
						total += tmpW[z].proj;
					}
					meanWR3 = total / 5;
				}

			}

		}
		
		System.out.println("mean rb1 is " + meanRB1);
		System.out.println("mean rb2 is " + meanRB2);
		System.out.println("mean rb3 is " + meanRB3);
		System.out.println("mean wr1 is " + meanWR1);
		System.out.println("mean wr2 is " + meanWR2);
		System.out.println("mean wr3 is " + meanWR3);
		
		

	}
	//***********************************************************ADD TEAM*******************************************************
	public boolean addTeam(Team team)
	{
		boolean ret = false;
		for(int i  = 0; i < teams.length; i++)
		{
			if(teams[i] == null)
			{
				teams[i] = team;
				team.draftPosition = i + 1;
				ret = true;
				break;
			}
		}
		return ret;
	}
	//**********************************************************PARSE SEASON PROJECTIONS************************************************************
	public static ArrayList<Player> parseSeason()
	{
		//Pattern to parse a comma seperated values file(.csv)
				Pattern p = Pattern.compile("[,$\n]");
				//List of players that will be used for the draft.
				ArrayList<Player> draftBoard = new ArrayList<Player>();
				
				//Parse Fpros ranks and get player name list
				try {
					File file = new File("ranks.csv");
					Scanner scan = new Scanner(file);
				
					scan.useDelimiter(p);
					scan.nextLine();
					
					while(scan.hasNextLine())
					{
						String tmp = scan.next();
						if(tmp.length() != 6)
						{
							String tmpName = scan.next();
							tmpName = tmpName.substring(1, tmpName.length()-1);
							String tmpTeam = scan.next();
							tmpTeam = tmpTeam.substring(1, tmpTeam.length()-1);
							char tmpPos = scan.next().charAt(1);
							draftBoard.add(new Player(tmpName, tmpTeam, tmpPos));
						}
						scan.nextLine();
					}
					scan.close();
					
					//Append Qb Values to QB players in list
					//qb proj format "Player","Team","ATT","CMP","YDS","TDS","INTS","ATT","YDS","TDS","FL","FPTS"
					File pfile = new File("fpQProj.csv");
					scan = new Scanner(pfile);
					scan.useDelimiter(p);
					while(scan.hasNextLine())
					{
						String tmpName = scan.next();
						//System.out.println(tmpName);
						tmpName = tmpName.substring(1, tmpName.length()-1);
						for(int i = 0; i < draftBoard.size(); i++)
						{
							if(draftBoard.get(i).name.equals(tmpName))
							{
								String tmp;
								scan.next();//team
								tmp = scan.next();
								draftBoard.get(i).passAtt = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).passComp = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								if(!tmp.endsWith("\""))
									tmp += scan.next();
								tmp = tmp.replaceAll(",", "");
								draftBoard.get(i).passYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).passTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).INTs = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).rushAtt = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).rushYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).rushTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								scan.next();//fumbles not tracked
								tmp = scan.next();
								draftBoard.get(i).proj = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								break;
							}
							if(i == (draftBoard.size()-1))
							{
								//System.out.println("No match for " + tmpName);
								if(scan.hasNextLine())
									scan.nextLine();
								break;
							}
						}
					}
					
					scan.close();
					
					//Append WR values to WRs in list
					//WR projection format "Player","Team","ATT","YDS","TDS","REC","YDS","TDS","FL","FPTS"
					File pfile2 = new File("fpWProj.csv");
					scan = new Scanner(pfile2);
					scan.useDelimiter(p);
					while(scan.hasNextLine())
					{
						String tmpName = scan.next();
						//System.out.println(tmpName);
						tmpName = tmpName.substring(1, tmpName.length()-1);
						for(int i = 0; i < draftBoard.size(); i++)
						{
							if(draftBoard.get(i).name.equals(tmpName))
							{
								String tmp;
								scan.next();
								scan.next();
								scan.next();
								scan.next();
								tmp = scan.next();
								draftBoard.get(i).receptions = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								if(!tmp.endsWith("\""))
									tmp += scan.next();
								tmp = tmp.replaceAll(",", "");
								draftBoard.get(i).recYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));	
								tmp = scan.next();
								draftBoard.get(i).recTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								scan.next();
								tmp = scan.next();
								draftBoard.get(i).proj = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								break;
							}
							if(i == (draftBoard.size()-1))
							{
								//System.out.println("No match for " + tmpName);
								if(scan.hasNextLine())
									scan.nextLine();
								break;
							}
						}
					}
					
					scan.close();
					
					//Append RB values to RBs in list
					//RB projection format "Player","Team","ATT","YDS","TDS","REC","YDS","TDS","FL","FPTS"
					File pfile3 = new File("fpRProj.csv");
					scan = new Scanner(pfile3);
					scan.useDelimiter(p);
					while(scan.hasNextLine())
					{
						String tmpName = scan.next();
						System.out.println(tmpName);
						tmpName = tmpName.substring(1, tmpName.length()-1);
						for(int i = 0; i < draftBoard.size(); i++)
						{
							if(draftBoard.get(i).name.equals(tmpName))
							{
								String tmp;
								scan.next();//team
								tmp = scan.next();
								draftBoard.get(i).rushAtt = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								if(!tmp.endsWith("\""))
									tmp += scan.next();
								tmp = tmp.replaceAll(",", "");
								draftBoard.get(i).rushYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).rushTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).receptions = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).recYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).recTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								scan.next();//fumbles
								tmp = scan.next();
								draftBoard.get(i).proj = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								break;
							}
							if(i == (draftBoard.size()-1))
							{
								//System.out.println("No match for " + tmpName);
								if(scan.hasNextLine())
									scan.nextLine();
								break;
							}
						}
					}
					
					scan.close();
					//Append TE values to TEs in list
					//TE projection format "Player","Team","REC","YDS","TDS","FL","FPTS"
					File pfile4 = new File("fpTProj.csv");
					scan = new Scanner(pfile4);
					scan.useDelimiter(p);
					while(scan.hasNextLine())
					{
						String tmpName = scan.next();
						//System.out.println(tmpName);
						tmpName = tmpName.substring(1, tmpName.length()-1);
						for(int i = 0; i < draftBoard.size(); i++)
						{
							if(draftBoard.get(i).name.equals(tmpName))
							{
								String tmp;
								scan.next();//team
								tmp = scan.next();
								draftBoard.get(i).receptions = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp  = scan.next();
								if(!tmp.endsWith("\""))
									tmp += scan.next();
								tmp = tmp.replaceAll(",", "");
								draftBoard.get(i).recYds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								tmp = scan.next();
								draftBoard.get(i).recTds = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								scan.next();//fumbles
								tmp = scan.next();
								draftBoard.get(i).proj = Double.parseDouble(tmp.substring(1, tmp.indexOf("\"", 1)));
								break;
							}
							if(i == (draftBoard.size()-1))
							{
								System.out.println("No match for " + tmpName);
								if(scan.hasNextLine())
									scan.nextLine();
								break;
							}
						}
					}
					
					scan.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//remove all rb/wr/te/qb without projections
				for(int i = 0; i < draftBoard.size(); i ++)
				{
					Player tmp = draftBoard.get(i);
					if(tmp.position != 'K' && tmp.position != 'D')
						if(tmp.proj == 0)
						{
							draftBoard.remove(i);
						}
				}
				return draftBoard;
	}

	//function to assign the standard deviation of a pool of players using a projection window to determine pool size
	public static void assignScarcity(char position, double floor, double ceiling)
	{
		double sum = 0;
		double players = 0;
		double mean = 0;
		double stdev;
		
		for(int i = 0; i < availablePlayers.size(); i ++)
		{	
			Player tmp = availablePlayers.get(i);
			if(tmp.proj < floor)
				break;
			else if(tmp.position == position && tmp.proj < ceiling && tmp.available)
			{
				sum += tmp.proj;
				players ++;
			}
		}
		
		mean = sum/players;
		sum = 0;
		
		for(int i = 0; i < availablePlayers.size(); i ++)
		{
			Player tmp = availablePlayers.get(i);
			if(tmp.proj < floor)
				break;
			else if(tmp.position == position && tmp.proj < ceiling && tmp.available)
			{
				sum += Math.pow((tmp.proj - mean), 2);
			}
			
		}
		stdev = Math.sqrt(sum/(players - 1));
		
		for(int i = 0; i < availablePlayers.size(); i++)
		{
			Player tmp = availablePlayers.get(i);
			if(tmp.proj < floor)
				break;
			else if(tmp.position == position && tmp.proj < ceiling && tmp.available)
			{
				tmp.stdev = (tmp.proj - mean)/stdev;
			}
		}
	}
	//**************************************************COUNTING FETCHERS******************************************************************
	//function to return anticipated next best player available after 'picks' players of position pos are taken
	public static Player getNextRank(int picks, char pos)
	{
		Player ret = new Player();
		int count = 0;
		for(int i = 0; i < availablePlayers.size(); i++)
		{
			Player tmp = availablePlayers.get(i);
			if(tmp.available && tmp.position == pos)
			{
				if(count == picks)
				{
					ret = tmp;
					break;
				}
				count ++;
			}
		}
		return ret;
	}
	//function to count players left of a certain position over a certain projection
	public static int countOver(char position, double floor)
	{
		int ret = 0;
		for(int i = 0; i < availablePlayers.size(); i++)
		{
			Player tmp = availablePlayers.get(i);
			if(tmp.position == position && tmp.available)
			{
				if(tmp.proj > floor)
					ret++;
				else
					break;
			}
		}
		return ret;
	}
	//function to count how many players in a specific position are left within the next i picks
	public static int countPos(int i, char pos)
	{
		int ret = 0;
		for(int j = 0; j < i; j++)
		{
			if(availablePlayers.get(j).position == pos && availablePlayers.get(j).available)
			{
				ret++;
			}
		}
		return ret;
	}
	//function to anticipate the next best player available at a position by a team's next pick
	//looks at teams picking after and decides on the likelihood of them picking that position
	public static Player anticipateNext()
	{
		Player ret = new Player();
		double picks = 0;
		
		
		return ret;
	}
	//***********************************************************INIT*******************************************************
	//method to run a draft
	public static void init(DraftFrame frame) throws InterruptedException, ExecutionException
	{
		//init draftboard
		String[][] board = new String[teams.length][rounds + 1];
		Scanner scan = new Scanner(System.in);
		
		//enter team names
		for(int i = 0; i < teams.length; i++)
		{
			board [i][0] = teams[i].name;
		}
		//make picks, update draftboard and player pool
		for(int i = 0; i < rounds; i ++)
		{
			currentRound++;
			draftRecord[(i * teams.length) + i] = "ROUND " + (1 + i);
			frame.draftRecordView.append(draftRecord[(i * teams.length) + i] + "\n");
			for(int j = 0; j < teams.length; j++)
			{
				
				frame.invalidate();
				frame.repaint();
				//variables for picking team by snake format, # of picks until this team picks next
				int pickingTeam = j;
				char pickPos = 'x';
				Player pick = new Player();
				int tilNext = (2 * (teams.length - j - 1));
				if(i%2 > 0)
				{
					pickingTeam = teams.length - j - 1;
					tilNext = (2 * pickingTeam);
				}
				frame.picker.teamDisplay.setTeam(teams[pickingTeam].roster);
				String tmpPickName = "JOE SCHMO";
				if(teams[pickingTeam].ai)
				{
					int index = teams[pickingTeam].pick(tilNext);
					pick  = availablePlayers.get(index);
					tmpPickName = availablePlayers.get(index).name;
					availablePlayers.get(index).available = false;
					pickPos = availablePlayers.get(index).position;
					frame.picker.Remove(index);
					frame.picker.teamDisplay.setTeam(teams[pickingTeam].roster);
				}
				else
				{
					/*-----method for console input----
					System.out.print("Enter pick for team " + board[j][0] + ": ");
					tmpPickName = scan.next();
					*/
					frame.picker.teamDisplay.setTeam(teams[pickingTeam].roster);
					frame.select.getPick().await();
					frame.select.reset();
					tmpPickName = frame.select.userPick;					
					for(int z = 0; z < availablePlayers.size(); z++)
					{
						if(availablePlayers.get(z).name.equals(tmpPickName))
						{
							teams[pickingTeam].addPlayer(availablePlayers.get(z));
							pick = availablePlayers.get(z);
							availablePlayers.get(z).available = false;
							pickPos = availablePlayers.get(z).position;
							frame.picker.Remove(z);
							break;
						}
					}
				}
				
				board[pickingTeam][i+1] = tmpPickName;
				frame.updateGrid((pickingTeam+1), (i+1), tmpPickName, pickPos);
				System.out.println(tilNext);
				System.out.println("Team " + board[pickingTeam][0] + " selects " + tmpPickName);
				draftRecord[(i * teams.length) + i + j + 1] = teams[pickingTeam].name + " - " + tmpPickName + ", " + pickPos;
				frame.draftRecordView.append(draftRecord[(i * teams.length) + i + j + 1] + "\n");
				frame.scroll.getVerticalScrollBar().setValue(frame.scroll.getVerticalScrollBar().getMaximum());
				//sleep to prevent AI draft picks from being made too fast, to make the program look cooler. SO COOL
				Thread.sleep(50);
			}
			
		}
		
		scan.close();
	}
}
	
