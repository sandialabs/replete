/*
Copyright (C) 2001-2004  Kyle Siegrist, Dawn Duehring
Department of Mathematical Sciences
University of Alabama in Huntsville

This program is part of Virtual Laboratories in Probability and Statistics,
http://www.math.uah.edu/stat/.

This program is licensed under a Creative Commons License. Basically, you are free to copy,
distribute, and modify this program, and to make commercial use of the program.
However you must give proper attribution.
See http://creativecommons.org/licenses/by/2.0/ for more information.
*/
package org.jscience.mathematics.statistics;

/**
 * This class models an abstract implementation of a real probability
 * distribution.
 *
 * @author Kyle Siegrist
 * @author Dawn Duehring
 * @version August, 2003
 */
public abstract class Distribution {
    //Constants
    /** DOCUMENT ME! */
    public final static int DISCRETE = 0;

    //Constants
    /** DOCUMENT ME! */
    /** DOCUMENT ME! */
    public final static int CONTINUOUS = 1;

    //Constants
    /** DOCUMENT ME! */
    /** DOCUMENT ME! */
    /** DOCUMENT ME! */
    public final static int MIXED = 2;

    //Variables
    /** DOCUMENT ME! */
    private int type;

    //Objects
    /** DOCUMENT ME! */
    private DistributionDomain domain;

    /**
     * This method defines the probability density function of the
     * distribution. This method must be overridden for any specific
     * distribuiton.
     *
     * @param x a number in the domain of the distribution
     *
     * @return DOCUMENT ME!
     */
    public abstract double getDensity(double x);

    /**
     * This method sets the domain of the distribution for purposes of
     * data collection and for default computations.  For a discrete
     * distribution, the domain specifies the values on which the distribution
     * is defined (although truncated if the true set of values is infinite).
     * For a continuous distribution, domain defines the interval on which the
     * distribuiton is defined (truncated if the true interval is infinite).
     *
     * @param d the domain
     */
    protected void setDomain(DistributionDomain d) {
        domain = d;
        type = domain.getType();
    }

    /**
     * This method sets the domain of the distribution for purposes of
     * data collection and for default computations.
     *
     * @param a lower value or bound of the domain
     * @param b the upper value or bound of the domain
     * @param w the width (step size) of the domain
     * @param t the type of domain (DISCRETE or CONTINUOUS)
     */
    protected void setDomain(double a, double b, double w, int t) {
        setDomain(new DistributionDomain(a, b, w, t));
    }

    /**
     * This method returns the domain of the distribution.
     *
     * @return the domain
     */
    public DistributionDomain getDomain() {
        return domain;
    }

    /**
     * This method returns the type of the distribution (discrete or
     * continuous).
     *
     * @return the type
     */
    public final int getType() {
        return type;
    }

    /**
     * This method returns the largest (finite) value of the
     * probability density function on the finite set of domain values. This
     * method should be overridden if the maximum value is known in closed
     * form.
     *
     * @return the maximum value of the probability density function
     */
    public double getMaxDensity() {
        double max = 0;
        double d;

        for (int i = 0; i < domain.getSize(); i++) {
            d = getDensity(domain.getValue(i));

            if ((d > max) & (d < Double.POSITIVE_INFINITY)) {
                max = d;
            }
        }

        return max;
    }

    /**
     * This method returns a default approximation of the moment of a
     * specified order about a specified point.  This method should be
     * overriden if the moment is known in closed form.
     *
     * @param a the center
     * @param n the order
     *
     * @return the moment of order n about a
     */
    public double getMoment(double a, int n) {
        double sum = 0;
        double x;
        double w;

        if (type == DISCRETE) {
            w = 1;
        } else {
            w = domain.getWidth();
        }

        for (int i = 0; i < domain.getSize(); i++) {
            x = domain.getValue(i);
            sum = sum + (Math.pow(x - a, n) * getDensity(x) * w);
        }

        return sum;
    }

    /**
     * This method returns a default approximation of the moment of a
     * specified order about 0. This method should be overriden if the moment
     * is known in closed form.
     *
     * @param n the order
     *
     * @return the moment of order n
     */
    public double getMoment(int n) {
        return getMoment(0, n);
    }

    /**
     * This method returns a default approximate mean. This method
     * should be overriden if the mean is known in closed form.
     *
     * @return the mean
     */
    public double getMean() {
        return getMoment(1);
    }

    /**
     * This method returns a default approximate variance. This method
     * should be overriden if the variance is known in closed form.
     *
     * @return the variance
     */
    public double getVariance() {
        return getMoment(getMean(), 2);
    }

    /**
     * This method returns the standard deviation, as the square root
     * of the variance.
     *
     * @return the standard deviation
     */
    public double getSD() {
        return Math.sqrt(getVariance());
    }

    /**
     * This method returns a default approximate cumulative
     * distribution function. This should be overriden if the CDF is known in
     * closed form.
     *
     * @param x a number in the domain of the distribution
     *
     * @return the cumulative probability at x
     */
    public double getCDF(double x) {
        double sum = 0;
        double w;
        double y;

        if (type == DISCRETE) {
            w = 1;
        } else {
            w = domain.getWidth();
        }

        int j = domain.getIndex(x);

        if (j < 0) {
            return 0;
        } else if (j >= domain.getSize()) {
            return 1;
        } else {
            for (int i = 0; i <= j; i++)
                sum = sum + (getDensity(domain.getValue(i)) * w);

            if (type == CONTINUOUS) {
                y = domain.getValue(j) - (0.5 * w);
                sum = sum + (getDensity((x + y) / 2) * (x - y));
            }
        }

        return sum;
    }

    /**
     * This method computes an approximate quantile function. This
     * should be overriden if the quantile function is known in closed form.
     *
     * @param p a probability in (0, 1)
     *
     * @return the quantile of order p
     */
    public double getQuantile(double p) {
        double x;
        double x1;
        double x2;
        double error;
        double q;
        int n;
        int i;

        if (type == DISCRETE) {
            if (p <= 0) {
                return domain.getLowerValue();
            } else if (p >= 1) {
                return domain.getUpperValue();
            } else {
                n = domain.getSize();
                i = 0;
                x = domain.getValue(i);
                q = getDensity(x);

                while ((q < p) & (i < n)) {
                    i++;
                    x = domain.getValue(i);
                    q = q + getDensity(x);
                }

                return x;
            }
        } else {
            if (p <= 0) {
                return domain.getLowerBound();
            } else if (p >= 1) {
                return domain.getUpperBound();
            } else {
                x1 = domain.getLowerBound();
                x2 = domain.getUpperBound();
                x = (x1 + x2) / 2;
                q = getCDF(x);
                error = Math.abs(q - p);
                n = 1;

                while ((error > 0.0001) & (n < 100)) {
                    n++;

                    if (q < p) {
                        x1 = x;
                    } else {
                        x2 = x;
                    }

                    x = (x1 + x2) / 2;
                    q = getCDF(x);
                    error = Math.abs(q - p);
                }

                return x;
            }
        }
    }

    /**
     * This method computes a default simulation of a value from the
     * distribution, as a random quantile. This method should be overridden if
     * a better method of simulation is known.
     *
     * @return a simulated value from the distribution
     */
    public double simulate() {
        return getQuantile(Math.random());
    }

    /**
     * This method computes a default approximate median. This method
     * should be overriden when there is a closed form expression for the
     * median.
     *
     * @return the median
     */
    public double getMedian() {
        return getQuantile(0.5);
    }

    /**
     * This method computes the failure rate function.
     *
     * @param x a number in the domain of the distribution
     *
     * @return the failure rate at x
     */
    public double getFailureRate(double x) {
        return getDensity(x) / (1 - getCDF(x));
    }

    /**
     * This method computes a default approximation to the moment
     * generating function. The moment generating function is the generating
     * function often used for continuous distributions. This method should be
     * overriden if the MGF is known in closed form.
     *
     * @param t a real number
     *
     * @return the moment generating function at t
     */
    public double getMGF(double t) {
        double sum = 0;
        double x;
        double w;

        if (type == DISCRETE) {
            w = 1;
        } else {
            w = domain.getWidth();
        }

        for (int i = 0; i < domain.getSize(); i++) {
            x = domain.getValue(i);
            sum = sum + (Math.exp(t * x) * getDensity(x) * w);
        }

        return sum;
    }

    /**
     * This method computes a default approximation to the probability
     * generating function. The probability generating function is the
     * generating function often used for discrete, distributions on the
     * non-negative integers. This method should be overriden if the PGF is
     * known in closed form.
     *
     * @param t a real number
     *
     * @return the probability generating function at t
     */
    public double getPGF(double t) {
        return getMGF(Math.log(t));
    }

    /**
     * This method returns a string that gives the name of the
     * distribution and the values of the parameters.
     *
     * @return a string giving the name of the distribution and the values of
     *         the parameters
     */
    public String toString() {
        return "Distribution distribution [type = " + type + ", domain = " +
        domain + "]";
    }
}
