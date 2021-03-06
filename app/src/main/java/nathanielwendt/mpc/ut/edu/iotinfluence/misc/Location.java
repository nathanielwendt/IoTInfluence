package nathanielwendt.mpc.ut.edu.iotinfluence.misc;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.DBStorable;

/**
 * Created by nathanielwendt on 4/5/16.
 */
public class Location implements DBStorable {
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

    //This function accelerates calculations by grouping them with square root of distance
    //Power indicates power of distance equation to raise to.
    //Default distance function uses power of 1/2 (square root)
    public static double distance(Location a, Location b, double power){
        return Math.pow( Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2), power );
    }

    @Override public String toString(){
        return this.x() + "," + this.y();
    }

    @Override
    public String toSchema() {
        return this.toString();
    }

    public static Location fromSchema(String schema) {
        String[] portions = schema.split(",");
        double x = Double.valueOf(portions[0]);
        double y = Double.valueOf(portions[1]);
        return new Location(x,y);
    }
}
