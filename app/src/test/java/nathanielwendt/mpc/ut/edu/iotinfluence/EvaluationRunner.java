package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.Device;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.eval.Grid;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.InitializedCallback;

/**
 * Created by nathanielwendt on 4/11/16.
 */
public class EvaluationRunner {
    public static final int DIM = 25;
    public static final int TRAIN_STEP = 2;
    public static final Location[] TRAINING_LOCS = new Location[144];
    public static final Location[] DEF_GRID_SAMPLES = new Location[(DIM + 1) * (DIM + 1)];
    public Warble warble;

    static {
        int count = 0;
        for(int y = TRAIN_STEP; y < DIM; y+=TRAIN_STEP){
            for(int x = TRAIN_STEP; x < DIM; x+=TRAIN_STEP){
                TRAINING_LOCS[count++] = new Location(x,y);
            }
        }

        count = 0;
        for(int y = 0; y <= DIM; y++){
            for(int x = 0; x <= DIM; x++){
                DEF_GRID_SAMPLES[count++] = new Location(x,y);
            }
        }
    }

    public List<DeviceModel> buildLightsGeneric(){
        List<DeviceModel> candidates = new ArrayList<>();
        Service testService = new TestService();
        LightModel[] lightModels = new LightModel[4];
        lightModels[0] = new LightModel();
        lightModels[0].id = "0";
        lightModels[0].service = testService;
        lightModels[0].location = new Location(0,0);
        lightModels[1] = new LightModel();
        lightModels[1].id = "1";
        lightModels[1].service = testService;
        lightModels[1].location = new Location(0,DIM);
        lightModels[2] = new LightModel();
        lightModels[2].id = "2";
        lightModels[2].service = testService;
        lightModels[2].location = new Location(DIM,DIM);
        lightModels[3] = new LightModel();
        lightModels[3].id = "3";
        lightModels[3].service = testService;
        lightModels[3].location = new Location(DIM,0);
        candidates.addAll(Arrays.asList(lightModels));
        return candidates;
    }

    @Test
    public void basicLocationTest() throws Exception {
        List<DeviceModel> lights = buildLightsGeneric();
        Grid grid = Grid.newUpperT(lights);

        warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(lights));
        warble.initialize();
        while(!warble.initialized()){}

        Location[] resultsTraining = trainWarble(grid, TRAINING_LOCS);
        //resultsTraining = trainWarble(grid, TRAINING_LOCS);

        String[] resultsWarble = evaluateWarble(grid, DEF_GRID_SAMPLES);
        Grid.SampleResult[] resultsGroundTruth = evaluateGroundTruth(grid, DEF_GRID_SAMPLES);

        printResults(resultsGroundTruth, resultsWarble);
        //examineResults(resultsGroundTruth, resultsWarble);
    }

    public void printResults(Grid.SampleResult[] resultsGroundTruth, String[] resultsWarble){
        int correctCount = 0;
        int totalCount = 0;
        for(int i = DIM; i >= 0; i--){
            for(int j = 0; j <= DIM; j++){
                int index = i * (DIM + 1) + j;
                String val;
                if(resultsGroundTruth[index].idInResult(resultsWarble[index])){
                    val = "Y ";
                    correctCount++;
                } else {
                    val = resultsWarble[index] + "," + resultsGroundTruth[index];
                }
                System.out.print(String.format("%1$"+5+ "s", val));
                totalCount++;
            }
            System.out.println();
        }
        System.out.println("% Correct: " + (double) correctCount / (double) totalCount);
    }

    public void examineResults(Grid.SampleResult[] resultsGroundTruth, String[] resultsWarble){
        Assert.assertEquals(resultsGroundTruth.length, resultsWarble.length);

        for(int i = 0; i < resultsWarble.length; i++){
            Assert.assertTrue(resultsGroundTruth[i].idInResult(resultsWarble[i]));
        }

        Assert.assertArrayEquals(resultsGroundTruth, resultsWarble);
    }

    //returns array of Locations that required Help! actions
    //duplicates indicate successive Help! actions on a given location
    public Location[] trainWarble(Grid grid, Location[] samples){
        Location[] errors = new Location[samples.length * 4]; // will go out of bounds on multiple test fails
        int count = 0;
        for(Location sample : samples){
//            if(!trainSample(grid, sample)){
//                errors[count++] = sample;
//            }

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
        try {
            warbleLight.on();
        } catch (DeviceUnavailableException e) {
            e.printStackTrace();
        }
        //LightModel groundTruthLight = (LightModel) grid.getNearestEffectiveDevice(sample);
        Grid.SampleResult sampleResult = grid.getNearestEffectiveIdCode(sample);

        if(!sampleResult.idInResult(warbleLight.deviceId())){
            warble.help(warbleLight.requestId(), warbleLight.deviceId());
            return false;
        }
        return true;
    }

    public Grid.SampleResult[] evaluateGroundTruth(Grid grid, Location[] samples){
        Grid.SampleResult[] res = new Grid.SampleResult[samples.length];
        int count = 0;
        for(Location sample : samples){
            //res[count++] = (String) grid.getNearestEffectiveDevice(sample).id;
            res[count++] = grid.getNearestEffectiveIdCode(sample);
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

            Light warbleLight = warble.retrieve(Light.class, reqs, 1).get(0);
            try {
                warbleLight.on();
            } catch (DeviceUnavailableException e) {
                e.printStackTrace();
            }
            res[count++] = warbleLight.deviceId();
        }
        return res;
    }

    public static class TestService implements Service {

        @Override
        public Light light(final String deviceId, String requestId) {
            return new Light(deviceId, requestId){
                @Override
                public void brightness(int level) throws DeviceUnavailableException {
                    super.brightness(level);
                }

                @Override
                public void off() throws DeviceUnavailableException {
                    super.off();
                }

                @Override
                public void on() throws DeviceUnavailableException {
                    super.on();
                }
            };
        }

        @Override
        public void fetchDevices(FetchDevicesCallback callback) {

        }
    }

    public static class TestDevManager implements DeviceManager {
        private List<DeviceModel> devices;
        private boolean initialized;

        public TestDevManager(List<DeviceModel> devices){
            this.devices = devices;
        }

        @Override
        public void scan() {
            initialized = true;
        }

        @Override
        public void scan(InitializedCallback callback) {
            initialized = true;
            callback.onInit();
        }

        @Override
        public boolean isInitialized() {
            return initialized;
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
