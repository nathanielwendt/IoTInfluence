package nathanielwendt.mpc.ut.edu.iotinfluence.eval;

import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;

/**
 * Created by nathanielwendt on 4/11/16.
 */
public class Grid {
    private List<DeviceModel> devices = new ArrayList<DeviceModel>();
    private static int DEF_DIM = 10;
    private final int xDim;
    private final int yDim;

    private List<Double> xBounds = new ArrayList<>();
    private List<Double> yBounds = new ArrayList<>();

    public void addDevice(DeviceModel dev){
        this.devices.add(dev);
    }

    public void addXBoundary(double val){
        this.xBounds.add(val);
    }

    public void addYBoundary(double val){
        this.yBounds.add(val);
    }

    public DeviceModel getNearestEffectiveDevice(Location loc){
        double smallestDist = Double.MAX_VALUE;
        DeviceModel bestDev = null;
        for(DeviceModel dev : devices){
            double currDist = Location.distance(loc, dev.location());
            if(currDist < smallestDist && !wallBlocked(dev, loc)){
                smallestDist = currDist;
                bestDev = dev;
            }
        }
        return bestDev;
    }

    //rather than returning the device, returns a code representing the device's id
    //A single value represents a single possible answer, comma separated values represent multiple possible answers
    public SampleResult getNearestEffectiveIdCode(Location loc){
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

    public static class SampleResult {
        List<String> ids = new ArrayList<>();

        public void add(String id){
            ids.add(id);
        }

        public void clear(){
            ids.clear();
        }

        public boolean idInResult(String id){
            return ids.contains(id);
        }

        @Override public String toString(){
            String res = "";
            String delim = "";
            for(String id : ids){
                res += delim + id;
                delim = "-";
            }
            return res;
        }
    }

    public boolean wallBlocked(DeviceModel dev, Location loc){
        double x1 = dev.location().x();
        double y1 = dev.location().y();
        double x2 = loc.x();
        double y2 = loc.y();

        for(double xBound : xBounds){
            if(splitByBarrier(x1, xBound, x2)){
                return true;
            }
        }

        for(double yBound : yBounds){
            if(splitByBarrier(y1, yBound, y2)){
                return true;
            }
        }

        return false;
    }

    public boolean splitByBarrier(double p, double barrier, double q){
        return (p > barrier && q < barrier) || (p < barrier && q > barrier);
    }

    public Grid(int xDim, int yDim){
        this.xDim = xDim;
        this.yDim = yDim;
    }

    public static Grid newStandard(List<DeviceModel> devices){
        Grid grid = new Grid(DEF_DIM, DEF_DIM);
        for(DeviceModel dev : devices){
            grid.addDevice(dev);
        }
        return grid;
    }

    public static Grid newUpperSingle(List<DeviceModel> devices){
        Grid grid = Grid.newStandard(devices);
        grid.addYBoundary(21);
        return grid;
    }

    public static Grid newUpperT(List<DeviceModel> devices){
        Grid grid = Grid.newStandard(devices);
        grid.addYBoundary(21);
        grid.addXBoundary(12.5);
        return grid;
    }

    public static Grid newX(List<DeviceModel> devices){
        Grid grid = Grid.newStandard(devices);
        grid.addYBoundary(12.5);
        grid.addXBoundary(12.5);
        return grid;
    }



}
