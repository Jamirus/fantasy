import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
//class to parse and analyze data for daily fantasy
//extraneous and not used in current program.
public class DraftKing {
	//will be initialized from DKSalraies in descending order of salary cap hit
	ArrayList<Player> players = new ArrayList<Player>();
	double salaryCap = 0;
	
	public DraftKing(double salaryCap)
	{
		this.salaryCap = salaryCap;
		File cap = new File("DKSalaries.csv");
		File wqp = new File("wqp.csv");
		File wrp = new File("wrp.csv");
		File wwp = new File("wwp.csv");
		File wtp = new File("wtp.csv");
		File wdp = new File("wdp.csv");
		
		try {
			parseCapList(cap);
			parseProjections(wqp,wrp,wwp,wtp,wdp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//function to retrieve most efficient points/cap players per position
	public void findEfficient()
	{
		Player q = new Player();
		Player r = new Player();
		Player w = new Player();
		Player t = new Player();
		Player d = new Player();
		
		for(int i = 0; i < players.size(); i++)
		{
			Player tmp = players.get(i);
			switch(tmp.position)
			{
			case 'Q':
				if((tmp.proj/tmp.cost) > (q.proj/q.cost))
					q = tmp;
				break;
			case 'W':
				if((tmp.proj/tmp.cost) > (w.proj/w.cost))
					w = tmp;
				break;
			case 'R':
				if((tmp.proj/tmp.cost) > (r.proj/r.cost))
					r = tmp;
				break;
			case 'T':
				if((tmp.proj/tmp.cost) > (t.proj/t.cost))
					t = tmp;
				break;
			case 'D':
				if((tmp.proj/tmp.cost) > (d.proj/d.cost))
					d = tmp;
				break;
			}
		}
		
		System.out.println("Q: " + q.name + ", " + (q.proj/q.cost));
		System.out.println("W: " + w.name + ", " + (w.proj/w.cost));
		System.out.println("R: " + r.name + ", " + (r.proj/r.cost));
		System.out.println("T: " + t.name + ", " + (t.proj/t.cost));
		System.out.println("D: " + d.name + ", " + (d.proj/d.cost));
		
		//std exp
		//start by finding totals and means
		double rTotal = 0;
		double wTotal = 0;
		double qTotal = 0;
		double tTotal = 0;
		double rPlayers = 0;
		double wPlayers = 0;
		double qPlayers = 0;
		double tPlayers = 0;
		
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.proj >= 8)
			{
				if(tmp.position == 'R')
				{
					rTotal += tmp.proj;
					rPlayers += 1;
				}
			}
			if(tmp.proj >= 8)
			{
				if(tmp.position == 'W')
				{
					wTotal += tmp.proj;
					wPlayers += 1;
				}
			}
			if(tmp.proj >= 12)
			{
				if(tmp.position == 'Q')
				{
					qTotal += tmp.proj;
					qPlayers += 1;
				}
			}
			if(tmp.proj >= 5)
			{
				if(tmp.position == 'T')
				{
					tTotal += tmp.proj;
					tPlayers += 1;
				}
			}
			
		}
		double rMean = rTotal/rPlayers;
		double wMean = wTotal/wPlayers;
		double qMean = qTotal/qPlayers;
		double tMean = tTotal/tPlayers;
		
		
		//calculate std deviation
		double rSum = 0;
		double wSum = 0;
		double qSum = 0;
		double tSum = 0;
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.proj >= 8)
			{
				if(tmp.position == 'R')
				{
					rSum += (Math.pow((rMean - tmp.proj),2)/(rTotal - 1));
				}
			}
			if(tmp.proj >= 8)
			{
				if(tmp.position == 'W')
				{
					wSum += (Math.pow((wMean - tmp.proj),2)/(wTotal - 1));
				}
			}
			if(tmp.proj >= 10)
			{
				if(tmp.position == 'Q')
				{
					qSum += (Math.pow((qMean - tmp.proj),2)/(qTotal - 1));
				}
			}
			if(tmp.proj >= 12)
			{
				if(tmp.position == 'Q')
				{
					tSum += (Math.pow((tMean - tmp.proj),2)/(tTotal - 1));
				}
			}
			
		}
		
		double rSDV = Math.sqrt(rSum);
		double wSDV = Math.sqrt(wSum);
		double qSDV = Math.sqrt(qSum);
		double tSDV = Math.sqrt(tSum);
		
		System.out.println("R,W,Q: " + rMean + " " + rSDV + ", " + wMean + " " + wSDV + ", " + qMean + " " + qSDV);
		
		//sort into tiers by players stdvs from mean
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.position == 'R')
			{
				tmp.stdev = (tmp.proj - rMean)/rSDV;
			}
			else if(tmp.position == 'W')
			{
				tmp.stdev = (tmp.proj - wMean)/wSDV;
			}	
			else if(tmp.position == 'Q')
			{
				tmp.stdev = (tmp.proj - qMean)/qSDV;
			}
			else if(tmp.position == 'T')
			{
				tmp.stdev = (tmp.proj - tMean)/tSDV;
			}
			
			//System.out.println(tmp.position + ", " + tmp.name + ", " + (tmp.tier*100000)/tmp.cost);
			//System.out.println();
		}
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.position == 'Q')
			{
				System.out.println(tmp.position + ", " + tmp.name + ", " + (tmp.stdev*100000)/tmp.cost);
				System.out.println();
			}
		}
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.position == 'R')
			{
				System.out.println(tmp.position + ", " + tmp.name + ", " + (tmp.stdev*100000)/tmp.cost);
				System.out.println();
			}
		}
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.position == 'W')
			{
				System.out.println(tmp.position + ", " + tmp.name + ", " + (tmp.stdev*100000)/tmp.cost);
				System.out.println();
			}
		}
		for(int i = 0; i < players.size(); i ++)
		{
			Player tmp = players.get(i);
			if(tmp.position == 'T')
			{
				System.out.println(tmp.position + ", " + tmp.name + ", " + (tmp.stdev*100000)/tmp.cost);
				System.out.println();
			}
		}
	}
//*****************************************BLACK HOLE OF PARSING******************************************************************************************************	

	
	//function to parse dksalaries csv, format "Position","Name",Salary,"GameInfo","AvgPointsPerGame","teamAbbrev"
	public void parseCapList(File file) throws FileNotFoundException
	{
		Scanner scan = new Scanner(file);
		Pattern p = Pattern.compile("[,$\n]");
		scan.useDelimiter(p);
		scan.nextLine();
		while(scan.hasNextLine())
		{
			Player tmp = new Player();
			tmp.position = scan.next().charAt(1);
			String tmpName = scan.next();
			tmp.name = tmpName.substring(1, tmpName.length()-1);
			tmp.cost = Double.parseDouble(scan.next());
			players.add(tmp);
			scan.nextLine();
			//diagnostic
			System.out.println(tmp.name + ", " + tmp.position + ", " + tmp.cost);
		}
		scan.close();
	}
	
	
	//function to parse fantasy pros weekly projections and append to player list
	//loops to parse each file are laregely the same code with some tweaking for annoying differences
	//csv file is tokenized with pattern p for commas and end lines, then we must iterate through stats/lines we will not use
	//to find names and projections
	public void parseProjections(File qw, File rw, File ww, File tw, File dw) throws FileNotFoundException
	{
		/*
		Scanner scan = new Scanner(qw);
		Pattern p = Pattern.compile("[,$\n]");
		scan.useDelimiter(p);
		scan.nextLine();
		scan.nextLine();
		
		//parse qbs, known mismatches mitch trubisky -- mithcell, mahomes -- mahomes II
		while(scan.hasNextLine())
		{
			String tmpName = scan.next();
			tmpName = tmpName.substring(1,tmpName.length() -1);
			for(int i = 0; i < 10; i++)
			{
				scan.next();
			}
			String tmpProjString = scan.next();
			double tmpProj = Double.parseDouble(tmpProjString.substring(1,tmpProjString.length()-2 ));
			
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).name.length() >= tmpName.length())
					if(players.get(i).name.substring(0,tmpName.length()).equals(tmpName))
					{
						players.get(i).proj = tmpProj;
						break;
					}
				else if(i == (players.size() - 1))
					System.out.println("no match for " + tmpName);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		//parse rbs
		scan = new Scanner(rw);
		scan.useDelimiter(p);
		scan.nextLine();
		scan.nextLine();
		while(scan.hasNextLine())
		{
			String tmpName = scan.next();
			tmpName = tmpName.substring(1,tmpName.length() -1);
			//team 1 att 2 yds 3 tds 4 rec 5 yds 6tds 7 fl 8
			for(int i = 0; i < 8; i ++)
			{
				scan.next();
			}
			String tmpProjString = scan.next();
			double tmpProj = Double.parseDouble(tmpProjString.substring(1,tmpProjString.length()-2 ));
			
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).name.length() >= tmpName.length())
					if(players.get(i).name.substring(0,tmpName.length()).equals(tmpName))
					{
						players.get(i).proj = tmpProj;
						break;
					}
				else if(i == (players.size() - 1))
					System.out.println("no match for " + tmpName);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		//parse wrs
		scan = new Scanner(ww);
		scan.useDelimiter(p);
		scan.nextLine();
		scan.nextLine();
		while(scan.hasNextLine())
		{
			String tmpName = scan.next();
			tmpName = tmpName.substring(1,tmpName.length() -1);
			//team 1 att 2 yds 3 tds 4 rec 5 yds 6tds 7 fl 8
			for(int i = 0; i < 8; i ++)
			{
				scan.next();
			}
			String tmpProjString = scan.next();
			double tmpProj = Double.parseDouble(tmpProjString.substring(1,tmpProjString.length()-2 ));
			
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).name.length() >= tmpName.length())
					if(players.get(i).name.substring(0,tmpName.length()).equals(tmpName))
					{
						players.get(i).proj = tmpProj;
						break;
					}
				else if(i == (players.size() - 1))
					System.out.println("no match for " + tmpName);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		//parse tes
		scan = new Scanner(tw);
		scan.useDelimiter(p);
		scan.nextLine();
		scan.nextLine();
		while(scan.hasNextLine())
		{
			String tmpName = scan.next();
			tmpName = tmpName.substring(1,tmpName.length() -1);
			//team 1 rec2 yds 3 tds4 fl5
			for(int i = 0; i < 5; i ++)
			{
				scan.next();
			}
			String tmpProjString = scan.next();
			double tmpProj = Double.parseDouble(tmpProjString.substring(1,tmpProjString.length()-2 ));
			
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).name.length() >= tmpName.length())
					if(players.get(i).name.substring(0,tmpName.length()).equals(tmpName))
					{
						players.get(i).proj = tmpProj;
						break;
					}
				else if(i == (players.size() - 1))
					System.out.println("no match for " + tmpName);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		//parse d
		scan = new Scanner(dw);
		scan.useDelimiter(p);
		scan.nextLine();
		scan.nextLine();
		while(scan.hasNextLine())
		{
			String tmpName = scan.next();
			System.out.println(tmpName);
			String[] tmpSplit = tmpName.split(" ");
			tmpName = tmpSplit[tmpSplit.length-1];
			tmpName = tmpName.substring(0, tmpName.length()-1);
			System.out.println(tmpName);
			//team1 sack 2 int 3 fr 4 ff 5 td 6 assist 7 safety 8 pa 9 yds 10
			for(int i = 0; i < 10; i ++)
			{
				scan.next();
			}
			String tmpProjString = scan.next();
			double tmpProj = Double.parseDouble(tmpProjString.substring(1,tmpProjString.length()-2 ));
			
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).name.length() >= tmpName.length())
					if(players.get(i).name.substring(0,tmpName.length()).equals(tmpName))
					{
						players.get(i).proj = tmpProj;
						break;
					}
				else if(i == (players.size() - 1))
					System.out.println("no match for " + tmpName);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		scan.close();
		
		*/
		FileInputStream q = new FileInputStream(qw);
		byte[] tmp = new byte[(int)qw.length()];
		try {
			q.read(tmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String qStr = new String(tmp,StandardCharsets.UTF_8);
		System.out.println("whaaaat");
		System.out.println(qw.length());
		System.out.println(qStr);
		
		String[] tokens = qStr.split("[\",\\n]+");
		System.out.println(tokens.length);
		for(int i = 0; i < tokens.length; i++)
		{
			System.out.println(i + ": " + tokens[i]);
		
		}
	}

	//*******************************************END OF PARSING HELL**********************************************************************************************************
}
