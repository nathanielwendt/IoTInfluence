package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.Action;
import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 4/20/16.
 */
public abstract class Grid {
    protected List<DeviceModel> devices = new ArrayList<>();
    protected List<Geometry.LineSegment> partitions = new ArrayList<>();
    protected ZoneManager zoneManager = new ZoneManager();
    protected double width;
    protected double height;
    protected double step;

    public static double HOT_WEIGHT = .65;
    public static double MED_WEIGHT = .22;
    public static double COLD_WEIGHT = .18;


    public static class ZoneManager {
        private Map<Zone.Weight, List<Zone>> zones = new HashMap<>();
        private Map<Zone.Weight, Double> adjustedProbs = new HashMap<>();

        public ZoneManager(){}

        public void add(Zone zone, Zone.Weight weight){
            List<Zone> currZones = zones.get(weight);
            if(currZones == null){
                List<Zone> newZones = new ArrayList<>();
                newZones.add(zone);
                zones.put(weight, newZones);
            } else {
                currZones.add(zone);
            }
            computeAdjustedProbs();
        }

        public double getAreaOfWeight(Zone.Weight weight){
            List<Zone> weightZones = zones.get(weight);
            if(weightZones == null){ return 0; }
            double area = 0;
            for(Zone zone: weightZones){
                area += zone.getArea();
            }
            return area;
        }

        public Location getRandomLoc(Zone.Weight weight){
            List<Zone> candidates = zones.get(weight);
            int zIndex = (int) (Math.random() * candidates.size());
            Zone zone = candidates.get(zIndex);
            return zone.getRandomSample();
        }

        public Zone.Weight getWeightAtLoc(Location loc){
            for(Map.Entry<Zone.Weight, List<Zone>> entry : zones.entrySet()){
                for(Zone zone : entry.getValue()){
                    if(zone.inZone(loc)){
                        return entry.getKey();
                    }
                }
            }
            return null;
        }

        public double[] getZoneAdjustedProbs(){
            double[] ret = new double[Zone.Weight.values().length];
            int i = 0;
            for(Zone.Weight weight : Zone.Weight.values()){
                System.out.println(weight);
                ret[i++] = adjustedProbs.get(weight);
            }
            return ret;
        }

        private void computeAdjustedProbs(){
            double totalArea = 0;
            for(Zone.Weight weight : Zone.Weight.values()){
                totalArea += getAreaOfWeight(weight);
            }
            for(Zone.Weight weight : Zone.Weight.values()){
                double area = getAreaOfWeight(weight);
                //double adjustedProb = (area / totalArea) * weight.prob;
                double adjustedProb = (weight.prob) / area;
                adjustedProbs.put(weight, adjustedProb);
                //System.out.println(weight.getProb() + " --> " + adjustedProb);
            }
            //System.out.println("-----------");
        }

        public double getAdjustedProbAtLoc(Location loc){
            Zone.Weight weight = getWeightAtLoc(loc);
            if(weight == null){ throw new RuntimeException(loc.toString()); }
            //return weight.getProb();
            return adjustedProbs.get(weight);
        }
    }

    public static class Zone {
        public double xMin,xMax,yMin,yMax;

        public Zone(double xMin, double xMax, double yMin, double yMax){
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        public double getArea(){
            return (this.xMax - this.xMin) * (this.yMax - this.yMin);
        }

        public boolean inZone(Location loc){
            return loc.x() >= this.xMin && loc.x() <= this.xMax && loc.y() >= this.yMin && loc.y() <= this.yMax;
        }

        public Location getRandomSample(){
            double xDim = xMax - xMin + 1;
            double yDim = yMax - yMin + 1;

            int xIndex = ((int) (Math.random() * xDim));
            int yIndex = ((int) (Math.random() * yDim));

            return new Location(xMin + xIndex, yMin + yIndex);
        }


        public enum Weight {
            HOT(Grid.HOT_WEIGHT), MED(Grid.MED_WEIGHT), COLD(Grid.COLD_WEIGHT);

            private double prob;
            Weight(double prob){
                this.prob = prob;
            }

            public double getProb(){
                return this.prob;
            }
        }
    }

    public List<DeviceModel> getDevices(){
        return devices;
    }

    public void setStep(double step){
        this.step = step;
    }

    public double[] getZoneAdjustedWeights(){
        return zoneManager.getZoneAdjustedProbs();
    }

    public SampleResult[][] getZoneHeatmap(boolean rotate){
        int xDim = getResultSize(width, step);
        int yDim = getResultSize(height, step);

        SampleResult[][] res;
        if(rotate){
            res = new SampleResult[yDim][xDim];
        } else {
            res = new SampleResult[xDim][yDim];
        }

        int xCount = 0;
        int yCount = 0;
        //for(double y = height - step; y >= 0; y-=step){
        for(double y = 0; y < height; y+=step){
            for(double x = 0; x < width; x+=step){
                double adjProb = zoneManager.getAdjustedProbAtLoc(new Location(x, y));
                SampleResult result = new SampleResult(String.valueOf(adjProb));

                if(rotate){
                    res[yCount][xCount] = result;
                } else {
                    res[xCount][yCount] = result;
                }

                xCount++;
            }
            xCount = 0;
            yCount++;
        }
        return res;
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

    private boolean onPartition(Location loc){
        if(loc == null){ return false; }
        Geometry.LineSegment segment = new Geometry.LineSegment(loc.x(), loc.y(), loc.x(), loc.y());
        for(Geometry.LineSegment partition: partitions){
            if(Geometry.doLinesIntersect(segment, partition)){
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

    public List<DeviceModel> getLights(){
        return devices;
    }

    public Location[] getProbLocs(int numSamples){
        int numWeights = Zone.Weight.values().length;
        int[] indexes = new int[numWeights];
        double[] probs = new double[numWeights];

        for(int i = 0; i < numWeights; i++){
            indexes[i] = i;
            probs[i] = Zone.Weight.values()[i].getProb();
        }

        //int[] numsToGenerate           = new int[]    { 0,1,2,3,4 };
       // double[] discreteProbabilities = new double[] { 0.1, 0.25, 0.3, 0.25, 0.1 };

        EnumeratedIntegerDistribution distribution =
                new EnumeratedIntegerDistribution(indexes, probs);

        int[] samples = distribution.sample(numSamples);
        Location[] res = new Location[samples.length];
        for(int i = 0; i < samples.length; i++){
            int index = samples[i];
            Zone.Weight weight = Zone.Weight.values()[index];

            Location currLoc;
            do {
                currLoc = zoneManager.getRandomLoc(weight);
            } while(onPartition(currLoc));

            Location adjLoc = new Location(currLoc.x() / step, currLoc.y() / step);
            res[i] = adjLoc;
        }
        return res;
    }

    public Location[] getAllLocs(){
        int xDim = getResultSize(width, step);
        int yDim = getResultSize(height, step);

        Location[] res = new Location[xDim * yDim];
        int count = 0;
        for(double x = 0; x < width; x+=step){
            for(double y = 0; y < height; y+=step){
                res[count++] = new Location(x,y);
            }
        }

//        for(int x = 0; x < xDim; x+=step){
//            for(int y = 0; y < yDim; y+=step){
//                res[(x * yDim) + y] = new Location(x,y);
//            }
//        }
        return res;
    }

    public void mockTrainAllLocs(){
        Location[] locations = getAllLocs();
        int count = 0;
        for(Location location: locations){
            SampleResult expectedResult = actGroundTruth(location.x(), location.y());
            String deviceId = expectedResult.getSingleResult();
            Location devLocation = null;
            for(DeviceModel device : devices){
                if(device.id.equals(deviceId)){
                    devLocation = device.location;
                }
            }

            if(deviceId.equals("")){ continue; }
            Action action = Action.newDefault(deviceId, location,
                    devLocation, true);
            action.type = Light.LightAction.ON;
            LocalActionDB.insert(String.valueOf(count++), deviceId, action);
        }
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

    public int getResultSize(double dimLength, double step){
        double result = 0;
        if(dimLength % step == 0){
            result = (dimLength / step);
        } else {
            result = (dimLength / step) + 1;
        }
        return (int) result;
    }

    public static final String DEVICE_DELIM = "[";

    public void overlayDevices(SampleResult[][] results){
        for(DeviceModel dev: devices){
            int x = (int) (dev.location().x() / step);
            int y = (int) (dev.location().y() / step);
            System.out.println(x + " , " + y);
            results[x][y] = new SampleResult("[" + dev.id + "]");
        }
    }

    public void overlayLocations(SampleResult[][] results, Location[] locs, SampleResult marker){
        for(Location loc : locs){
            results[(int) loc.x()][(int) loc.y()] = marker;
        }
    }

    public void overlayPartitions(SampleResult[][] results){
        for(Geometry.LineSegment partition : partitions){
            Geometry.Point[] points = partition.getPointsOnSegment();
            for(Geometry.Point point : points){
                int xIndex = (int) (point.x / step);
                int yIndex = (int) (point.y / step);
                try {
                    //results[xIndex][yIndex].add("*");
                    results[xIndex][yIndex] = new SampleResult("*");
                } catch(ArrayIndexOutOfBoundsException e){
                    //partition bounds are allowed to be out of index
                }
            }
        }
    }

    public SampleResult[][] evaluate(EvaluationAction evalAction){
        int xDim = getResultSize(width, step);
        int yDim = getResultSize(height, step);

        SampleResult[][] res = new SampleResult[xDim][yDim];

        int xCount = 0;
        int yCount = 0;
        for(double y = 0; y < height; y+=step){
            for(double x = 0; x < width; x+=step){
                SampleResult actualResult = evalAction.act(x,y);
                SampleResult expectedResult = actGroundTruth(x, y);

                if(actualResult.equals(expectedResult)){
                    res[xCount][yCount] = evalAction.onSuccess(actualResult);
                } else {
                    res[xCount][yCount] = evalAction.onError(expectedResult, actualResult);
                }
                xCount++;
            }
            xCount = 0;
            yCount++;
        }
        return res;
    }
}
