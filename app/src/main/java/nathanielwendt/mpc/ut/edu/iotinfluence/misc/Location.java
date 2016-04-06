package nathanielwendt.mpc.ut.edu.iotinfluence.misc;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class Location {
    private final double x;
    private final double y;

    public Location(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double x(){
        return this.x;
    }

    public double y(){
        return this.y;
    }
    
    public static double distance(Location a, Location b){
        return Math.sqrt( Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2) );
    }
}
