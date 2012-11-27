#!/usr/bin/env
# -*- coding:utf-8 -*-

"""	
	Derive Pi by Monte Carlo method
	version 2
	I am gonna do the same thing but with multi-processing instead of multi-threading.

			- Sam Sun <sunjunjian@gmail.com>, 2012
"""

import random
import time
import io
from multiprocessing import Pool


def worker(num_loops):
	inside = 0

	for i in range(num_loops * 1000):
		x = random.random()
		y = random.random()

		if (x*x + y*y) <= 1:
			inside += 1

	return inside



if __name__ == '__main__':
	# number of threads to be used
	num_procs = 4
	# LCM is used to distribute workload among workers
	LCM = 840
	# output
	lines = []

	for i in range(1,num_procs + 1):
		start = time.time()

		# using a pool of workers
		pool = Pool(processes = i)
		x = [LCM/i] * i
		y = sum(pool.map(worker, x))

		mcpi = 4 * (float(y) / float(LCM * 1000))
		#print 'Monte Carlo Pi is :   ', mcpi

		elapsed = (time.time() - start)

		# need to make sure lines are Unicode chars
		lines.append(repr(mcpi) + u',' + repr(i) + u',' + repr(LCM * 1000) + u',' + repr(elapsed) + u'\n')

	with io.open('python.out', 'w') as file:
		# writelines method only takes Unicode (no string)
		file.writelines(lines)

