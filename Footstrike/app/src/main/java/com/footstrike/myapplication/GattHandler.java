package com.footstrike.myapplication;

import static com.footstrike.myapplication.MainActivity.runOnUIThread;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.provider.ContactsContract;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class GattHandler {

    //Gait values
    public static float arch;
    public static float met5;
    public static float met3;
    public static float met1;
    public static float heelR;
    public static float heelL;
    public static float hallux;
    public static float toes;

    public static DataStore data = new DataStore();
    static boolean done = true;
    public static Runnable runnable = () -> {};
    public static Runnable runnableTxt = () -> {};
   // public static int counter;


    //Services and characteristics
    static final UUID deviceServiceUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final UUID deviceServiceCharacteristicUuid1 = UUID.fromString("00002101-0000-1000-8000-00805F9B34FB");
    static final UUID deviceServiceCharacteristicUuid2 = UUID.fromString("00003101-0000-1000-8000-00805F9B34FB");
    static final String deviceMAC = "70:74:95:CF:0D:53";

    //BLE objects
    static BluetoothGattCharacteristic transChar;
    static BluetoothGattCharacteristic transChar2;
    static BluetoothGatt mainGatt;

    public static ConnectionStatusChangeHandler connectionStatusChangeHandler;


    public static boolean isConnected = false;


    //Initialises connection
    public static void init(Context context) {
        // run function on separate thread
        new Thread(() ->
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // use to perform fundamental Bluetooth tasks
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceMAC); // specify our device MAC
            device.connectGatt(context, false, gattCallback);

        }).start();
    }

    public static void write(int s){
        transChar.setValue(s,BluetoothGattCharacteristic.FORMAT_SINT32,0);
        mainGatt.writeCharacteristic(transChar);
    }

    public static byte[] read(){
        return transChar.getValue();

    }

    private static final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            //Success
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                isConnected = true;
                mainGatt = gatt;
                gatt.discoverServices();
            } else {
                isConnected = false;
                gatt.close();
            }

//            connectionStatusChangeHandler.onChange(isConnected);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            //Setup
            transChar = gatt.getService(deviceServiceUuid).getCharacteristic(deviceServiceCharacteristicUuid1);
            transChar2 = gatt.getService(deviceServiceUuid).getCharacteristic(deviceServiceCharacteristicUuid2);


            //Setup notifications
            gatt.setCharacteristicNotification(transChar, true);
            BluetoothGattDescriptor desc = transChar.getDescriptors().get(0);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);


            //Write to descriptor to seal the deal
            gatt.writeDescriptor(desc);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

                gatt.setCharacteristicNotification(transChar2, true);
                BluetoothGattDescriptor desc2 = transChar2.getDescriptors().get(0);
                desc2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                gatt.writeDescriptor(desc2);  // write the second desc after the first has finished


        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // store characteristic values in a Buffer
            ByteBuffer buffer = ByteBuffer.wrap(characteristic.getValue());
            // order the buffer
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            // check which characteristic triggered this method
            if (characteristic.getUuid().equals(deviceServiceCharacteristicUuid1)) {
                arch  = buffer.getFloat();
                met5 = buffer.getFloat();
                met3 = buffer.getFloat();
                met1 = buffer.getFloat();
            }else if(characteristic.getUuid().equals(deviceServiceCharacteristicUuid2)){
                heelR = buffer.getFloat();
                heelL = buffer.getFloat();
                hallux = buffer.getFloat();
                toes = buffer.getFloat();
            }
            // Stored data in data store object
            data.archVal  = CalculateForce.calculateForceADS(arch);
            data.met5Val = CalculateForce.calculateForceADS(met5);
            data.met3Val = CalculateForce.calculateForceArduino(met3);
            data.met1Val = CalculateForce.calculateForceArduino(met1);
            data.heelrVal = CalculateForce.calculateForceArduino(heelR);
            data.heellVal = CalculateForce.calculateForceArduino(heelL);
            data.halluxVal = CalculateForce.calculateForceArduino(hallux);
            data.toesVal = CalculateForce.calculateForceArduino(toes);

            runOnUIThread(()->{
                runnable.run();
                runnableTxt.run();
            });

        }


    };


    public interface ConnectionStatusChangeHandler {
        void onChange(boolean isConnected);
    }


}
