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
import nathanielwendt.mpc.ut.edu.iotinfluence.util.Statistics;
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

            Grid.HOT_WEIGHT = .6;
            Grid.MED_WEIGHT = .25;
            Grid.COLD_WEIGHT = .15;

            zoneManager.add(new Zone(2,6,2,11), Zone.Weight.HOT); //living main
            zoneManager.add(new Zone(6,16,2,4), Zone.Weight.HOT); //kitchen main
            zoneManager.add(new Zone(12,19,7,13), Zone.Weight.MED); //bathroom
            zoneManager.add(new Zone(9,12,7,13), Zone.Weight.COLD); //closet

            zoneManager.add(new Zone(0,2,0,13), Zone.Weight.COLD);
            zoneManager.add(new Zone(2,16,0,2), Zone.Weight.COLD);
            zoneManager.add(new Zone(16,19,0,7), Zone.Weight.COLD);
            zoneManager.add(new Zone(6,16,4,7), Zone.Weight.COLD); //h
            zoneManager.add(new Zone(6,9,7,13), Zone.Weight.COLD); //i
            zoneManager.add(new Zone(2,6,10,13), Zone.Weight.COLD);
        }
    }

    public static class OneBedroomGrid extends Grid {
        public OneBedroomGrid(double step) {
            this.step = step;
            this.width = 19;
            this.height = 28;

            Location[] lightLocs = new Location[]{new Location(5, 21), new Location(9, 15),
                    new Location(14, 21), new Location(18, 16),
                    new Location(2, 1), new Location(9,1),
                    new Location(15,7)};

            Service testService = new TestService();
            int count = 0;
            for (Location loc : lightLocs) {
                LightModel dev = new LightModel();
                dev.id = String.valueOf(count++);
                dev.service = testService;
                dev.location = loc;
                devices.add(dev);
            }

            Grid.HOT_WEIGHT = .3;
            Grid.MED_WEIGHT = .1;
            Grid.COLD_WEIGHT = .5;

//            Grid.HOT_WEIGHT = .30;
//            Grid.MED_WEIGHT = .22;
//            Grid.COLD_WEIGHT = .48;


            partitions.add(new Geometry.LineSegment(0, 14, 10, 14));
            partitions.add(new Geometry.LineSegment(10, 14, 10, 28));
            partitions.add(new Geometry.LineSegment(10, 20, 19, 20));
            partitions.add(new Geometry.LineSegment(10, 0, 10, 8));

            zoneManager.add(new Zone(2,6,2,10), Zone.Weight.HOT);
            zoneManager.add(new Zone(12,15,2,16), Zone.Weight.HOT);
            zoneManager.add(new Zone(2,6,16,24), Zone.Weight.HOT);
            zoneManager.add(new Zone(6.01,11.99,9,10), Zone.Weight.HOT); //hallway
            zoneManager.add(new Zone(9,18,19,27), Zone.Weight.MED); //bathroom

            zoneManager.add(new Zone(0,2,0,13), Zone.Weight.COLD);
            zoneManager.add(new Zone(2,9,0,2), Zone.Weight.COLD);
            zoneManager.add(new Zone(2,9,10,13), Zone.Weight.COLD);//c
            zoneManager.add(new Zone(6,9,2,10), Zone.Weight.COLD);
            zoneManager.add(new Zone(9,12,0,19), Zone.Weight.COLD);//e
            zoneManager.add(new Zone(12,15,0,2), Zone.Weight.COLD);
            zoneManager.add(new Zone(15,18,0,19), Zone.Weight.COLD);
            zoneManager.add(new Zone(12,15,16,19), Zone.Weight.COLD);
            zoneManager.add(new Zone(6,9,13,27), Zone.Weight.COLD);
            zoneManager.add(new Zone(2,6,24,27), Zone.Weight.COLD);
            zoneManager.add(new Zone(0,2,13,27), Zone.Weight.COLD);
            zoneManager.add(new Zone(2,6,13,16), Zone.Weight.COLD);
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
        Grid grid = new SimpleGrid(0.5);
        final Warble warble = new Warble(new Activity());
        TestDevManager testDevManager = new TestDevManager(grid.getDevices());
        testDevManager.removeLocations();
        warble.setDevManager(testDevManager);
        warble.discover();
        while(!warble.hasDiscovered()){}


//        SampleResult[][] evalResults = evaluateWarble(grid, warble);
//        printCountSummaries();
//
//        //grid.overlayDevices(evalResults);
//        //printGrid(evalResults);
//
//        Location[] trainLocs = grid.getProbLocs(20);
//
//        List<SampleResult> trainErrors = trainWarble(grid, warble, trainLocs, false);
//        evalResults = evaluateWarble(grid, warble);
//        printCountSummaries();
//
//        grid.overlayLocations(evalResults, trainLocs, new SampleResult("#"));
//        grid.overlayDevices(evalResults);
//
//        printGrid(evalResults);


        //SpatialReqOperator.DISTANCE_WEIGHT = 3000;

        Results results = iterate(grid, warble, 192, 1000);
        Statistics perCorrectDelta = results.getPerCorrectDelta();
        Statistics correctDelta = results.getCorrectDelta();
        Statistics errorDelta = results.getErrorDelta();

        ///System.out.println("--- % Correct Delta ---");
        System.out.println(perCorrectDelta.mean());
        System.out.println(perCorrectDelta.median());
        System.out.println(perCorrectDelta.stdDev());
        System.out.println(perCorrectDelta.min());
        System.out.println(perCorrectDelta.max());

        //System.out.println("--- Correct Delta ---");
        System.out.println(correctDelta.mean());
        System.out.println(correctDelta.median());
        System.out.println(correctDelta.stdDev());
        System.out.println(correctDelta.min());
        System.out.println(correctDelta.max());

        //System.out.println("--- Error Delta ---");
        System.out.println(errorDelta.mean());
        System.out.println(errorDelta.median());
        System.out.println(errorDelta.stdDev());
        System.out.println(errorDelta.min());
        System.out.println(errorDelta.max());
    }

    public Results iterate(Grid grid, Warble warble, int numTraining, int iterations){
        LocalActionDB.clear();
        SampleResult[][] evalResults = evaluateWarble(grid, warble);

        double initialCorrect = 1 - ((double) errorCount / (double) evalCount);
        System.out.println("initial % correct: " + initialCorrect);
        System.out.println("initial correct count: " + (evalCount - errorCount));
        //System.out.println("initial correct count: " + (evalCount - errorCount));
        Results results = new Results(initialCorrect, errorCount, iterations);

        for(int i = 0; i < iterations; i++){
            LocalActionDB.clear();
            Location[] trainLocs = grid.getProbLocs(numTraining);
            List<SampleResult> trainErrors = trainWarble(grid, warble, trainLocs, false);
            evalResults = evaluateWarble(grid, warble);
            double perCorrect = 1 - ((double) errorCount / (double) evalCount);

            results.addEntry(perCorrect, errorCount);
            //System.out.println("error: " + errorCount);
            //System.out.println("correct: " + (evalCount - errorCount));
            //System.out.println(initialCorrect + " -> " + perCorrect);
        }
        return results;
    }

    private static class Results {
        private double perOriginal;
        private double errorOriginal;
        private double totalSamples;

        private double[] perCorrect;
        private double[] error;
        private double[] correct;
        private int index = 0;

        public Results(double perOriginal, double errorOriginal, int size){
            this.perOriginal = perOriginal;
            this.errorOriginal = errorOriginal;
            this.totalSamples = size;

            perCorrect = new double[size];
            error = new double[size];
            correct = new double[size];
        }

        public void addEntry(double perCorrect, double error){
            this.perCorrect[index] = perCorrect;
            this.error[index] = error;
            this.correct[index] = totalSamples - error;
            index++;
        }

        private double[] getDelta(double[] results, double original){
            double[] data = new double[results.length];
            for(int i = 0; i < results.length; i++){
                data[i] = results[i] - original;
            }
            return data;
        }

        public Statistics getPerCorrectDelta(){
            return new Statistics(getDelta(perCorrect, perOriginal));
        }

        public Statistics getCorrectDelta(){
            return new Statistics(getDelta(correct, totalSamples - errorOriginal));
        }

        public Statistics getErrorDelta(){
            return new Statistics(getDelta(error, errorOriginal));
        }
    }

    private void printCountSummaries(){
        System.out.println("percent correct: " + (1 - ((double) errorCount / (double) evalCount)));
        System.out.println("number correct: " + (evalCount - errorCount));
        System.out.println("number incorrect: " + errorCount);
        System.out.println("number total: " + evalCount);
    }

//    @Test
//    public void testSetup(){
//        Grid.HOT_WEIGHT = .34;
//        Grid.MED_WEIGHT = .16;
//        Grid.COLD_WEIGHT = .5;
//        SpatialReqOperator.DistToHistoryRatio = 14;
//        double improvement = iterate(160);
//    }

    @Test
    public void testGoldStandard(){
        Grid grid = new StudioGrid(0.5);
        Warble warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(grid.getDevices()));
        warble.discover();
        while(!warble.hasDiscovered()){}

        grid.mockTrainAllLocs();

        SampleResult[][] evalResults = evaluateWarble(grid, warble);
        printCountSummaries();

        grid.overlayDevices(evalResults);
        printGrid(evalResults);
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
                        SpatialReq.Influence.AWARE, new Location(x, y));
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
            public SampleResult onError(SampleResult expected, SampleResult actual) {
                if (expected.isEmpty()) {
                    return new SampleResult();
                } else {
                    errorCount++;
                    return new SampleResult(actual.toString() + ":" + expected.toString());
                }
            }

            @Override
            public SampleResult onSuccess(SampleResult actual) {
                return new SampleResult("Y");
            }
        });
        return evalResults;
    }

    @Test
    public void testGenerateProbHeatmap(){
        Grid grid = new StudioGrid(1);
        SampleResult[][] heatmapRes = grid.getZoneHeatmap(false);
        HeatChart map = new HeatChart(SampleResult.toDoubleArr(heatmapRes));


        // Step 2: Customise the chart.
        map.setTitle("Studio");
        map.setXAxisLabel("Distance (ft.)");
        map.setYAxisLabel("Distance (ft.)");
        map.setXAxisValuesFrequency(2);
        map.setYAxisValuesFrequency(2);


        // Step 3: Output the chart to a file.
        try {
            map.saveToFile(new File("java-heat-chart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTrainingGeneration(){
        Grid grid = new OneBedroomGrid(1);
        final Warble warble = new Warble(new Activity());
        warble.setDevManager(new TestDevManager(grid.getDevices()));
        warble.discover();
        while(!warble.hasDiscovered()){}

        Location[] trainLocs = grid.getProbLocs(25);

        SampleResult[][] evalResults = grid.evaluate(new EvaluationAction() {
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

        grid.overlayDevices(evalResults);
        grid.overlayLocations(evalResults, trainLocs, new SampleResult("#"));
        printGrid(evalResults);

        SampleResult[][] heatmapRes = grid.getZoneHeatmap(false);
        HeatChart map = new HeatChart(SampleResult.toDoubleArr(heatmapRes));

        // Step 2: Customise the chart.
        map.setTitle("Saweeet");
        map.setXAxisLabel("Distance (ft.)");
        map.setYAxisLabel("Distance (ft.)");


        // Step 3: Output the chart to a file.
        try {
            map.saveToFile(new File("java-heat-chart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testFindBestParams(){
//
//        for(int i = 0; i < 5; i++){
//            System.out.println("------- Beginning test ------");
//            setupParams(i);
//            System.out.println("-- HOT: " + Grid.HOT_WEIGHT);
//            System.out.println("-- MED: " + Grid.MED_WEIGHT);
//            System.out.println("-- COLD: " + Grid.COLD_WEIGHT);
//            double best = 0;
//            double improved;
//            int bestRatio = -1;
//            for(int j = 1; j < 25; j++){
//                System.out.println("DistToHistoryRatio >> " + j);
//                SpatialReqOperator.DistToHistoryRatio = j;
//                improved = iterate(80);
//                if(improved > best){
//                    best = improved;
//                    bestRatio = j;
//                }
//            }
//            System.out.println("The best ratio with these params is: " + bestRatio + ". Improved by" + best);
//            System.out.println("--------  End of test -------");
//        }
//    }


}
