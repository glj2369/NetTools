package com.example.glj23.finalftp.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.glj23.finalftp.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import Bean.IPBeanV6;
import dev.DevUtils;
import dev.utils.app.AppCommonUtils;
import dev.utils.app.NetWorkUtils;
import dev.utils.app.wifi.WifiUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {


    private View view;
    private TextView mTest;
    /**
     * 4353445
     */
    private TextView mWifiSsid;
    /**  */
    private TextView mWifiIp;
    /**  */
    private TextView mWifiOutip;
    /**  */
    private TextView mWifiGbip;
    /**  */
    private TextView mWifiWgip;
    /**  */
    private TextView mWifiMac;
    private WifiUtils wifiUtils;
    /**
     * 4353445
     */
    private TextView mWifiBssid;
    /**
     * 4353445
     */
    private TextView mLoCity;
    /**  */
    private TextView mLoCoun;
    /**  */
    private TextView mLoYys;
    /**  */
    private TextView mLoWd;
    /**  */
    private TextView mLoJd;
    /**  */
    private TextView mLoCiso;
    private NetWorkReceiver netWorkReceiver;
    private IntentFilter intentFilter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    IPBeanV6 ipbean = (IPBeanV6) msg.obj;
                    mWifiOutip.setText(ipbean.getIp());
                    mLoCity.setText(ipbean.getCity());
                    mLoCoun.setText(ipbean.getCountry());
                    mLoJd.setText(ipbean.getLatitude() + "");
                    mLoWd.setText(ipbean.getLongitude() + "");
                    mLoCiso.setText(ipbean.getCountry_iso());
                    break;
                case 1:
                    mWifiOutip.setText("");
                    mLoCity.setText("");
                    mLoCoun.setText("");
                    mLoJd.setText("");
                    mLoWd.setText("");
                    mLoCiso.setText("");
                    break;
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        initDate();
        getOut();
        Log.e("-----", "onCreateView");
        return view;
    }

    private void initDate() {
        mWifiSsid.setText(WifiUtils.getSSID());
        if (AppCommonUtils.isO()) {
            mWifiMac.setText(getWifiMacAddress());
        } else {
            mWifiMac.setText(wifiUtils.getMacAddress(wifiUtils.getWifiInfo()));
        }
        mWifiGbip.setText(NetWorkUtils.getBroadcastIpAddress());
        mWifiWgip.setText(NetWorkUtils.getGatewayByWifi());
        mWifiIp.setText(NetWorkUtils.getIPAddress(true));
        mWifiBssid.setText(wifiUtils.getBSSID(wifiUtils.getWifiInfo()));
        mLoYys.setText(NetWorkUtils.getNetworkOperatorName());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(netWorkReceiver);
    }

    private String getWifiMacAddress() {
        String defaultMac = "02:00:00:00:00:00";
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ntwInterface : interfaces) {

                if (ntwInterface.getName().equalsIgnoreCase("wlan0")) {//之前是p2p0，修正为wlan
                    byte[] byteMac = ntwInterface.getHardwareAddress();
                    if (byteMac == null) {
                        // return null;
                    }
                    StringBuilder strBuilder = new StringBuilder();
                    for (int i = 0; i < byteMac.length; i++) {
                        strBuilder.append(String
                                .format("%02X:", byteMac[i]));
                    }

                    if (strBuilder.length() > 0) {
                        strBuilder.deleteCharAt(strBuilder.length() - 1);
                    }

                    return strBuilder.toString();
                }

            }
        } catch (Exception e) {
//             Log.d(TAG, e.getMessage());
        }
        return defaultMac;
    }

    private void initView(View view) {
        mWifiSsid = view.findViewById(R.id.wifi_ssid);
        mWifiIp = view.findViewById(R.id.wifi_ip);
        mWifiOutip = view.findViewById(R.id.wifi_outip);
        mWifiGbip = view.findViewById(R.id.wifi_gbip);
        mWifiWgip = view.findViewById(R.id.wifi_wgip);
        mWifiMac = view.findViewById(R.id.wifi_mac);
        DevUtils.init(getContext());
        wifiUtils = new WifiUtils();
        mWifiBssid = view.findViewById(R.id.wifi_bssid);
        mLoCity = view.findViewById(R.id.lo_city);
        mLoYys = view.findViewById(R.id.lo_yys);
        mLoWd = view.findViewById(R.id.lo_wd);
        mLoJd = view.findViewById(R.id.lo_jd);
        mLoCiso = view.findViewById(R.id.lo_ciso);
        mLoCoun = view.findViewById(R.id.lo_coun);
        netWorkReceiver = new NetWorkReceiver();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(netWorkReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }


    public void getOut() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request build = new Request.Builder().url("http://ifconfig.co/json").build();
                try {
                    String string = okHttpClient.newCall(build).execute().body().string();
                    Gson gson = new Gson();
                    Log.e("---------",string);
                    IPBeanV6 ipbean = gson.fromJson(string, IPBeanV6.class);
                    Message message = new Message();
                    if (ipbean != null) {
                        message.what = 0;
                        message.obj = ipbean;

                    } else {
                        message.what = 1;
                    }
                    handler.sendMessage(message);
                    Log.e("------", string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    class NetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("--", "碎片接收！！！！！！");
            initDate();

            if (NetWorkUtils.isConnect()) {
                getOut();
            } else {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        }
    }
}
