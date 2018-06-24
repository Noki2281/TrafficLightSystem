package Class;

import java.util.ArrayList;

public class Crossroad {
	private ArrayList<Lane> LaneList;
	
	public Crossroad(){
		LaneList = new ArrayList<>();
	}
	public Crossroad(Crossroad cr){
		LaneList = new ArrayList<Lane>();
		Lane[] laneArr = new Lane[cr.getLaneList().size()];
		for (int i=0; i<laneArr.length; i++){
			Lane l = cr.getLaneList().get(i);
			laneArr[i] = new Lane(l.getLaneNum(),l.getWeight(),l.getCycle_time(),l.getMin_green(),l.getCars_crossing_per_second(),l.getLambda(),l.getMax_idle_time());
		}
		for (int i=0; i<laneArr.length; i++){
			Lane l = cr.getLaneList().get(i);
			for (int j=0; j<laneArr.length; j++){
				Lane r = cr.getLaneList().get(j);
				if (l.getRelatives().contains(r)){
					laneArr[i].getRelatives().add(laneArr[j]);
				}
			}
		}
		for (int i=0;i<laneArr.length;i++)
			LaneList.add(laneArr[i]);
	}

	public ArrayList<Lane> getLaneList() {
		return LaneList;
	}

	public void setLaneList(ArrayList<Lane> laneList) {
		LaneList = laneList;
	}
	
}
