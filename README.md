Preliminary
===========
Suppose we have a circle with radius r=1 inscribed within a square. The area of the ciecle is πr^3 = π1^3 = π, and the area of the square is (2r)^2 = 2^2 = 4. The ratio of the area of the circle to the area of the square is

<pre>
      area of circle         πr^2       π
ρ = ------------------- = ---------- = ---
      area of square        (2r)^2      4
</pre> 

If we could compute the ratio, then we could multiply it by 4 to obtain the value of π.


The Monte Carlo Method
======================
Monte Carlo methods can be thought of as statistical simulation methods that utilize a sequences of random numbers to perform the simulation.  The name "Monte Carlo'' was coined by Nicholas Constantine Metropolis (1915-1999) and inspired by Stanslaw Ulam (1909-1986), because of the similarity of statistical simulation to games of chance, and because Monte Carlo is a center for gambling and games of chance.  In a typical process one compute the number of points in a set A that lies inside box R.  The ratio of the number of points that fall inside A to the total number of points tried is equal to the ratio of the two areas.  The accuracy of the ratio depends on the number of points used, with more points leading to a more accurate value.. 

Every time a Monte Carlo simulation is made using the same sample size n it will come up with a slightly different value. The values converge very slowly of the order O(n^(-1/2)).  This property is a consequence of the Central Limit Theorem.


Other Thoughts
==============
Monte Carlo method can also be used to approximate the area A under a curve y = f(x) for a<x<b, or, to approximate the area of a region defined by a set of inequalities or constraints.
