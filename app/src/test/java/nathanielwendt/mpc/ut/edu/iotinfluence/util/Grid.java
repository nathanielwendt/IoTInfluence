package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 4/20/16.
 */
public abstract class Grid {
    protected List<DeviceModel> devices;
    protected List<Geometry.LineSegment> partitions;
    protected double width;
    protected double height;
    protected double step;

    public SampleResult[] train(TrainingAction trainingAction, Location[] locs){
        SampleResult[] res = new SampleResult[locs.length];
        int count = 0;
        for(Location loc: locs){
            //String result = trainingAction.act(loc.x(), loc.y());
            res[count++] = new SampleResult("");
        }
        return res;
    }

    public void setStep(double step){
        this.step = step;
    }

    private boolean wallBlocked(DeviceModel dev, Location loc) {
        Geometry.LineSegment refToDev = new Geometry.LineSegment(dev.location().x(), dev.location().y(),
                loc.x(), loc.y());

        for(Geometry.LineSegment partition: partitions){
            if(Geometry.doLinesIntersect(refToDev, partition)){
                return true;
            }
        }
        return false;
    }

    private SampleResult actGroundTruth(double x, double y){
        Location loc = new Location(x,y);
        double smallestDist = Double.MAX_VALUE;
        SampleResult res = new SampleResult();
        for(DeviceModel dev : devices){
            double currDist = Location.distance(loc, dev.location());
            if(currDist == smallestDist && !wallBlocked(dev, loc)){
                res.add(dev.id);
            } else if(currDist < smallestDist && !wallBlocked(dev, loc)){
                smallestDist = currDist;
                res.clear();
                res.add(dev.id);
            }
        }
        return res;
    }

    public int getResultSize(double dimLength, double step){
        double result = 0;
        if(dimLength % step == 0){
            result = (dimLength / step) - 1;
        } else {
            result = (dimLength / step);
        }
        return (int) result;
    }
    public List<DeviceModel> getLights(){
        return devices;
    }

    public List<SampleResult> train(Location[] locations, boolean retry, TrainingAction trainAction){
        List<SampleResult> results = new ArrayList<>();
        for(Location location : locations){
            SampleResult actualResult = trainAction.act(location.x(), location.y());
            SampleResult expectedResult = actGroundTruth(location.x(), location.y());

            if(retry){
                while(!actualResult.equals(expectedResult)){
                    results.add(trainAction.onError(expectedResult, actualResult));
                }
            } else {
                if(!actualResult.equals(expectedResult)){
                    results.add(trainAction.onError(expectedResult, actualResult));
                }
            }
        }
        return results;
    }

    public SampleResult[][] evaluate(EvaluationAction evalAction){
        int xDim = getResultSize(width, step);
        int yDim = getResultSize(height, step);

        SampleResult[][] res = new SampleResult[xDim][yDim];

        int xCount = 0;
        int yCount = 0;
        for(double y = 0; y <= height; y+=step){
            for(double x = 0; x <= width; x+=step){
                SampleResult actualResult = evalAction.act(x,y);
                SampleResult expectedResult = actGroundTruth(x, y);

                if(actualResult.equals(expectedResult)){
                    res[xCount][yCount] = evalAction.onSuccess(actualResult);
                } else {
                    res[xCount][yCount] = evalAction.onError(expectedResult, actualResult);
                }
                xCount++;
            }
            yCount++;
        }
        return res;
    }
}
