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

	public static final int EQUAL = 1;
	public static final int UNIFORM = 2;
	public static final int POISSON = 3;

	Crossroad cr1;
	ArrayList<Lane> laneList;
	int greenTime = 0;
	int greenLane = 0;
	int time;
	private boolean isLive;
	private int maxIterations;
	private int algorithm;
	private int insertMode;
	private static boolean generateCars = true;
	private static int carsInsertedArray[][];
	public static int carsGeneratedSum = 0;
	private int[] totalNumOfCarsPerTime;


	public MainSim(boolean simLive, int maxIterations, int algorithm, int insertMode, Crossroad cr){
		this.algorithm = algorithm;
		this.insertMode = insertMode;
		this.maxIterations = maxIterations;
		isLive = simLive;
		time = 0;
		cr1 = cr;
		laneList = cr1.getLaneList();
		totalNumOfCarsPerTime = new int[maxIterations];
	}

	public void run() {
		if (generateCars) {
			carsGeneratedSum = 0;
			carsInsertedArray = new int[maxIterations][laneList.size()];
		}
		int []u_time = new int[laneList.size()];
		while (true){
			int carsRightNow = 0;
			for (Lane l : laneList){
				carsRightNow += l.getCarList().size();
			}
			totalNumOfCarsPerTime[time] = carsRightNow;
			time ++;
			if (generateCars) {
				Random r = new Random();

				switch (insertMode) {
					default:
					case EQUAL: {
						for (int i=0; i<laneList.size(); i++){
							Lane l = laneList.get(i);
							if (l.getLambda()<=60)
								if (time%(60/l.getLambda()) == 0){
								l.getCarList().add(new Car(time));
								carsInsertedArray[time-1][i] = 1;
								}
							else
								{
									int extra = (r.nextInt(60)<(l.getLambda()%60) ? 1 : 0);
									for (int j=0; j< (l.getLambda()/60) + extra ; j++){
										l.getCarList().add(new Car(time));
										}
									carsInsertedArray[time-1][i] = (l.getLambda()/60) + extra;
								}
							}
						break;
					}
					case UNIFORM: {
						for (int i = 0; i<laneList.size(); i++){
							Lane l = laneList.get(i);
							if (l.getLambda()<=60){
								u_time[i]--;
								//initial time
								if (u_time[i] == -1) u_time[i] = r.nextInt(((2*60)/l.getLambda())+1);
								while (u_time[i] == 0){
									l.getCarList().add(new Car(time));
									carsInsertedArray[time-1][i]++;
									u_time[i] = r.nextInt(((2*60)/l.getLambda())+1);
								}
							}
							else {
								int uniformCarNum = r.nextInt(((l.getLambda()*2)/60)+1);
								for (int j = 0; j<uniformCarNum; j++){
									l.getCarList().add(new Car(time));
									carsInsertedArray[time-1][i]++;
								}
							}
						}
						break;
					}
					case POISSON: {
						for (int i = 0; i<laneList.size(); i++) {
							Lane l = laneList.get(i);
							u_time[i]--;
							//initial time
							if (u_time[i] == -1) u_time[i] = getPoisson((60)/(double)l.getLambda());
							while (u_time[i] == 0){
								l.getCarList().add(new Car(time));
								carsInsertedArray[time-1][i]++;
								u_time[i] = getPoisson((60)/(double)l.getLambda());
							}
						}
						break;
					}
				}
			}
			else {
				for (int i = 0; i<laneList.size(); i++){
					for (int j = 0; j<carsInsertedArray[time-1][i]; j++){
						laneList.get(i).getCarList().add(new Car(time));
					}
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
							if (l.getRed_count() >= l.getCycle_time()){ //select lane with red_count >= cycle_time
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
					//if somebody deadlocked
					boolean hanuk = false;
					for (int i=0; i<laneList.size(); i++) {
						Lane l = laneList.get(i);
						if (l.getRed_count() > l.getMax_idle_time()){
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
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (int i=0; i<laneList.size(); i++){
			}
			if (greenTime > 0) greenTime--;

			if (maxIterations == time) {
				if (generateCars) {
					generateCars = false;
					for (int i = 0; i<MainSim.getCarsInsertedArray().length; i++){
						for (int j=0; j<laneList.size(); j++){
							carsGeneratedSum += MainSim.getCarsInsertedArray()[i][j];
						}
					}
				}
				return;
			}
		}
	}

	public static int getPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
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

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public static boolean isGenerateCars() {
		return generateCars;
	}

	public static void setGenerateCars(boolean generateCars) {
		MainSim.generateCars = generateCars;
	}

	public static int[][] getCarsInsertedArray() {
		return carsInsertedArray;
	}

	public static void setCarsInsertedArray(int[][] carsInsertedArray) {
		MainSim.carsInsertedArray = carsInsertedArray;
	}

	public int[] getTotalNumOfCarsPerTime() {
		return totalNumOfCarsPerTime;
	}

	public void setTotalNumOfCarsPerTime(int[] totalNumOfCarsPerTime) {
		this.totalNumOfCarsPerTime = totalNumOfCarsPerTime;
	}
}
