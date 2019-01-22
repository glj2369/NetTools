package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.example.glj23.finalftp.R;

import org.apache.ftpserver.FtpServer;

import es.dmoral.toasty.Toasty;
import service.FtpService;

/**
 * A simple {@link Fragment} subclass.
 */
public class FtpFragment extends Fragment implements View.OnClickListener {

    /**
     * ftp开关
     */
    private Button mBtFtp;
    /**
     * 自动加载开关
     */
    private Button mBtFtpjz;
    /**
     * 启动自动加载：
     */
    private TextView mTvFtpjz;
    /**
     * ftp服务器状态：
     */
    private TextView mTvFtptt;
    /**
     * ftp服务器地址：
     */
    private TextView mTvFtpdz;
    private View view;
    private FtpServer mFtpServer;
    public static boolean flag = false;
    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private Intent intent;
    private EditText mServerUser;
    private EditText mServerPsw;
    /**
     * 2222
     */
    private EditText mServerPort;
    private SharedPreferences ftpServer;
    /**
     * 保存配置
     */
    private Button mServerSave;
    private AlertDialog builder;
    private String username;
    private String psw;
    private String port;

    public FtpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ftp, container, false);
        initView();
        mBtFtp.setOnClickListener(this);
       /* File f = new File(ftpConfigDir);
        if (!f.exists()) {
            f.mkdir();
        }*/
        //copyResourceFile(R.raw.users, ftpConfigDir + "users.properties");//拷贝配置文件到sd卡
        // Read(ftpConfigDir + "users.properties");
        Log.e("!!!!!!", Environment.getExternalStorageDirectory().getAbsolutePath());
        ftpServer = getContext().getSharedPreferences("FtpServer", Context.MODE_PRIVATE);
        username = ftpServer.getString("user", "admin");
        psw = ftpServer.getString("psw", "123");
        port = ftpServer.getString("port", "2222");
        mServerUser.setText(username);
        mServerPsw.setText(psw);
        mServerPort.setText(port);
        return view;
    }


    private void initView() {
        mBtFtp = (Button) view.findViewById(R.id.bt_ftp);
        mBtFtp.setOnClickListener(this);
        mTvFtptt = (TextView) view.findViewById(R.id.tv_ftptt);
        mTvFtpdz = (TextView) view.findViewById(R.id.tv_ftpdz);
        context = getContext().getApplicationContext();
        mServerUser = (EditText) view.findViewById(R.id.server_user);
        mServerPsw = (EditText) view.findViewById(R.id.server_psw);
        mServerPort = (EditText) view.findViewById(R.id.server_port);
        mServerSave = (Button) view.findViewById(R.id.server_save);
        mServerSave.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context == null) {
            context = getContext().getApplicationContext();
        }

        if (ServiceUtils.isServiceRunning("service.FtpService")) {
            Log.e("--------", "废物");
            ftpOn();
            flag = true;
        } else {
            ftpOff();
            flag = false;
        }

        if (localBroadcastManager == null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bt_ftp:
                if (flag) {
                    localBroadcastManager.sendBroadcast(new Intent("stopftp"));
                    ftpOff();
                    flag = false;
                } else {

                    if (!mServerPort.getText().toString().equals("") && !mServerUser.getText().toString().equals("")) {
                        long port = Long.parseLong(mServerPort.getText().toString());
                        if (port >= 1025 && port <= 65534) {
                            save();
                            intent = new Intent(context, FtpService.class);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent);
                            } else {
                                context.startService(intent);
                            }
                            ftpOn();
                            flag = true;
                        } else {
                            error("端口号必须在1024-65534之间");
                        }


                    } else {
                        error("用户名或端口号不能为空！");
                    }


                }

                break;
            case R.id.server_save:
                if (!mServerPort.getText().toString().equals("") && !mServerUser.getText().toString().equals("") ) {
                    save();
                    long port = Long.parseLong(mServerPort.getText().toString());
                    if (port >= 1025 && port <= 65534) {
                        //Toast.makeText(context, "配置已保存!^_^", Toast.LENGTH_SHORT).show();
                        Toasty.success(context, "配置已保存!^_^", Toast.LENGTH_SHORT, true).show();
                    } else {
                        error("端口号必须在1024-65534之间!");
                    }

                } else {
                    error("用户名或端口号不能为空！");
                }

                break;
        }
    }

    private void error(String s) {
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        builder = new AlertDialog.Builder(getContext())
                .setTitle("警告！")
                .setMessage(s)
                .setIcon(R.drawable.warn)
                .setPositiveButton("确定", null)
                .setCancelable(false).create();
        builder.getWindow().setWindowAnimations(R.style.mystyle);
        builder.show();
    }

    private void save() {
        SharedPreferences.Editor edit = ftpServer.edit();
        edit.putString("user", mServerUser.getText().toString().trim());
        edit.putString("psw", mServerPsw.getText().toString().trim());
        edit.putString("port", mServerPort.getText().toString().trim());
        edit.apply();
    }

    private void ftpOn() {
        mTvFtpdz.setText("Ftp服务器地址：" + "\nFtp://" + NetworkUtils.getIPAddress(true)
                + ":" + mServerPort.getText().toString().trim() + "\n");
        mTvFtptt.setText("Ftp服务器状态：" + "开启！");
        //Toast.makeText(getActivity(), "ftp服务器已启动！", Toast.LENGTH_SHORT).show();
        mBtFtp.setBackgroundResource(R.drawable.ftpon);
    }

    private void ftpOff() {
        mTvFtpdz.setText("Ftp服务器未启动！");
        mTvFtptt.setText("Ftp服务器状态：" + "关闭！");
        // Toast.makeText(getActivity(), "Ftp服务器已关闭！", Toast.LENGTH_SHORT).show();
        mBtFtp.setBackgroundResource(R.drawable.ftpoff);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("---------", "destory");
        if (null != mFtpServer) {
            mFtpServer.stop();
            mFtpServer = null;
        }
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }

    }


}
