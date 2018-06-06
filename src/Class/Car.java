package Class;

import java.util.Date;

public class Car {
	private Date date_of_entry;
	
	public Car(){
		date_of_entry = new Date();
		
	}

	public Date getDate_of_entry() {
		return date_of_entry;
	}

	public void setDate_of_entry(Date date_of_entry) {
		this.date_of_entry = date_of_entry;
	}
	
}
