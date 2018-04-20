package com.example.eylee.bluetoothsetting;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothSettingListActivity extends Activity{

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    ArrayList<String> per = new ArrayList<>();
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    private static final int REQUEST_CODE = 0x004;


    private ListView listViewConnectedDevices = null, listViewPairedDevices = null,  listViewNewDevices = null;
    private TextView textViewConnectedDevices = null, textViewPariedDevice = null, textViewNewDeivce = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.bluetooth_setting_list);

        //view create
        textViewConnectedDevices =  (TextView)  findViewById(R.id.tvConnectedDevices);
        textViewNewDeivce = (TextView) findViewById(R.id.tvNewDevices);
        textViewPariedDevice = (TextView) findViewById(R.id.tvPairedDevices);
        listViewConnectedDevices = (ListView) findViewById(R.id.lvConnectedDevices);
        listViewNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        listViewPairedDevices = (ListView) findViewById(R.id.lvPairedDevices);

        mContext = getApplicationContext();
        if(BluetoothDeviceData.mBluetoothAdapter == null){
            BluetoothDeviceData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        checkPermission();
        requestPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startBluetoothServcie();
    }

    private void startBluetoothServcie() {

        if(!BluetoothDeviceData.mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothDeviceData.REQUEST_ENABLE_BT);
        }else if(BluetoothDeviceData.bluetoothChatService == null){
            setupChat();
            ToastUtils.toast(mContext,"setupChat!!");
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
//        BluetoothDeviceData.mConversationArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.message);

        // Initialize the BluetoothChatService to perform bluetooth connections
        if(BluetoothDeviceData.bluetoothDeviceHandler == null){
            BluetoothDeviceData.bluetoothDeviceHandler = new BluetoothDeviceHandler(mContext);
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        BluetoothDeviceData.bluetoothChatService = new BluetoothChatService(mContext, BluetoothDeviceData.bluetoothDeviceHandler);


    }

    private void checkPermission() {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                per.add(permission);
            }else{
                registerReceiverNHandler();
            }
        }

    }
    private void requestPermission() {
        if (per.size() > 0) {
            String[] p = new String[per.size()];
            ActivityCompat.requestPermissions(this, per.toArray(p), REQUEST_CODE);
        }
    }

    public void registerReceiverNHandler(){
        if(BluetoothDeviceData.bluetoothDeviceReceiver == null){
            BluetoothDeviceData.bluetoothDeviceReceiver = new BluetoothDeviceBroadcastReceiver(mContext);

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDeviceData.ACTION_CONN_STATE);
            registerReceiver(BluetoothDeviceData.bluetoothDeviceReceiver, filter);
            // 블루투스 디바이스가 찾아졌을 때, 인텐트를 전달받기 위핸 인텐트 필터 등록
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(BluetoothDeviceData.bluetoothDeviceReceiver, filter);
            //블루투스 디바이스 검색 과정이 끝났을 때, 인텐트를 전달받기 위한 인텐트 필터 등록
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(BluetoothDeviceData.bluetoothDeviceReceiver, filter);

            filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            registerReceiver(BluetoothDeviceData.bluetoothDeviceReceiver, filter);

            filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(BluetoothDeviceData.bluetoothDeviceReceiver, filter);
        }

        if(BluetoothDeviceData.bluetoothDeviceHandler == null){
            BluetoothDeviceData.bluetoothDeviceHandler = new BluetoothDeviceHandler(mContext);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        discoveryDevice();
        getDeviceList();


        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (BluetoothDeviceData.bluetoothChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BluetoothDeviceData.bluetoothChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                BluetoothDeviceData.bluetoothChatService.start();
            }
        }
    }

    private void discoveryDevice() {

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scaning);
        // Turn on sub-title for new devices
        textViewNewDeivce.setVisibility(View.VISIBLE);
        listViewNewDevices.setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (BluetoothDeviceData.mBluetoothAdapter.isDiscovering()) {
            BluetoothDeviceData.mBluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        BluetoothDeviceData.mBluetoothAdapter.startDiscovery();
        if(!BluetoothDeviceData.mBluetoothAdapter.isDiscovering()){
            Log.d(TAG, "action :: isDiscovering not" );
            BluetoothDeviceData. mBluetoothAdapter.startDiscovery();

        }else{
            Log.d(TAG, "action :: isDiscovering !!!!" );
        }
    }

    private void getDeviceList() {
//        BluetoothDeviceData.mConnectedArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_connected_device_item);
        BluetoothDeviceData.mConnectedArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_device_name_item);
        BluetoothDeviceData.mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_device_name_item);
        BluetoothDeviceData.mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_device_name_item);

        listViewConnectedDevices.setAdapter(BluetoothDeviceData.mConnectedArrayAdapter);
        listViewPairedDevices.setAdapter(BluetoothDeviceData.mPairedDevicesArrayAdapter);
        listViewNewDevices.setAdapter(BluetoothDeviceData.mNewDevicesArrayAdapter);

        listViewConnectedDevices.setOnItemClickListener(mConnectedDeivceClickListener);
        listViewPairedDevices.setOnItemClickListener(mDeviceClickListener);
        listViewNewDevices.setOnItemClickListener(mDeviceClickListener);

        //현재 paired 된 단말 리스트
        Set<BluetoothDevice> pairedDevices = BluetoothDeviceData.mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            textViewPariedDevice.setVisibility(View.VISIBLE);

            for(BluetoothDevice device : pairedDevices){
                BluetoothDeviceData.mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else{
//            String noDevices = getResources().getText(R.string.none_paired).toString();
            BluetoothDeviceData.mPairedDevicesArrayAdapter.add("none paired");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BluetoothDeviceData.REQUEST_ENABLE_BT){
            ToastUtils.toast(mContext, "REQUEST_ENABLE_BT");
        }
    }


    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            BluetoothDeviceData.mBluetoothAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

//            String noDevices = getResources().getText(R.string.none_paired).toString();
//            String noNewDevice = getResources().getText(R.string.none_bluetooth_device_found).toString();
            Log.i(TAG, "info :: 클릭시 :: " + info);
            ToastUtils.toast(mContext, "address :: 클릭시 :: " + address);
            connectDevice(address, false);
        }
    };

    private AdapterView.OnItemClickListener mConnectedDeivceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            Log.i(TAG, "info :: 클릭시 :: " + info);
            ToastUtils.toast(mContext, "address :: 클릭시 :: " + address);
            disconnectDevice(address, position);
        }
    };
    /**
     * Establish connection with other device
     *
     * @param addr
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(String addr, boolean secure) {

        // Get the device MAC address
        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);
        // Attempt to connect to the device
        BluetoothDeviceData.bluetoothChatService.connect(device, secure);
    }

    private  void disconnectDevice(String addr, int position){
        // Get the device MAC address
        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothDeviceData.mBluetoothAdapter.getRemoteDevice(addr);
        // Attempt to connect to the device
        BluetoothDeviceData.bluetoothChatService.disconnected(device, position);
    }
    public void btn_tvConnectedDevices(View view){
        if(listViewConnectedDevices.getVisibility()==View.GONE)
            listViewConnectedDevices.setVisibility(View.VISIBLE);
        else if(listViewConnectedDevices.getVisibility()==View.VISIBLE)
            listViewConnectedDevices.setVisibility(View.GONE);

    }

    public void btn_tvPairedDevices(View view){
        if(listViewPairedDevices.getVisibility()==View.GONE)
            listViewPairedDevices.setVisibility(View.VISIBLE);
        else if(listViewPairedDevices.getVisibility()==View.VISIBLE)
            listViewPairedDevices.setVisibility(View.GONE);

    }

    public void btn_tvNewDevices(View view){
        if(listViewNewDevices.getVisibility()==View.GONE)
            listViewNewDevices.setVisibility(View.VISIBLE);
        else if(listViewNewDevices.getVisibility()==View.VISIBLE)
            listViewNewDevices.setVisibility(View.GONE);
    }
}
