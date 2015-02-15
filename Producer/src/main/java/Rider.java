public class Rider {
	private Bike bike;

	public Rider(Bike bike) {
		this.bike = bike;
	}

	public int prepareForUphill() {
		int gear = bike.getGear();
		for (int idx = 0; idx < 2; idx++) {
			bike.shiftGear(true);
			gear++;
		}
		return gear;
	}
}