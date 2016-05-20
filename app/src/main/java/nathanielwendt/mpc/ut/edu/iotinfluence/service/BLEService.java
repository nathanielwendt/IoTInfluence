package nathanielwendt.mpc.ut.edu.iotinfluence.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nathanielwendt.mpc.ut.edu.iotinfluence.db.LocalActionDB;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.DeviceUnavailableException;
import nathanielwendt.mpc.ut.edu.iotinfluence.device.Light;
import nathanielwendt.mpc.ut.edu.iotinfluence.devicereqs.TypeReq;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.DeviceModel;
import nathanielwendt.mpc.ut.edu.iotinfluence.models.LightModel;

/**
 * Created by nathanielwendt on 4/27/16.
 */
public class BLEService implements Service {

    private TypeReq.Type type;
    private String deviceId;

    public BLEService(String deviceId, TypeReq.Type type){
        this.type = type;
        this.deviceId = deviceId;
    }

    public String id(){
        return this.deviceId;
    }

    public class BLEHandler {

        private static final int ADVERTISE_DUR = 3000;
        Handler mHandler;
        private static final String BLE_UUID = "B667104D-93C0-4529-8E37-7C6362A2D09E";
        BluetoothLeAdvertiser advertiser;
        AdvertiseSettings settings;
        AdvertiseData data;
        AdvertiseCallback advertisingCallback;

        public BLEHandler(){
            mHandler = new Handler();

            advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
            settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(false)
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

        //To-Do: check that value size won't overrun beacon buffer
        public void advertise(String value, int duration){
            ParcelUuid pUuid = new ParcelUuid( UUID.fromString(BLE_UUID) );
            data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(pUuid)
                    .addServiceData(pUuid, value.getBytes(Charset.forName("UTF-8")))
                    .build();

            advertiser.startAdvertising(settings, data, advertisingCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    advertiser.stopAdvertising(advertisingCallback);
                }
            }, duration);
        }


        //included for evaluation, remove for final product
        public void advertise(String value, int duration, final AdvertiseCallback callback){
            ParcelUuid pUuid = new ParcelUuid( UUID.fromString(BLE_UUID) );
            data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(pUuid)
                    .addServiceData(pUuid, value.getBytes(Charset.forName("UTF-8")))
                    .build();

            advertiser.startAdvertising(settings, data, advertisingCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    advertiser.stopAdvertising(advertisingCallback);
                    callback.onStartFailure(1);
                }
            }, duration);
        }
    }

    @Override
    public Light light(String deviceId, String requestId, LocalActionDB localActionDB) {
        return new Light(deviceId, requestId, localActionDB) {
            BLEHandler bleHandler = new BLEHandler();

            @Override
            public void brightness(int level) throws DeviceUnavailableException {
                super.brightness(level);
                Log.d("BLElight", "trying to turn on light");
                bleHandler.advertise("bright:" + level, BLEHandler.ADVERTISE_DUR);
            }

            @Override
            public void off() throws DeviceUnavailableException {
                super.off();
                bleHandler.advertise("off", BLEHandler.ADVERTISE_DUR);
            }

            @Override
            public void on() throws DeviceUnavailableException {
                super.on();
                bleHandler.advertise("on", BLEHandler.ADVERTISE_DUR);
            }
        };
    }

    @Override
    public void fetchDevices(FetchDevicesCallback callback) {
        List<DeviceModel> devices = new ArrayList<>();
        if(type == TypeReq.Type.LIGHT){
            LightModel light = new LightModel();
            light.id = deviceId;
            light.hubManufacturer = "BLE Devices";
            light.type = TypeReq.Type.LIGHT;
            light.service = BLEService.this;
            devices.add(light);
        } else {
            Log.w("BLE", "recognized type not supported yet for BLE Service");
        }
        callback.onFetch(devices);
    }
}
