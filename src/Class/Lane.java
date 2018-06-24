package Class;

import java.util.ArrayList;

public class Lane {
	private ArrayList<Car> carList;
	private int weight, cycle_time,red_count,min_green,max_idle_time;
	private boolean is_green;
	private int cars_crossing_per_second;
	private ArrayList<Lane> relatives;
	private int laneNum;
	public int reportTotalCars = 0;
	public double reportTotalAvgTime = 0;
	public int reportIdleGreen = 0;
	public int reportGreenCount = 0;
	private int lambda;
	
	public Lane(int laneNum, int weight, int cycle_time, int min_green, int cars_crossing_per_second, int lambda,int max_idle_time) {
		super();
		this.laneNum = laneNum;
		this.carList = new ArrayList<>();
		this.weight = weight;
		this.cycle_time = cycle_time;
		this.red_count = 1000;
		this.min_green = min_green;
		this.cars_crossing_per_second = cars_crossing_per_second;
		this.relatives = new ArrayList<>();
		this.lambda = lambda;
		this.max_idle_time = max_idle_time;
	}
	public ArrayList<Car> getCarList() {
		return carList;
	}
	public void setCarList(ArrayList<Car> carList) {
		this.carList = carList;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getCycle_time() {
		return cycle_time;
	}
	public void setCycle_time(int cycle_time) {
		this.cycle_time = cycle_time;
	}
	public int getRed_count() {
		return red_count;
	}
	public void setRed_count(int red_count) {
		this.red_count = red_count;
	}
	public int getMin_green() {
		return min_green;
	}
	public void setMin_green(int min_green) {
		this.min_green = min_green;
	}
	public boolean is_green() {
		return is_green;
	}
	public void is_green(boolean is_green) {
		this.is_green = is_green;
	}
	public void passCars() {
		for (int i=0; i<Math.min(carList.size(), cars_crossing_per_second); i++){
			carList.remove(0);
			reportTotalCars++;
		}
	}
	public int getCars_crossing_per_second() {
		return cars_crossing_per_second;
	}
	public void setCars_crossing_per_second(int cars_crossing_per_second) {
		this.cars_crossing_per_second = cars_crossing_per_second;
	}
	public ArrayList<Lane> getRelatives() {
		return relatives;
	}
	public void setRelatives(ArrayList<Lane> relatives) {
		this.relatives = relatives;
	}

	public int getLaneNum() {
		return laneNum;
	}

	public void setLaneNum(int laneNum) {
		this.laneNum = laneNum;
	}

	public int getLambda() {
		return lambda;
	}

	public void setLambda(int lambda) {
		this.lambda = lambda;
	}

	public int getMax_idle_time() {
		return max_idle_time;
	}

	public void setMax_idle_time(int max_idle_time) {
		this.max_idle_time = max_idle_time;
	}
}
