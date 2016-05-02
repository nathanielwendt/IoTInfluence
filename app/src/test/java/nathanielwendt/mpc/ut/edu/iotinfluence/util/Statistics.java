package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import java.util.Arrays;

public class Statistics
{
    double[] data;
    int size;

    public Statistics(double[] data)
    {
        this.data = data;
        size = data.length;
    }

    public double mean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    public double variance()
    {
        double mean = mean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    public double min(){
        double min = Double.MAX_VALUE;
        for(double item : data){
            if(item < min){
                min = item;
            }
        }
        return min;
    }

    public double max(){
        double max = Double.MIN_VALUE;
        for(double item : data){
            if(item > max){
                max = item;
            }
        }
        return max;
    }

    public double stdDev()
    {
        return Math.sqrt(variance());
    }

    public double median()
    {
        Arrays.sort(data);

        if (data.length % 2 == 0)
        {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        }
        else
        {
            return data[data.length / 2];
        }
    }
}
