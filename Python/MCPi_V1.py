#!/usr/bin/env
# -*- coding:utf-8 -*-

"""	
	Derive Pi by Monte Carlo method
	version 1
	I am gonna use nulti-threading.
			- Sam Sun <sunjunjian@gmail.com>, 2012
"""


import random
import time
import threading
import io
# import timeit


class Counter:
	def __init__(self):
		self.total_number = 0
		self.inside_number = 0
		self.mcpi = 0

	def reset(self):
		self.total_number = 0
		self.inside_number = 0
		self.mcpi = 0

	def add(self, total, inside):
		self.total_number += total
		self.inside_number += inside

	def getPi(self):
		if self.total_number != 0:
			self.mcpi = 4 * (float(self.inside_number) / float(self.total_number))
		else:
			self.mcpi = 0
		return	self.mcpi	

	def display(self):
		print 'Monte Carlo Pi is :   ', self.getPi()


def worker(num_loops, cnt):
	"""	The worker, invoked in a manager. 
			'num_loops' - the number of loops we want to perform the Monte Carlo 
							simulations, with unit in thousand. 
			'cnt' 		- the object where we store the counters.
	"""	

	global mutex

	for i in range(num_loops):
		total = 0
		inside =0

		for j in range(1000):
			x = random.random()
			y = random.random()

			if (x*x + y*y) <= 1:
					inside += 1

			total += 1

		mutex.acquire()
		cnt.add(total, inside)
		mutex.release()


def manager(num_thrds, num_loops):
	"""	The manager function spawns workers. 
			'num_thrds' - the number of workers. 
			'num_loops' - the number of loops we want to perform the Monte Carlo 
							simulations, with unit in thousand.
	"""

	mutex.acquire()
	cnt.reset()
	mutex.release()

	# initialize the thread pool
	thread_pool = []

	for i in range(num_thrds):
		thrd = threading.Thread(target=worker, args=(num_loops, cnt))
		thread_pool.append(thrd)

	# start threads
	for i in range(len(thread_pool)):
		thread_pool[i].start()

	for i in range(len(thread_pool)):
		threading.Thread.join(thread_pool[i])

	#cnt.display()



if __name__ == "__main__":
	global mutex

	# initialize the mutex
	mutex = threading.Lock()
	# initialize the result Counter
	cnt = Counter()
	# number of threads to be used
	num_thrds = 4
	# LCM is used to distribute workload among workers
	LCM = 840
	# output
	lines = []

	for i in range(1,num_thrds + 1):
		start = time.time()
		manager(i, LCM/i)
		elapsed = (time.time() - start)

		# need to make sure lines are Unicode chars
		lines.append(repr(cnt.getPi()) + u',' + repr(i) + u',' + repr(LCM * 1000) + u',' + repr(elapsed) + u'\n')

	with io.open('python.out', 'w') as file:
		# writelines method only takes Unicode (no string)
		file.writelines(lines)

