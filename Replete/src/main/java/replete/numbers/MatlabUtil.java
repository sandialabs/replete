package replete.numbers;

/**
 * @author Derek Trumbo
 */

public class MatlabUtil {

    public static double corr2(double[] a, double[] b) {
        if(a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have same length.");
        }

        // Calculate mean of each vector:
        double aMean = 0.0;  for(double i : a) {
            aMean += i;
        }  aMean /= a.length;
        double bMean = 0.0;  for(double i : b) {
            bMean += i;
        }  bMean /= b.length;

        // Calculate numerator.
        double numerator = 0.0;
        for(int n = 0; n < a.length; n++) {
            numerator += (a[n] - aMean) * (b[n] - bMean);
        }

        // Calculate sum with first vector.
        double denomSumA = 0.0;
        for(int n = 0; n < a.length; n++) {
            double diff = a[n] - aMean;
            denomSumA += diff * diff;
        }

        // Calculate sum with second vector.
        double denomSumB = 0.0;
        for(int n = 0; n < b.length; n++) {
            double diff = b[n] - bMean;
            denomSumB += diff * diff;
        }

        // Calculate denominator.
        double denominator = Math.sqrt(denomSumA * denomSumB);

        // Return quotient.
        return numerator / denominator;
    }

    public static double graythresh(double[] a) {

        //I = im2uint8(I(:));
        int[] I = new int[a.length];
        for(int s = 0; s < a.length; s++) {
            I[s] = (int) Math.round(a[s] * 255);
        }
        // num_bins = 256;
        //counts = imhist(I,num_bins);
        int numBins = 256;
        int[] counts = new int[numBins];
        for(int s = 0; s < I.length; s++) {
            counts[I[s]]++;
        }
        //p = counts / sum(counts);
        int sumCounts = 0;
        for(int c = 0; c < counts.length; c++) {
            sumCounts += counts[c];
        }
        double[] p = new double[counts.length];
        for(int x = 0; x < counts.length; x++) {
            p[x] = counts[x] / (double)sumCounts;
        }
        double[] omega = new double[p.length];
        omega[0] = p[0];
        for(int x = 1; x < p.length; x++) {
            omega[x] = p[x] + omega[x - 1];
        }
        double[] pds = new double[p.length];
        for(int x = 0; x < pds.length; x++) {
            pds[x] = p[x] * (x+1);
        }
        double[] mu = new double[pds.length];
        mu[0] = pds[0];
        for(int x = 1; x < mu.length; x++) {
            mu[x] = pds[x] + mu[x - 1];
        }
        double mu_t = mu[mu.length - 1];
        double[] num = new double[pds.length];
        for(int x = 0; x < num.length; x++) {
            num[x] = Math.pow(mu_t * omega[x] - mu[x], 2);
        }
        double[] denom = new double[pds.length];
        for(int x = 0; x < num.length; x++) {
            denom[x] = omega[x] * (1 - omega[x]);
        }
        double xx = 0;
        for(int x = 0; x < num.length; x++) {
            xx+=num[x];
        }
        xx = 0;
        for(int x = 0; x < denom.length; x++) {
            xx+=denom[x];
        }
        double[] quo = new double[num.length];
        for(int x = 0; x < quo.length; x++) {
            quo[x] = num[x] / denom[x];
        }
        double maxval = -100000;
        for(int x = 0; x < quo.length; x++) {
            if(quo[x] > maxval) {
                maxval = quo[x];
            }
        }

        if(!Double.isInfinite(maxval) && !Double.isNaN(maxval)) {
            int idxSum = 0;
            int idxCount = 0;
            for(int x = 0; x < quo.length; x++) {
                if(quo[x] == maxval) {
                    idxSum += (x + 1);
                    idxCount++;
                }
            }
            double idx = (idxSum / (double) idxCount);
            return (idx - 1) / (numBins - 1);

        }

        return 0.0;
    }

    public static double[] im2bw(double[] a, double thresh) {
        double[] res = new double[a.length];
        for(int x = 0; x < a.length; x++) {
            if(a[x] <= thresh) {
                res[x] = 0.0;
            } else {
                res[x] = 1.0;
            }
        }
        return res;
    }
}
