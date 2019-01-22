package com.example.glj23.finalftp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.glj23.finalftp.Fragment.AboutFragment;
import com.example.glj23.finalftp.Fragment.ArpFragment;
import com.example.glj23.finalftp.Fragment.FtpClientkFragment;
import com.example.glj23.finalftp.Fragment.FtpFragment;
import com.example.glj23.finalftp.Fragment.MainFragment;
import com.example.glj23.finalftp.Fragment.PingFragment;
import com.example.glj23.finalftp.Fragment.TelnetFragment;
import com.example.glj23.finalftp.Fragment.TracertFragment;
import com.example.glj23.finalftp.Fragment.WolFragment;
import com.example.glj23.finalftp.Fragment.ipCountFragment;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import Util.NetUtil;
import Util.SystemUtil;
import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    /**
     * 手机厂商
     */
    private TextView mPhone;
    /**
     * 192.168.1.1
     */
    private TextView mIpAddress;
    private ImageView mNetPic;
    /**
     * android 6.0
     */
    private TextView mAndroid;
    /**
     * wifi网络
     */
    private TextView mNetWork;
    private NetWorkReceiver netWorkReceiver;
    private IntentFilter intentFilter;
    private Boolean flag = true;
    private String[] perms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initXG();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkPer();


    }

    private void initXG() {
        XGPushConfig.enableDebug(this, true);
        Log.d("TPush", "设备token为：" + XGPushConfig.getToken(this));

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
//token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
        //XGPushManager.bindAccount(getApplicationContext(), "XINGE");
        //XGPushManager.setTag(this,"XINGE");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    public void initData() {
        String localIpAddress = getLocalIpAddress();
        mPhone.setText(SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel());
        mAndroid.setText("安卓" + SystemUtil.getSystemVersion());
        int apnType = NetUtil.getAPNType(this);
        if (apnType == 0) {
            mNetWork.setText("没有网络");
            //Toast.makeText(this, mNetWork.getText(), Toast.LENGTH_SHORT).show();
            mNetPic.setBackgroundResource(R.drawable.dw);
            mIpAddress.setText("当前无网络");
            ;
        } else if (apnType == 1) {
            mNetWork.setText(getString(R.string.wifi));
            mNetPic.setBackgroundResource(R.drawable.wifi);
            mIpAddress.setText(NetUtil.getLanIp(this));
            //Toast.makeText(this, mNetWork.getText(), Toast.LENGTH_SHORT).show();
        } else {
            mNetPic.setBackgroundResource(R.drawable.sj);
            mIpAddress.setText(NetUtil.getSjIp());
            if (apnType == 2) {
                mNetWork.setText(getString(R.string.two));
            } else if (apnType == 3) {
                mNetWork.setText(getString(R.string.three));
            } else if (apnType == 4) {
                mNetWork.setText(getString(R.string.four));
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ftp) {
            // Handle the camera action
            replaceFragment(new FtpFragment());
        } else if (id == R.id.nav_telnet) {
            replaceFragment(new TelnetFragment());
        } else if (id == R.id.nav_ping) {

            replaceFragment(new PingFragment());
        } else if (id == R.id.nav_tracert) {
            replaceFragment(new TracertFragment());
        } else if (id == R.id.nav_wol) {
            replaceFragment(new WolFragment());
        } else if (id == R.id.nav_main) {
            replaceFragment(new MainFragment());
        } else if (id == R.id.nav_arp) {
            replaceFragment(new ArpFragment());
        } else if (id == R.id.nav_ftpClient) {
            replaceFragment(new FtpClientkFragment());
        } else if (id == R.id.nav_ip) {
            replaceFragment(new ipCountFragment());
        } else if (id == R.id.nav_about) {
            replaceFragment(new AboutFragment());
        } else if (id == R.id.nav_link) {
            joinQQ();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 跳转QQ聊天界面
     */
    public void joinQQ() {
        try {
            //可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=1032573284";//uin是发送过去的qq号码
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "你竟然没安装QQ o_o", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toasty.normal(this, "你竟然没安装QQ o_o", Toast.LENGTH_SHORT, getDrawable(R.drawable.qq), true).show();
            }
        }
    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.part_layout, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netWorkReceiver);
        //XGPushManager.unregisterPush(this);
    }

    private void initView() {
        mPhone = (TextView) findViewById(R.id.phone);
        mIpAddress = (TextView) findViewById(R.id.ipAddress);
        mNetPic = (ImageView) findViewById(R.id.netPic);
        mAndroid = (TextView) findViewById(R.id.android);
        mNetWork = (TextView) findViewById(R.id.netWork);
        replaceFragment(new MainFragment());
        netWorkReceiver = new NetWorkReceiver();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, intentFilter);
        perms = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

    }

    @SuppressLint("LongLogTag")
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }

        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            if (getIntent().getIntExtra("flag", 0) == 1) {
                replaceFragment(new FtpClientkFragment());
            } else if (getIntent().getIntExtra("ftpServer", 0) == 1) {
                replaceFragment(new FtpFragment());
            } else if (getIntent().getIntExtra("telflag", 0) == 1) {
                replaceFragment(new TelnetFragment());
            }
            flag = false;
        }


    }

    public void checkPer() {
        if (!EasyPermissions.hasPermissions(this, perms)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                EasyPermissions.requestPermissions(this, "请同意^_^", 1, perms);
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort("用户授权成功");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort("用户授权失败！");

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            new AppSettingsDialog.Builder(this).setTitle("权限重要o_o").setRationale("麻烦同意一下～～").build().show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                //checkPer();
                //ToastUtils.showShort("设置返回！");

                if (EasyPermissions.hasPermissions(this, perms)) {
                    ToastUtils.showShort("用户授权成功！");
                } else {
                    ToastUtils.showShort("用户授权失败！");
                }
                break;
        }
    }

    class NetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
}



