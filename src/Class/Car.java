package Class;

import java.util.Date;

public class Car {
	private Date date_of_entry;
	private int time;
	
	public Car(int time){
		date_of_entry = new Date();
		this.time = time;
		
	}

	public Date getDate_of_entry() {
		return date_of_entry;
	}

	public void setDate_of_entry(Date date_of_entry) {
		this.date_of_entry = date_of_entry;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
