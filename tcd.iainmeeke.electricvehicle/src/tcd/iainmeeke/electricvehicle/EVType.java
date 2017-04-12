package tcd.iainmeeke.electricvehicle;

public enum EVType {
	LEAF(24000,3000),
	VOLT(16000,3300),
	TESLA(53000,9600);
	
	private final double capacity; //in Kwh
	private final double chargePower; //in kW
	
	EVType(double capacity, double chargePower){
		this.capacity = capacity;
		this.chargePower = chargePower;
	}
	
	public double getCapacity(){
		return this.capacity;
	}
	
	public double getChargePower(){
		return this.chargePower;
	}
}
