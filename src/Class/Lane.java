package Class;

import java.util.ArrayList;

public class Lane {
	private ArrayList<Car> carList;
	private int weight,max_idle,red_count,min_green;
	private boolean is_green;
	private int cars_crossing_per_second;
	private ArrayList<Lane> relatives;
	private int laneNum;
	public int reportTotalCars = 0;
	public double reportTotalAvgTime = 0;
	public int reportIdleGreen = 0;
	public int reportGreenCount = 0;
	
	public Lane(int weight, int max_idle, int min_green,int cars_crossing_per_second) {
		super();
		this.carList = new ArrayList<>();
		this.weight = weight;
		this.max_idle = max_idle;
		this.red_count = 0;
		this.min_green = min_green;
		this.cars_crossing_per_second = cars_crossing_per_second;
		this.relatives = new ArrayList<>();
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
	public int getMax_idle() {
		return max_idle;
	}
	public void setMax_idle(int max_idle) {
		this.max_idle = max_idle;
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
}
