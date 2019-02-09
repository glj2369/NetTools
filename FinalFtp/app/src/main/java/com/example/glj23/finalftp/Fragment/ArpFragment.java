package com.example.glj23.finalftp.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.example.glj23.finalftp.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Bean.ArpBean;
import adapter.ArpList;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArpFragment extends Fragment implements View.OnClickListener {


    private View view;
    /**
     * 开始搜索
     */
    private Button mArpBt;
    private TextView mArpTv;
    private ListView mArpLi;
    private List<ArpBean> list = new ArrayList<ArpBean>();
    private ArpList arpList;
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    arpList.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    public ArpFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arp, container, false);
        initView(view);
        discover(NetworkUtils.getIpAddressByWifi());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                readArp();
                Message message=new Message();
                message.what=0;
                handler.sendMessage(message);
            }
        }, 100, 2000);

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }
    private void initView(View view) {
        mArpBt = view.findViewById(R.id.arp_Bt);
        mArpBt.setOnClickListener(this);
        mArpTv = view.findViewById(R.id.arp_Tv);
        mArpLi = view.findViewById(R.id.arp_li);
        timer = new Timer();
        arpList = new ArpList(getContext(), R.layout.arplistitem, list);
        mArpLi.setAdapter(arpList);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.arp_Bt:
                discover(NetworkUtils.getIpAddressByWifi());
                //Toast.makeText(getContext(), "数据刷新", Toast.LENGTH_SHORT).show();
                Toasty.info(getContext(), "数据刷新", Toast.LENGTH_SHORT, true).show();
                break;
        }
    }
    // 根据ip 网段去 发送arp 请求
    private void discover(String ip) {
        String newip = "";
        if (!ip.equals("")) {
            String ipseg = ip.substring(0, ip.lastIndexOf(".") + 1);
            for (int i = 2; i < 255; i++) {
                newip = ipseg + String.valueOf(i);
                Log.e("-------------", newip);
                if (newip.equals(ip)) continue;
                Thread ut = new UDPThread(newip);
                ut.start();
            }
        }
    }
    //读取qrp表
    private void readArp() {
        list.clear();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    Log.e("scanner", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);
                    list.add(new ArpBean(ip, mac));


                } catch (Exception e) {
                }
            }
            br.close();

        } catch (Exception e) {
        }
    }
    // UDPThread
    public class UDPThread extends Thread {
        private String target_ip = "";

        public final byte[] NBREQ = {(byte) 0x82, (byte) 0x28, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x43, (byte) 0x4B,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x0, (byte) 0x0, (byte) 0x21, (byte) 0x0, (byte) 0x1};

        public static final short NBUDPP = 137;

        public UDPThread(String target_ip) {
            this.target_ip = target_ip;
        }

        @Override
        public synchronized void run() {
            if (target_ip == null || target_ip.equals("")) return;
            DatagramSocket socket = null;
            InetAddress address = null;
            DatagramPacket packet = null;
            try {
                address = InetAddress.getByName(target_ip);
                packet = new DatagramPacket(NBREQ, NBREQ.length, address, NBUDPP);
                socket = new DatagramSocket();
                socket.setSoTimeout(200);
                socket.send(packet);
                socket.close();
            } catch (SocketException se) {
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
