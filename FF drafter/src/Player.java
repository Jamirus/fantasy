//Class to hold data for one player, some data not applicable to all positions
//some data not used, but parsed and kept for future AI applications
public class Player implements Comparable<Player>{
	String name = "bozo";			//player name
	char position = 'z';			//player position (Q-qb, R-rb, W-wr,T-te, K-kicker, d-defense)
	boolean available = false;		//Boolean to determine if a player is still available to be drafted
	double proj = 0;				//fp Projected points
	double stdev = 0;				//Player stdev from mean in player pool during draft
	int tier = 3;					//player projection tier
	double cost = 999999;			//cost variable for draft kings lineup ai
	String team = "FA";
	
	public Player(){
		
	}
	public Player(String name, String team, char position)
	{
		this.name = name;
		this.position = position;
		this.team = team;
		available = true;
	}
	
	public String show()
	{
		return this.name + " " + this.position + " " + this.proj;
	}
	@Override
	public int compareTo(Player x) {
		int ret = 0;
		ret = (int)(x.proj - this.proj);
		return ret;
	}
	
	public String toString()
	{
		return(this.position + ": " + this.name + " - " + this.proj);
	}

}
