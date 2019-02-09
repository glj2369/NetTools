package com.example.glj23.finalftp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import Bean.TelHis;
import Util.TelnetUtil;
import es.dmoral.toasty.Toasty;
import service.TelnetService;

public class TelnetActivity extends AppCompatActivity implements View.OnClickListener, TelnetUtil.GetMessageListener {

    /**
     * 服务器返回信息
     */
    private TextView mTelnetTv;
    private ScrollView mTelnetScrow;
    /**
     * 编辑命令
     */
    private EditText mTelnetEt;
    /**
     * 发送命令
     */
    private Button mTelnetSend;
    /**
     * 发送命令
     */
    private Button mTelnetExit;
    private InputMethodManager imm;
    private TelnetUtil telnet;
    private AlertDialog builder;
    private Toolbar mTelToolbar;
    private TelHis tel;
    private Boolean flag = false;
    private TelnetService.TelBinder telBinder;
    private LocalBroadcastManager localBroadcastManager;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            telBinder = (TelnetService.TelBinder) service;
            telBinder.conn();
            telnet = telBinder.getTel();
            telnet.setListener(TelnetActivity.this);
            if (telnet == null) {
                Log.e("----", "tel连接");
                error("连接错误,请重新尝试!");
                builder1.show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Intent intent;
    private AlertDialog builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telnet);
        initView();
        setSupportActionBar(mTelToolbar);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        intent = new Intent(this, TelnetService.class);
        tel = (TelHis) getIntent().getSerializableExtra("tel");
        intent.putExtra("tel", tel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            flag = true;
        } else {
            startService(intent);
            flag = true;
        }
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        //imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


    }
    private void initView() {
        mTelnetTv = findViewById(R.id.telnet_Tv);
        mTelnetScrow = findViewById(R.id.telnet_scrow);
        mTelnetEt = findViewById(R.id.telnet_Et);
        mTelnetSend = findViewById(R.id.telnet_send);
        mTelnetSend.setOnClickListener(this);
        mTelnetExit = findViewById(R.id.telnet_exit);
        mTelnetExit.setOnClickListener(this);
        mTelnetTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTelToolbar = findViewById(R.id.tel_toolbar);
        dialog();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        telnet.disconnect();
        if (flag) {
            if (serviceConnection != null) {
                stopService(intent);
                unbindService(serviceConnection);
                flag = false;
            }
        }
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        if (builder1 != null && builder1.isShowing()) {
            builder1.cancel();
        }
        Log.e("---------", "Tel活动销毁！");
    }
    @Override
    public void onMessage(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (info != null) {
                    String s = mTelnetTv.getText().toString();
                    String del = del(s + "\n" + info);
                    mTelnetTv.setText(del);
                    Log.e("Telnet", s);
                    mTelnetScrow.post(new Runnable() {
                        @Override
                        public void run() {
                            mTelnetScrow.smoothScrollTo(0, mTelnetTv.getBottom());
                        }
                    });

                } else {
                    // Log.e("Telnet", "空！！！");
                }
            }
        });
    }
    /**
     * 过滤多余字符
     *
     * @param s
     * @return
     */
    private String del(String s) {
        String all = s.replaceAll("\\[1;34m|\\[0m|\\[36;1m|\\[35;1m|\\[1;36|\\]0|\\[m|;", "");
        return all;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.telnet_send:
                String send = mTelnetEt.getText().toString();
                //imm.hideSoftInputFromWindow(mTelnetEt.getWindowToken(), 0);
                mTelnetEt.setText("");
                localBroadcastManager.sendBroadcast(new Intent("tel").putExtra("com", send));
                if (send.trim().equals("exit")) {
                    //Toast.makeText(this, "Telnet客户端运行结束!", Toast.LENGTH_SHORT).show();
                    Toasty.info(this, "Telnet客户端运行结束!").show();
                    finish();
                }
                break;
            case R.id.telnet_exit:
                exit();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void exit() {
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        builder.show();
    }
    private void dialog() {
        if (builder == null) {
            builder = new AlertDialog.Builder(this).setTitle("退出?").setMessage("是否断开连接o_o").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (flag) {
                        if (serviceConnection != null) {
                            stopService(intent);
                            unbindService(serviceConnection);
                            flag = false;
                        }
                    }
                    finish();
                }
            }).setIcon(R.drawable.wh).create();
            builder.getWindow().setWindowAnimations(R.style.mystyle);
        }

    }
    private void error(String s) {
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        builder1 = new AlertDialog.Builder(TelnetActivity.this)
                .setTitle("警告！")
                .setMessage(s)
                .setIcon(R.drawable.warn)
                .setPositiveButton("重新尝试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TelnetActivity.this, MainActivity.class);
                        intent.putExtra("telflag", 1);
                        finish();
                        startActivity(intent);
                    }
                })
                .setCancelable(false).create();
        builder1.getWindow().setWindowAnimations(R.style.mystyle);

    }
}
