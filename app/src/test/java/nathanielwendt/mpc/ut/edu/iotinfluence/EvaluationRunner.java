package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.eval.Grid;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;

/**
 * Created by nathanielwendt on 4/11/16.
 */
public class EvaluationRunner {
    public static final Location[] TRAINING_LOCS = new Location[16];
    public static final Location[] DEF_GRID_SAMPLES = new Location[100];
    public Warble warble;

    static {
        int count = 0;
        for(int i = 2; i < 10; i+=2){
            for(int j = 2; j < 10; j+=2){
                TRAINING_LOCS[count++] = new Location(i,j);
            }
        }

        count = 0;
        for(int i = 0; i <= 10; i++){
            for(int j = 0; j <= 10; j++){
                DEF_GRID_SAMPLES[count++] = new Location(i,j);
            }
        }
    }

    public List<DeviceModel> buildLightsGeneric(){
        List<DeviceModel> candidates = new ArrayList<>();
        LightModel[] lightModels = new LightModel[4];
        lightModels[0] = new LightModel();
        lightModels[0].id = "0";
        lightModels[0].location = new Location(0,0);
        lightModels[1] = new LightModel();
        lightModels[1].id = "1";
        lightModels[1].location = new Location(0,10);
        lightModels[2] = new LightModel();
        lightModels[2].id = "2";
        lightModels[2].location = new Location(10,10);
        lightModels[3] = new LightModel();
        lightModels[3].id = "3";
        lightModels[3].location = new Location(10,0);
        candidates.addAll(Arrays.asList(lightModels));
        return candidates;
    }

    @Test
    public void basicLocationTest() throws Exception {
        List<DeviceModel> lights = buildLightsGeneric();
        Grid grid = Grid.newStandard(lights);

        warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(lights));

        trainWarble(grid, TRAINING_LOCS);
        String[] resultsWarble = evaluateWarble(grid, DEF_GRID_SAMPLES);
        String[] resultsGroundTruth = evaluateGroundTruth(grid, DEF_GRID_SAMPLES);
        examineResults(resultsGroundTruth, resultsWarble);
    }

    public void examineResults(String[] resultsGroundTruth, String[] resultsWarble){
        Assert.assertEquals(resultsGroundTruth.length, resultsWarble.length);
        Assert.assertArrayEquals(resultsGroundTruth, resultsWarble);
    }

    //returns array of Locations that required Help! actions
    //duplicates indicate successive Help! actions on a given location
    public Location[] trainWarble(Grid grid, Location[] samples){
        Location[] errors = new Location[samples.length * 3]; // will go out of bounds on multimple test fails
        int count = 0;
        for(Location sample : samples){
            while(!trainSample(grid, sample)){
                errors[count++] = sample;
            }
        }
        return errors;
    }

    public boolean trainSample(Grid grid, Location sample){
        SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                SpatialReq.Influence.AWARE, sample);
        List<DeviceReq> reqs = new ArrayList<>();
        reqs.add(spatialReq);
        List<Light> lights = warble.retrieve(Light.class, reqs, 1);
        Light warbleLight = lights.get(0);
        LightModel groundTruthLight = (LightModel) grid.getNearestEffectiveDevice(sample);

        if(!warbleLight.deviceId().equals(groundTruthLight.id)){
            warble.help(warbleLight.requestId(), warbleLight.deviceId());
            return false;
        }
        return true;
    }

    public String[] evaluateGroundTruth(Grid grid, Location[] samples){
        String[] res = new String[samples.length];
        int count = 0;
        for(Location sample : samples){
            res[count++] = (String) grid.getNearestEffectiveDevice(sample).id;
        }
        return res;
    }

    public String[] evaluateWarble(Grid grid, Location[] samples){
        String[] res = new String[samples.length];
        int count = 0;
        for(Location sample : samples){
            SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                    SpatialReq.Influence.AWARE, sample);
            List<DeviceReq> reqs = new ArrayList<>();
            reqs.add(spatialReq);
            res[count++] = warble.retrieve(Light.class, reqs, 1).get(0).deviceId();
        }
        return res;
    }

    public static class TestDevManager implements DeviceManager {
        private List<DeviceModel> devices;

        public TestDevManager(List<DeviceModel> devices){
            this.devices = devices;
        }

        @Override
        public void scan() {}

        @Override
        public void scan(InitializedCallback callback) {}

        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        public <D extends Device> List<DeviceModel> fetchDevices(Class<D> clazz) {
            return this.devices;
        }

        @Override
        public List<DeviceModel> fetchDevices() {
            return this.devices;
        }
    }
}
