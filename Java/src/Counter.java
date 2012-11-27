public class Counter{
	private	int		totalNumber;
	private	int		insideNumber;
	
	public	Counter() {
		totalNumber = 0;
		insideNumber = 0;
	}
	
	// thread safe method
	public synchronized void	add(int total, int inside) {
		totalNumber  += total;
		insideNumber += inside;
	}

	public synchronized void	init() {
		totalNumber  = 0;
		insideNumber = 0;
	}	
	
	public synchronized double	getPi() {
		double	mcpi = 0;
		if (totalNumber != 0)
			mcpi = 4*(double)insideNumber/totalNumber;	
		return	mcpi;
	}
	
	public synchronized void	display() {
		System.out.println("The Monte Carlo Pi is: " + getPi());
	}
}