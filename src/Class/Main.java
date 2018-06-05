import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {

		Crossroad cr1 = new Crossroad();
		Lane l1 = new Lane(3,20,10,4);
		Lane l2 = new Lane(1,30,10,2);
		Lane l3 = new Lane(2,30,10,2);
		Lane l4 = new Lane(3,20,10,4);
		l1.getRelatives().add(l4);
		l3.getRelatives().add(l4);
		l4.getRelatives().add(l1);
		cr1.getLaneList().add(l1);
		cr1.getLaneList().add(l2);
		cr1.getLaneList().add(l3);
		cr1.getLaneList().add(l4);
		int greenTime = 0;
		int greenLane = 0;
		ArrayList<Lane> laneList = cr1.getLaneList();
		
		
		while (true){
			Random r = new Random();
			//selecting lane
			int laneNum = r.nextInt(laneList.size());
			//random number of cars to add
			int carNum = r.nextInt(20);
			//add cars to lane
			//for (int i=0; i<carNum; i++){
			laneList.get(laneNum).getCarList().add(new Car());
			//}
				//change lanes
			//Advance red count for all red lanes
			for (int i=0; i<laneList.size(); i++){
				if (i!=greenLane) laneList.get(i).setRed_count(laneList.get(i).getRed_count()+1);
			}
			//Need to change lanes
			if (greenTime==0){
				for (int i=0; i<laneList.size(); i++){
					Lane l = laneList.get(i);
					if (l.getRed_count() >= l.getMax_idle()){ //select lane with red_count >= max_idle
						greenLane = i;
						greenTime = l.getMin_green();
						l.setRed_count(0);
						for (int j=0; j<laneList.size(); j++){
							if (l.getRelatives().contains(laneList.get(j)) || l.equals(laneList.get(j))) 
								{
									laneList.get(j).is_green(true);
									laneList.get(j).setRed_count(0);
								}
							else laneList.get(j).is_green(false);
							}
						
						break;
					}
				}
			}
//			if (time%5 == 0) {
//				greenLane = (greenLane + 1)%laneList.size();
//				for (int i=0; i<laneList.size(); i++){
//					laneList.get(i).is_green(false);
//						
//				}
//				laneList.get(greenLane).is_green(true);
//			}
			for (Lane l: laneList){
				if (l.is_green()){
					l.passCars();
				}
			}
			//laneList.get(greenLane).passCars();
			try {
				TimeUnit.MILLISECONDS.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Green is on: " + greenLane + " For greentime: " + greenTime);
			for (int i=0; i<laneList.size(); i++){
				System.out.println("Lane: " + (i+1) + " Cars: " + laneList.get(i).getCarList().size() + " Green: " + laneList.get(i).is_green());
			}
			System.out.println("------------------------");
			if (greenTime > 0) greenTime--;
		}
	}

}
