package acs.logic.util;

public class QueueingTheory {
	
	
	private double generalQuantity; 		// L - Total amount of customers in system (currently parking + waiting for a parking place)
	private double arrivalRate; 			// Lamda - arrival rate of customers to the GRID
	private double totalTimeInSystem; 		// W - Total time in the system (waiting for a parking place + parking)
	private double averageQueueQuantity_q; 	// Lq - amount of customers waiting for a parking in average.
	private double averageWaitingTime_q; 	// Wq - average waiting time in a queue
	private double serviceRate; 			// Meu - how many customers, a single parking spot, is serving (in a unit of time)
	private double averageServiceDuration; 	// 1/Meu - how long customer is using a parking spot
	private int servers; 				// C - how many parking spots are available
	private double overload;				// p - load on the system
	private double getServiceImmediately; 	// p0 - What is the chance of getting service immediately without waiting at all
	private double w_t; 					// W(t) - The chances to get a service within t units of time
	private double r;						// r - ratio
	
	public QueueingTheory() {
		super();
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get L
	public double getGeneralQuantity() {	
		return this.generalQuantity;
	}
	
	// set L
	public void setGeneralQuantity(double arrivalRate, double totalTimeInSystem) { // (lamda, W)
		this.generalQuantity = ((arrivalRate/60) * totalTimeInSystem);		// L = lamda * W
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get lamda
	public double getArrivalRate() {
		return arrivalRate;
	}
	
	// set lamda
	public void setArrivalRate(double arrivalRate) {
		this.arrivalRate = arrivalRate;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get W
	public double getTotalTimeInSystem() {
		return totalTimeInSystem;
	}
	
	// set W
	public void setTotalTimeInSystem(double totalTimeInSystem) {
		this.totalTimeInSystem = totalTimeInSystem;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get Lq
	public double getAverageQueueQuantity_q() {
		return averageQueueQuantity_q;
	}
	
	// set Lq
	public void setAverageQueueQuantity_q(double arrivalRate, double averageWaitingTime_q) {
		this.averageQueueQuantity_q = ((arrivalRate/60) * averageWaitingTime_q); // L = lamda * Wq
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get Wq
	public double getAverageWaitingTime_q() {
		return averageWaitingTime_q;
	}
	
	// set Wq
	public void setAverageWaitingTime_q(double arrivalRate, double generalQuantity, double averageQueueQuantity_q) {
		this.averageWaitingTime_q = (arrivalRate/60)/(generalQuantity-averageQueueQuantity_q);
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get Meu
	public double getServiceRate() {
		return serviceRate;
	}
	
	// set Meu
	public void setServiceRate(double serviceRate) {
		this.serviceRate = serviceRate;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get 1/Meu
	public double getAverageServiceDuration() {
		return averageServiceDuration;
	}

	// set 1/Meu
	public void setAverageServiceDuration(double serviceRate) {
		this.averageServiceDuration = 1/serviceRate;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get C
	public int getServers() {
		return servers;
	}
	
	// set C
	public void setServers(int servers) {
		this.servers = servers;
	}
	
	// -------------------------------------------------------------------------------------------------------
	
	// get p
	public double getOverload() {
		return overload;
	}
	
	// set p
	public void setOverload(double arrivalRate, double servers, double serviceRate) {
		this.overload = arrivalRate/(servers*serviceRate);
	}

	// -------------------------------------------------------------------------------------------------------
	
	public double getGetServiceImmediately() {
		return getServiceImmediately;
	}
	
	public void setGetServiceImmediately() {
		this.getServiceImmediately = Math.pow(sigma(this.servers), -1);
	}
	
	// -------------------------------------------------------------------------------------------------------

	// get W(t)
	public double getW_t() {
		return w_t;
	}
	// set W(t)	
	public void setW_t(double t) {
		double temp1,temp2;
		
		temp2 = Math.pow(Math.E, ((-1) * (this.servers * this.serviceRate - arrivalRate)*t));
		temp1 = (Math.pow(this.r, this.servers) * this.getServiceImmediately) / (factorialIterative(this.servers) * (1-this.overload));
		this.w_t = 1 - (temp1 * temp2);
	}
	
	// -------------------------------------------------------------------------------------------------------

	// get r
	public double getR() {
		return r;
	}
	
	// set r	
	public void setR(double arrivalRate, double serviceRate) {
		this.r = arrivalRate/serviceRate;
	}
	
	// Factorial
	public long factorialIterative (long n) 
	{
	    long r = 1;
	    for ( long i = 1; i <= n; i++ ) 
	    {
	        r*=i;
	    }
	    return r;
	}
	
	// Sigma
    public double sigma(int n){
        double sum = 0;
        for (int i = 0; i <= n-1; i++) { // n=0 --> c-1
            sum += sigmaHelper(i);
        }
        return sum;
    }
    
    public double sigmaHelper(int n){
        double temp = (Math.pow(this.r, n) / factorialIterative(n)) + (Math.pow(this.r, this.servers) / (factorialIterative(this.servers) * (1 - this.overload)));
        return temp;
    }
    
    
}
