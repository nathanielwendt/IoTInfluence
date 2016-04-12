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



}
