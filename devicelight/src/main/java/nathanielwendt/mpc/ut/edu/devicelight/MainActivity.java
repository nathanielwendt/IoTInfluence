package nathanielwendt.mpc.ut.edu.devicelight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String BLE_UUID = "F0F816B3-F6DE-48B9-9C5D-CE6EB4A59C97";
    BluetoothLeAdvertiser advertiser;
    AdvertiseSettings settings;
    AdvertiseData data;
    AdvertiseCallback advertisingCallback;

    private BluetoothLeScanner mBluetoothScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Context ctx;
    private static final int SCAN_PERIOD = 5000;

    TextView console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = (TextView) findViewById(R.id.console_text);

        //setup scanning
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
        }
        final android.bluetooth.BluetoothManager bluetoothManager =
                (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();


        //setup advertising
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable(false)
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(BLE_UUID) );
        data = new AdvertiseData.Builder()
//                .setIncludeDeviceName(true)
                .addServiceUuid(pUuid)
                .addServiceData(pUuid, "Active".getBytes(Charset.forName("UTF-8")))
                .build();

        advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d("BLE", "Advertising started successfully");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                super.onStartFailure(errorCode);
            }
        };
    }

    public String extractCommand(ScanResult result){
        Map<ParcelUuid, byte[]> res = result.getScanRecord().getServiceData();
        for(Map.Entry<ParcelUuid, byte[]> item : res.entrySet()){
            return new String(item.getValue(), StandardCharsets.UTF_8);
        }
        return "";
    }

    public void setScreen(boolean on){
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        if(on){
            layout.screenBrightness = 1F;
        } else {
            layout.screenBrightness = 0F;
        }
        getWindow().setAttributes(layout);
    }

    public void actCommand(String command){
        switch(command){
            case "on": setScreen(true); break;
            case "off": setScreen(false); break;
        }
    }

    public void onClickAdvertise(View v){
        advertiser.startAdvertising( settings, data, advertisingCallback );
    }

    public void onClickStopAdvertise(View v){
        Log.d("BLE", "stopping advertising");
        advertiser.stopAdvertising(advertisingCallback);
    }

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String command = extractCommand(result);
            Log.d("BLE", "command >> " + command);
            console.setText("Received command >> " + command);
            actCommand(command);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public void onClickStartScan(View v){
        console.setText("Scanning...");
        mBluetoothScanner.startScan(scanCallback);
    }

    public void onClickStopScan(View v){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                console.setText("");
                mBluetoothScanner.stopScan(scanCallback);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
