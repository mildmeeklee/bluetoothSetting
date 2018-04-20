package com.example.eylee.bluetoothsetting;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.eylee.bluetoothsetting.BluetoothChatService.*;

/**
 * 
 * @author eylee 2018-04-03 
 * 공통 변수 관리하는 클래스
 *
 */
public class BluetoothDeviceData {
//	public static PrinterConnFactoryManager printerConnFactoryManager = null;
    public static ArrayList<String> per = new ArrayList<>();
    public static  String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };
    public static final int REQUEST_CODE =30;
    public static final int BLUETOOTH_REQUEST_CODE = 31;
    public static final int CONN_STATE_DISCONN =32;
    public static final int PRINTER_COMMAND_ERROR = 33;
    public static final int CONN_PRINTER = 34;
    public static int id = 0;
    public static int counts;
    
    public static PendingIntent mPermissionIntent;
    

    
    
    public static final int    REQUEST_ENABLE_BT      = 10001;
    public static final int    REQUEST_ENABLE_PAIR      = 10003;
    public static final String EXTRA_DEVICE_ADDRESS = "address";
	public static  BroadcastReceiver bluetoothDeviceReceiver = null;
	public static BluetoothDeviceHandler bluetoothDeviceHandler = null;

	public static BluetoothChatService bluetoothChatService = null;
    /**
     * Array adapter for the conversation thread
     */
    public static ArrayAdapter<String> mConversationArrayAdapter = null;

	  //블루투스 휴대용 프린터기 변수
	public static  BluetoothAdapter mBluetoothAdapter; // 블루투스 장치 제어가능한 객체


    public static ArrayAdapter<String> mConnectedArrayAdapter;  // connected 디바이스
	public static  ArrayAdapter<String> mPairedDevicesArrayAdapter; // paired 된 디바이스
	public static  ArrayAdapter<String> mNewDevicesArrayAdapter; // new 디바이스

    public static ArrayList<ConnectedItem> connectedItems = new ArrayList<ConnectedItem>();
    public static ArrayList<ConnectedItem> newDeviceItems = new ArrayList<ConnectedItem>();
    public static ArrayList<PairedItem> pairedItems = new ArrayList<PairedItem>();
//    public static ArrayList<ConnectedItem> pairedItems = new ArrayList<ConnectedItem>();

    public static ConnectedListBaseAdapter connectedListBaseAdapter;
    public static NewDeviceListBaseAdapter newDeviceListBaseAdapter;
    public static PairedListBaseAdapter    pairedListBaseAdapter;

	public static  ListView listViewPairedDevices = null,  listViewNewDevices = null;
	public static  TextView textViewPariedDevice = null, textViewNewDeivce = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_CONNECTED = 20;
    public static final int MESSAGE_TOAST = 5;

    public static final String MESSAGE_DEL_POS = "del_position";
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public  static final String ACTION_CONN_STATE = "action_connect_state";
    public  static  final String ACTION_QUERY_PRINTER_STATE = "action_query_printer_state";
    public  static  final String STATE = "state";
    public   static final String DEVICE_ID = "id";
    public static  final int CONN_STATE_DISCONNECT = 20;
    public  static final int CONN_STATE_CONNECTING = 21;
    public   static final int CONN_STATE_FAILED = 22;
    public  static  final int CONN_STATE_CONNECTED = 23;
    public  static  final int CONN_STATE_PAIRED = 25;
    public static ConcurrentHashMap<String, BluetoothChatService.ConnectionThread> deviceConnHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, BluetoothChatService.ConnectionThread> deviceConnTryTempHashMap = new ConcurrentHashMap<>();
	// print 버튼 눌렀을 때 array list 에 객체 저장
//	public static  ArrayList<MyBluetoothDevice> bluetoothDevices = null;
    
	//
//	public static ArrayList<PrinterConnManager> printerConnManagerList = null;
}
