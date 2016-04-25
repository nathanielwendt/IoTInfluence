package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;

import org.junit.Test;
import org.tc33.jheatchart.HeatChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.DeviceReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.SpatialReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.misc.Location;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.service.Service;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.EvaluationAction;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Geometry;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Grid;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.SampleResult;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.TestDevManager;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.TestService;
import nathanielwendt.mpc.ut.edu.iotinfluence.util.TrainingAction;

/**
 * Created by nathanielwendt on 4/20/16.
 */
public class AdvancedEvaluationRunner {

    public int errorCount = 0;
    public int evalCount = 0;

    public static class StudioGrid extends Grid {
        public StudioGrid(double step) {
            this.step = step;
            this.width = 20;
            this.height = 14;

            Location[] lightLocs = new Location[]{new Location(2, 2), new Location(5, 11),
                    new Location(12, 11), new Location(15, 13),
                    new Location(12, 5), new Location(19, 5)};

            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            partitions.add(new Geometry.LineSegment(10, 8, 10, 14));
            partitions.add(new Geometry.LineSegment(10, 8, 20, 8));
            partitions.add(new Geometry.LineSegment(13, 8, 13, 14));


            //Don't include upper bounds in zones (e.g. width=20, upperbound should be 19, not 20)
            //used decimal values to make the heatmap look better, should affect probabilities negligibly
            zoneManager.add(new Zone(3,7,3,11), Zone.Weight.HOT); //living main
            zoneManager.add(new Zone(7.001,17,3,5), Zone.Weight.HOT); //kitchen main
            zoneManager.add(new Zone(13,19,8,13), Zone.Weight.MED); //bathroom
            zoneManager.add(new Zone(10,13,8,13), Zone.Weight.MED); //closet
            zoneManager.add(new Zone(0,2.99,0,13), Zone.Weight.COLD);
            zoneManager.add(new Zone(0,19,0,2.99), Zone.Weight.COLD);
            zoneManager.add(new Zone(17.001,19,2.001,7.001), Zone.Weight.COLD);
            zoneManager.add(new Zone(7.001,17.99,5.001,7.99), Zone.Weight.COLD);
            zoneManager.add(new Zone(3,9.99,11.001,13), Zone.Weight.COLD);
            zoneManager.add(new Zone(8,9,8,11), Zone.Weight.COLD);
        }
    }

    public static class StudioGrid2 extends Grid {
        public StudioGrid2(double step) {
            this.step = step;
            this.width = 14;
            this.height = 20;

            Location[] lightLocs = new Location[]{new Location(1, 4), new Location(4, 14),
                    new Location(10, 14), new Location(10, 5),
                    new Location(11, 10)};

            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            partitions.add(new Geometry.LineSegment(0, 10, 5, 10));
            partitions.add(new Geometry.LineSegment(5, 0, 5, 10));
            partitions.add(new Geometry.LineSegment(0, 13, 5, 13));
            partitions.add(new Geometry.LineSegment(5, 10, 5, 13));
            partitions.add(new Geometry.LineSegment(9, 10, 9, 15));
            partitions.add(new Geometry.LineSegment(9, 13, 14, 13));
            partitions.add(new Geometry.LineSegment(9, 15, 14, 15));
        }
    }

    public Light lastWarbleLight;


    @Test
    public void testTrainingGeneration(){
        Grid studioGrid = new StudioGrid(1);
        final Warble warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(studioGrid.getDevices()));
        warble.initialize();
        while(!warble.initialized()){}

        Location[] trainLocs = studioGrid.getProbLocs(25);

        SampleResult[][] evalResults = studioGrid.evaluate(new EvaluationAction() {
            @Override
            public SampleResult act(double x, double y) {
                return new SampleResult("");
            }

            @Override
            public SampleResult onError(SampleResult expected, SampleResult actual){
                if(expected.isEmpty()){
                    return new SampleResult();
                } else {
                    return new SampleResult("Y");
                }
            }

            @Override
            public SampleResult onSuccess(SampleResult actual){
                return new SampleResult("Y");
            }
        });

        studioGrid.overlayDevices(evalResults);
        studioGrid.overlayLocations(evalResults, trainLocs, new SampleResult("#"));
        printGrid(evalResults);

        SampleResult[][] heatmapRes = studioGrid.getZoneHeatmap();
        HeatChart map = new HeatChart(SampleResult.toDoubleArr(heatmapRes));

        // Step 2: Customise the chart.
        map.setTitle("Saweeet");
        map.setXAxisLabel("X Axis");
        map.setYAxisLabel("Y Axis");


        // Step 3: Output the chart to a file.
        try {
            map.saveToFile(new File("java-heat-chart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printGrid(SampleResult[][] evalResults){
        int yDim = evalResults[0].length;
        int xDim = evalResults.length;

        for(int y = yDim - 1; y >= 0; y--){
            for(int x = 0; x < xDim; x++){
                SampleResult res = evalResults[x][y];
                System.out.print(String.format("%1$5s", res.toString()));
            }
            System.out.println();
        }
    }

    @Test
    public void testBasic(){

        Grid studioGrid = new StudioGrid(0.5);
        final Warble warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(studioGrid.getDevices()));
        warble.initialize();
        while(!warble.initialized()){}


        SampleResult[][] evalResults = evaluateWarble(studioGrid, warble);
        System.out.println(1 - ((double) errorCount / (double) evalCount));

        //Location[] trainLocs = studioGrid.getProbLocs(30);
        Location[] trainLocs = studioGrid.getAllLocs();
        System.out.println(trainLocs.length);

        List<SampleResult> trainErrors = studioGrid.train(trainLocs, false, new TrainingAction() {
            @Override
            public SampleResult act(double x, double y) {
                SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                        SpatialReq.Influence.AWARE, new Location(x, y));
                List<DeviceReq> reqs = new ArrayList<>();
                reqs.add(spatialReq);
                List<Light> lights = warble.retrieve(Light.class, reqs, 1);
                lastWarbleLight = lights.get(0);
                try {
                    lastWarbleLight.on();
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
                return new SampleResult(lastWarbleLight.deviceId());
            }

            @Override
            public SampleResult onError(SampleResult expected, SampleResult actual) {
                warble.help(lastWarbleLight.requestId(), lastWarbleLight.deviceId());
                return new SampleResult("0");
            }
        });

        evalResults = evaluateWarble(studioGrid, warble);
        System.out.println(  1 - ((double) errorCount / (double) evalCount) );

        studioGrid.overlayDevices(evalResults);
        //studioGrid.overlayLocations(evalResults, trainLocs, new SampleResult("#"));

        int yDim = evalResults[0].length;
        int xDim = evalResults.length;

        for(int y = yDim - 1; y >= 0; y--){
            for(int x = 0; x < xDim; x++){
                SampleResult res = evalResults[x][y];
                System.out.print(String.format("%1$5s", res.toString()));
            }
            System.out.println();
        }

//        // Step 1: Create our heat map chart using our data.
//        HeatChart map = new HeatChart(SampleResult.toDoubleArr(evalResults));
//
//        // Step 2: Customise the chart.
//        map.setTitle("Saweeet");
//        map.setXAxisLabel("X Axis");
//        map.setYAxisLabel("Y Axis");
//
//
//        // Step 3: Output the chart to a file.
//        try {
//            map.saveToFile(new File("java-heat-chart.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private SampleResult[][] evaluateWarble(Grid grid, final Warble warble){
        evalCount = 0;
        errorCount = 0;
        SampleResult[][] evalResults = grid.evaluate(new EvaluationAction() {
            @Override
            public SampleResult act(double x, double y) {
                SpatialReq spatialReq = new SpatialReq(SpatialReq.Bound.CLOSEST,
                        SpatialReq.Influence.AWARE, new Location(x,y));
                List<DeviceReq> reqs = new ArrayList<>();
                reqs.add(spatialReq);
                List<Light> lights = warble.retrieve(Light.class, reqs, 1);
                Light warbleLight = lights.get(0);
                try {
                    warbleLight.off();
                    evalCount++;
                } catch (DeviceUnavailableException e) {
                    e.printStackTrace();
                }
                return new SampleResult(warbleLight.deviceId());
            }

            @Override
            public SampleResult onError(SampleResult expected, SampleResult actual){
                if(expected.isEmpty()){
                    return new SampleResult();
                } else {
                    errorCount++;
                    return new SampleResult(actual.toString() + ":" + expected.toString());
                }
            }

            @Override
            public SampleResult onSuccess(SampleResult actual){
                return new SampleResult("Y");
            }
        });
        return evalResults;
    }

}
