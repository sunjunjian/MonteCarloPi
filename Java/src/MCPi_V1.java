/*
 *	Deriving Pi by Monte Carlo method
 *	Version 1.0
 *
 *		- Sam Sun <sunjunjian@gmail.com>, 2012
 */


import java.util.Random;
//import java.io.*;

public class MCPi_V1 {
	public static void main(String[] args) {
		final Counter	cnt = new Counter();
		
		class Worker	extends Thread {
			private int		times;
			
			public	Worker(int value) {
				times = value;
			}

			public void run() {
				Random	generator = new Random();
				
				for (int i=0; i<times; ++i) {
					int		 total = 0;
					int		inside = 0;
					
					for (int j=0; j<1000; ++j, ++total) {
						double	x = generator.nextDouble();
						double	y = generator.nextDouble();
						
						if ((x*x+y*y) <= 1)
							++inside;
					}
					
					// Counter.add() is thread safe
					cnt.add(total, inside);
				}
				
			}
		}
		
		int	maxThrd = 2 * Runtime.getRuntime().availableProcessors();
		System.out.println("This  machine   has " + maxThrd/2 + " hardware threads available.");
		System.out.println("We are going to use " + maxThrd + " threads.\n");
		if (maxThrd > 8)
			maxThrd = 8;
		
		Thread[]	workerGroup = new Thread[maxThrd];
			
		long	start = System.nanoTime();
	
		for (int i=0; i < workerGroup.length; ++i) {
			workerGroup[i] = new Worker(300);
		
			workerGroup[i].start();
		}
		
		for (int i=0; i < workerGroup.length; ++i) {
			try {
				workerGroup[i].join();
			} catch (InterruptedException ignore) {}
		}		
		
		
		long	end = System.nanoTime();
		double	timeSpent = (double)(end - start)/1000000000;
		System.out.println("Total running time is: "+timeSpent+"\n");
		
		cnt.display();
	}
}