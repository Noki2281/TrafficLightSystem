package Class;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainSim implements Runnable{

	public static final int STANDARD = 1;
	public static final int SMART = 2;
	public static final int STANDARD_WAZE = 3;
	public static final int SENSOR = 4;

	public static final int RANDOM = 1;
	public static final int EQUAL = 2;
	public static final int WEIGHT = 3;

	Crossroad cr1;
	ArrayList<Lane> laneList;
	int greenTime = 0;
	int greenLane = 0;
	int time;
	private boolean isLive;
	private int trafficLevel;
	private int maxIterations;
	private int algorithm;
	private int insertMode;


	public MainSim(int trafficLevel, boolean simLive, int maxIterations, int algorithm, int insertMode){
		this.algorithm = algorithm;
		this.insertMode = insertMode;
		this.trafficLevel = trafficLevel;
		this.maxIterations = maxIterations;
		isLive = simLive;
		time = 0;
		cr1 = new Crossroad();
		Lane l1 = new Lane(3,20,10,4);
		l1.setLaneNum(1);
		Lane l2 = new Lane(1,30,10,2);
		l2.setLaneNum(2);
		Lane l3 = new Lane(2,30,10,2);
		l3.setLaneNum(3);
		Lane l4 = new Lane(3,20,10,4);
		l4.setLaneNum(4);
		l1.getRelatives().add(l4);
		l3.getRelatives().add(l4);
		l4.getRelatives().add(l1);
		cr1.getLaneList().add(l1);
		cr1.getLaneList().add(l2);
		cr1.getLaneList().add(l3);
		cr1.getLaneList().add(l4);
		laneList = cr1.getLaneList();
	}

	public void run() {

		while (true){
			time ++;
			Random r = new Random();
			int carNum = r.nextInt(trafficLevel+1);
			switch (insertMode){
				default:
				case RANDOM:{
					//selecting lane
					int laneNum = r.nextInt(laneList.size());
					//add cars to lane
					for (int i=0; i<carNum; i++){
						Lane l = laneList.get(laneNum);
						l.getCarList().add(new Car(time));
					}
					break;
				}
				case EQUAL:{

					//add cars to all lanes
					for (int i=0; i<carNum; i++) {
						for (Lane l : laneList) {
							l.getCarList().add(new Car(time));
							l.reportTotalCars++;
						}
					}
					break;
				}
				case WEIGHT:{
					for (Lane l : laneList) {
						int adjustedCarNum = r.nextInt(l.getWeight()*carNum+1);
						for (int i=0; i<adjustedCarNum; i++) {
							l.getCarList().add(new Car(time));
							l.reportTotalCars++;
						}
					}
					break;
				}
			}
			//Advance red count for all red lanes
			for (int i=0; i<laneList.size(); i++){
				if (i!=greenLane) laneList.get(i).setRed_count(laneList.get(i).getRed_count()+1);
			}
            //change lanes
			switch (algorithm){
				default:
				case STANDARD:{
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
					break;
				}
				case SMART:{
					//if somebody hanuk
					boolean hanuk = false;
					for (int i=0; i<laneList.size(); i++) {
						Lane l = laneList.get(i);
						if (l.getRed_count() > l.getMax_idle()*20){
							greenLane = i;
							hanuk = true;
						}
					}
					if (!hanuk && (greenTime==0 || laneList.get(greenLane).getCarList().size()==0)){
						double maxTWM = 0;
						int maxLaneTWM = 0;
						for (int i=0; i<laneList.size(); i++) {
							double TWM = 0;
							Lane l = laneList.get(i);
							ArrayList<Car> cars = l.getCarList();
							double avg = 0;
							for (Car c : cars){
								avg += time - c.getTime();
							}
							avg = avg==0? 0: avg/(cars.size());
							TWM = l.getWeight()*l.getCarList().size()*avg;
							if (maxTWM < TWM) {
								maxTWM = TWM;
								maxLaneTWM = i;
							}
						}
						greenLane = maxLaneTWM;
					}
					laneList.get(greenLane).setRed_count(0);
					greenTime = laneList.get(greenLane).getMin_green();
					for (int j=0; j<laneList.size(); j++){
						if (laneList.get(greenLane).getRelatives().contains(laneList.get(j)) || laneList.get(greenLane).equals(laneList.get(j)))
						{
							laneList.get(j).is_green(true);
							laneList.get(j).setRed_count(0);
						}
						else laneList.get(j).is_green(false);
					}
					break;
				}
				case STANDARD_WAZE:{

					break;
				}
				case SENSOR:{

					break;
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
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
			for (Lane l: laneList){
                ArrayList<Car> cars = l.getCarList();
                double avg = 0;
                for (Car c : cars){
                    avg += time - c.getTime();
                }
                avg = avg==0? 0: avg/(cars.size());
                l.reportTotalAvgTime += Double.parseDouble(df.format(avg));
				if (l.is_green()){
				    l.reportGreenCount++;
				    if (l.getCarList().size()==0) l.reportIdleGreen++;
					l.passCars();
				}
			}
			//laneList.get(greenLane).passCars();
			if (isLive) {
				try {
					TimeUnit.MILLISECONDS.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (int i=0; i<laneList.size(); i++){
			}
			if (greenTime > 0) greenTime--;

			if (maxIterations == time) return;
		}
	}

	public Crossroad getCr1() {
		return cr1;
	}

	public void setCr1(Crossroad cr1) {
		this.cr1 = cr1;
	}

	public ArrayList<Lane> getLaneList() {
		return laneList;
	}

	public void setLaneList(ArrayList<Lane> laneList) {
		this.laneList = laneList;
	}

	public int getGreenTime() {
		return greenTime;
	}

	public void setGreenTime(int greenTime) {
		this.greenTime = greenTime;
	}

	public int getGreenLane() {
		return greenLane;
	}

	public void setGreenLane(int greenLane) {
		this.greenLane = greenLane;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean live) {
		isLive = live;
	}

	public int getTrafficLevel() {
		return trafficLevel;
	}

	public void setTrafficLevel(int trafficLevel) {
		this.trafficLevel = trafficLevel;
	}

	public int getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(int algorithm) {
		this.algorithm = algorithm;
	}

	public int getInsertMode() {
		return insertMode;
	}

	public void setInsertMode(int insertMode) {
		this.insertMode = insertMode;
	}
}
