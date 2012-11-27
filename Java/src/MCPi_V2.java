/*
 *	Deriving Pi by Monte Carlo method
 *	Version 2.0:
 *		1. Adding a Manager Thread to collect the results.
 *		2. Using java.util.nio package to handle the file IO.
 *
 *					- Sam Sun <sunjunjian@gmail.com>, 2012
 */


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;



public class MCPi_V2{
	final static int LCM = 84000;
	final static String OUTPUT_FILE = "java.out";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public static void main(String args[]) throws IOException{	
		final ReentrantLock	lock = new ReentrantLock();
		final Counter cnt = new Counter();
		List<String> lines = new ArrayList<String>();
		MCPi_V2	text = new MCPi_V2();
		
		class Worker extends Thread {
			private int		Loops;
			
			public	Worker(int value) {
				Loops = value;
			}

			public void run() {
				Random	generator = new Random();

				int		 total = 0;
				int		inside = 0;
				
				for (int i=0; i<Loops; ++i) {				
					for (int j=0; j<1000; ++j, ++total) {
						double	x = generator.nextDouble();
						double	y = generator.nextDouble();
						
						if ((x*x+y*y) <= 1)
							++inside;
					}
				}
				
				try {
					lock.lock();
					cnt.add(total, inside);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					lock.unlock();
				}
			}
		}

		class Manager extends Thread {
			private	int	numThrds;
			private int	numLoops;
			private double Timespent; 
			
			public	Manager(int Thrds, int Loops) {
				numThrds = Thrds;
				numLoops = Loops;
				Timespent = 0;
			}

			public double getTimespent() {
				return	Timespent;
			}

			public double getPi() {
				return cnt.getPi();
			}
			
			public void run() {
				ExecutorService svc = Executors.newFixedThreadPool(numThrds);

				long	start = System.nanoTime();
				
				for (int i = 0; i < numThrds; ++i) {
					svc.execute(new Worker(numLoops));
				}		
				
				svc.shutdown();
				
				boolean	finished = false;
				try {
					// wait until all threads have finished or the time has been reached.
					finished = svc.awaitTermination(10, TimeUnit.MINUTES);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (finished) {
					long	end = System.nanoTime();
					Timespent = (double)(end - start)/1000000000;
					//System.out.println("Total running time is: "+Timespent);
				}
			}
		}		
			
		int	maxThrd = 2 * Runtime.getRuntime().availableProcessors();
		
		System.out.println("This  machine   has " + maxThrd/2 + " hardware threads available.");
		System.out.println("We are going to use " + maxThrd + " threads.\n");
		if (maxThrd > 8)
			maxThrd = 8;		
		
		for (int i = 1; i <= maxThrd; ++i) {
			ExecutorService service = Executors.newSingleThreadExecutor();
			Manager mg = new Manager(i, LCM/i);
			service.execute(mg);
			service.shutdown();			
			
			boolean	finished = false;
			try {
				// wait until the manager has finished or the time has been reached.
				finished = service.awaitTermination(10, TimeUnit.MINUTES);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (finished) {
				String msg = mg.getPi() + "," + i + "," + LCM * 1000 + "," + mg.getTimespent();
				System.out.println(msg);
				lines.add(msg);
				text.writeLinesToFile(lines, OUTPUT_FILE);
			}			
		}
	}	

	void writeLinesToFile(List<String> Lines, String aFileName) throws IOException {
		Path	path = Paths.get(aFileName);
		Files.write(path, Lines, ENCODING);
	}
}







