package nathanielwendt.mpc.ut.edu.iotinfluence;

import android.app.Activity;

import org.junit.Test;
import org.tc33.jheatchart.HeatChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
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

    public static class SimpleGrid extends Grid {
        public SimpleGrid(double step){
            this.step = step;
            this.width = 25;
            this.height = 25;
            Location[] lightLocs = new Location[]{new Location(0,0), new Location(0,24),
                                                    new Location(24,24), new Location(24,0)};
            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            partitions.add(new Geometry.LineSegment(5, 0, 5, 25));
            partitions.add(new Geometry.LineSegment(0, 20, 25, 20));

            zoneManager.add(new Zone(0,5,0,20), Zone.Weight.MED);
            zoneManager.add(new Zone(0,5,20,24), Zone.Weight.COLD);
            zoneManager.add(new Zone(6,24,0,20), Zone.Weight.HOT);
            zoneManager.add(new Zone(6,24,21,24), Zone.Weight.MED);
        }
    }

    public static class StudioGrid extends Grid {
        public StudioGrid(double step) {
            this.step = step;
            this.width = 20;
            this.height = 14;

            Location[] lightLocs = new Location[]{new Location(2, 2), new Location(4, 7),
                    new Location(12, 11), new Location(17, 9),
                   // new Location(12, 5),
                    new Location(19, 3)};

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
            zoneManager.add(new Zone(7.001,17.99,5.001,7.99), Zone.Weight.HOT); //target area
            zoneManager.add(new Zone(3,9.99,11.001,13), Zone.Weight.HOT);
            zoneManager.add(new Zone(8,9,8,11), Zone.Weight.COLD);
        }
    }

    public static class StudioGrid2 extends Grid {
        public StudioGrid2(double step) {
            this.step = step;
            this.width = 19;
            this.height = 28;

            Location[] lightLocs = new Location[]{new Location(5, 21), new Location(9, 15),
                    new Location(14, 21), new Location(14, 19),
                    new Location(7, 7), new Location(13,7)};

            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            partitions.add(new Geometry.LineSegment(0, 14, 10, 14));
            partitions.add(new Geometry.LineSegment(10, 14, 10, 27));
            partitions.add(new Geometry.LineSegment(10, 20, 19, 20));

            zoneManager.add(new Zone(0,10,14,27), Zone.Weight.MED);
            zoneManager.add(new Zone(0,18,0,14), Zone.Weight.MED);
            zoneManager.add(new Zone(10,18,14,20), Zone.Weight.MED);
            zoneManager.add(new Zone(10,18,20,27), Zone.Weight.MED);

        }
    }

    public Light lastWarbleLight;


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
        printGrid(evalResults);

        Location[] trainLocs = studioGrid.getProbLocs(100);
        //Location[] trainLocs = studioGrid.getAllLocs();
        System.out.println(trainLocs.length);

        List<SampleResult> trainErrors = trainWarble(studioGrid, warble, trainLocs, false);
        evalResults = evaluateWarble(studioGrid, warble);
        System.out.println(1 - ((double) errorCount / (double) evalCount));

        studioGrid.overlayLocations(evalResults, trainLocs, new SampleResult("#"));
        studioGrid.overlayDevices(evalResults);

        printGrid(evalResults);
    }

    @Test
    public void testIterate(){
        Grid studioGrid = new StudioGrid(0.5);
        Warble warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(studioGrid.getDevices()));
        warble.initialize();
        while(!warble.initialized()){}

        double totalImprovement = 0;
        int i = 0;
        SampleResult[][] evalResults = evaluateWarble(studioGrid, warble);
        double initialCorrect = 1 - ((double) errorCount / (double) evalCount);
        for(i = 0; i < 10; i++){
            LocalActionDB.clear();
            Location[] trainLocs = studioGrid.getProbLocs(50);
            List<SampleResult> trainErrors = trainWarble(studioGrid, warble, trainLocs, false);
            evalResults = evaluateWarble(studioGrid, warble);
            double perCorrect = 1 - ((double) errorCount / (double) evalCount);
            totalImprovement += (perCorrect - initialCorrect);
            System.out.println(initialCorrect + " -> " + perCorrect);
        }

        System.out.println(totalImprovement / i);
    }

    private List<SampleResult> trainWarble(Grid grid, final Warble warble, Location[] trainLocs, boolean repeat){
        List<SampleResult> trainErrors = grid.train(trainLocs, repeat, new TrainingAction() {
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
        return trainErrors;
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
}
