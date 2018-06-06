package Class;

import java.util.ArrayList;

public class Crossroad {
	private ArrayList<Lane> LaneList;
	
	public Crossroad(){
		LaneList = new ArrayList<>();
	}

	public ArrayList<Lane> getLaneList() {
		return LaneList;
	}

	public void setLaneList(ArrayList<Lane> laneList) {
		LaneList = laneList;
	}
	
}
