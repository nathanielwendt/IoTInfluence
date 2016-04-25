package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import java.util.ArrayList;
import java.util.List;

public class SampleResult {
    List<String> ids = new ArrayList<>();

    public SampleResult(){}

    public boolean isEmpty(){
        return ids.isEmpty();
    }

    public SampleResult(String id){
        ids.add(id);
    }

    public void add(String id){
        ids.add(id);
    }

    public void clear(){
        ids.clear();
    }

    public boolean idInResult(String id){
        return ids.contains(id);
    }

    public double toDouble(){
        return Double.valueOf(ids.get(0));
    }

    public String getSingleResult(){
        return ids.get(0);
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

    public static double[][] toDoubleArr(SampleResult[][] data){
        if(data.length == 0){ return new double[0][0]; }

        double[][] res = new double[data.length][data[0].length];

        for(int x = 0; x < data.length; x++){
            for(int y = 0; y < data[0].length; y++){
                res[x][y] = data[x][y].toDouble();
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleResult that = (SampleResult) o;

        for(String id : ids){
            if(that.idInResult(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ids != null ? ids.hashCode() : 0;
    }
}
