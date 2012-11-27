/*
 *	Deriving Pi by Monte Carlo method
 *
 *		- Sam Sun <sunjunjian@gmail.com>, 2012
 */


#include 	"boost/thread.hpp"
#include	<cstdlib>
#include	<ctime>
#include 	<iostream>
#include	<string>
#include	<vector>
#include	<fstream>
#include	<sstream>
#include	<iterator> 
#define 	SEED 	35791246	// Used for RNG
#define		LCM		840			// It's used to make sure the loops will be divided evenly
								// 	  among the workers. 

using	namespace	std;


boost::mutex	io_mutex;
long			total_number;
long			inside_number;



/**
 *	This worker generates random numbers within a unit square. Any point may
 *		or may not fall in the inner circle. We can derive the value of Pi
 *		by calculating the ratio.
 *		
 *		argument:
 *			loops - unit in thousand
 */
void	worker( int	loops )
{
		long	total = 0;
		long	inside  = 0;

		for (int i = 0; i < loops; ++i)
		{
				for (long j = 0; j < 1000; ++j, ++total)
				{
						double	x = rand()/(double) RAND_MAX;
						double	y = rand()/(double) RAND_MAX;

						if ( (x*x+y*y) <= 1 )
								++inside;				
				}
		}

		// multi-thread safe io
		boost::mutex::scoped_lock	lock(io_mutex);
		total_number  += total;
		inside_number += inside;
}



/**
 *	This function performs Monte Carlo calculation.
 *		argument:
 *			num_threads - the number of threads will be used
 *			num_loops   - the number of loops will be run, unit in thousand
 */
double	manager( int	num_threads, int	num_loops) {
		total_number 	= 0;
		inside_number	= 0;
		double			mcPi;	

		boost::thread_group	thread_pool;

		// spawn worker threads according to the number of physical threads
		for (int i = 0; i < num_threads; ++i)
		{
				boost::thread 	*t = new 	boost::thread(worker, num_loops);
				thread_pool.add_thread(t);
		}

		// wait for all threads to finish
		thread_pool.join_all();

		mcPi = 4*((double)inside_number/(double)total_number);
		cout << "Total # of loops is: " << total_number << endl;
		cout << "        pi       is: " << mcPi << endl;

		return	mcPi;
}


     

int main( int argc, char* argv[] )
{

		clock_t			start, end;
		double			time_spent;
		int				max_threads;
		double			mcPi;
		string			file_name = "c.out";
		vector<string>	lines;		

	 	// Seed the RNG - Ramdon Number Generator
		srand( SEED );		

		cout << string( 30, '\n' );

		max_threads = 2 * boost::thread::hardware_concurrency();
		cout << "This  machine  has  "<< max_threads/2 << " hardware threads available.\n" << endl;
		// We don't want to spawn too many threads
		if ( max_threads > 8 )
				max_threads = 8;
		cout << "We are going to use "<< max_threads << " threads.\n" << endl;


		// main part
		for ( int i = 1; i <= max_threads; ++i )
		{
				for ( int j = 100; j <= 1000; j += 100 )
				{
						start = clock();
					
						// first argument is num_threads, second is num_loops
						mcPi = manager( i, j * LCM / i );

						end = clock();
						time_spent = (double)(end - start) / CLOCKS_PER_SEC;
						cout << "Total running time    is: " << time_spent << "  secs" << endl;	

						ostringstream	aLine;
						aLine << mcPi << "," << i << "," << 1000 * j * LCM << "," << time_spent << endl;
						lines.push_back( aLine.str() );
				}
		}

		// ostream_iterator<string>	os(cout);
		// copy( lines.begin(), lines.end(), os );

		// output result to ofile as CSV		
		ofstream		ofile( file_name.c_str(), ios_base::out | ios_base::trunc );
		std::ostream_iterator<string>	osi( ofile, "" );
		if ( ofile.is_open() )
		{
				copy( lines.begin(), lines.end(), osi );
				ofile.close();				
		}

		return 0;
}