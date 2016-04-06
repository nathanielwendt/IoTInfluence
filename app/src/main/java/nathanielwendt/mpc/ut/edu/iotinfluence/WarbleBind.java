package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.content.Context;
import android.location.Location;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceCommand;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;

/**
 * Created by nathanielwendt on 3/21/16.
 */
public class WarbleBind {
    private final int N;
    private final List<DeviceReq> reqs;
    private final DeviceCommand command;
    private final Observable obs;
    private final Fluidity fluidity;
    private final Warble warble;
    private final Context ctx;

    private WarbleBind(Builder builder){
        if(builder.reqs == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        if(builder.command == null){
            throw new IllegalArgumentException("No Device Requirements included in Warble Request");
        }

        if(builder.obs == null){
            throw new IllegalArgumentException("No Observable included in Warble Request");
        }

        if(builder.ctx == null){
            throw new IllegalArgumentException("No context included in Warble Request");
        }

        this.ctx = builder.ctx;
        this.warble = new Warble(this.ctx);
        this.fluidity = builder.fluidity;
        this.N = builder.N;
        this.reqs = builder.reqs;
        this.command = builder.command;
        this.obs = builder.obs;
        this.obs.addObserver(new Observer(){
            @Override
            public void update(Observable observable, Object data) {
                Location loc = (Location) data;
                //act
            }
        });
    }

    public void unbind(){
        //cleanup any histories
        //update database
        //object is done
    }

    public enum Fluidity {
        FIXED(0), ACTIVE(10000), HYPER(1000);

        //ms
        private int scanInterval;
        Fluidity(int scanInterval){
            this.scanInterval = scanInterval;
        }
    }

    public static class Builder {
        private int N = 1;
        private List<DeviceReq> reqs;
        private DeviceCommand command;
        private Observable obs;
        private Fluidity fluidity;
        private Context ctx;

        public Builder(){}

        public Builder num(int num){
            this.N = num;
            return this;
        }

        public Builder reqs(List<DeviceReq> reqs){
            this.reqs = reqs;
            return this;
        }

        public Builder command(DeviceCommand command){
            this.command = command;
            return this;
        }

        public Builder fluidity(Fluidity fluidity){
            this.fluidity = fluidity;
            return this;
        }

        public Builder ctx(Context ctx){
            this.ctx = ctx;
            return this;
        }

        public WarbleBind bind(Observable obs){
            this.obs = obs;
            return new WarbleBind(this);
        }
    }


}

