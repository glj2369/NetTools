package service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.glj23.finalftp.MainActivity;
import com.example.glj23.finalftp.R;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FtpService extends Service {
    private FtpServer mFtpServer;
    private LocalBroadRe localBroadRe;
    private LocalBroadcastManager localBroadcastManager;

    public FtpService() {
    }


    void start() {
        SharedPreferences ftpServer = getSharedPreferences("FtpServer", MODE_PRIVATE);
        String username = ftpServer.getString("user", "admin");
        String psw = ftpServer.getString("psw", "123");
        String port = ftpServer.getString("port", "2222");

        Log.e("-------", "Ftp开始start");
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        /*PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        String[] str = {"mkdir", ftpConfigDir};
        try {
            Process ps = Runtime.getRuntime().exec(str);
            try {
                ps.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = ftpConfigDir + "users.properties";
        File files = new File(filename);
        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());*/
        //设置监听端口
        factory.setPort(Integer.parseInt(port));

        //替换默认监听
        serverFactory.addListener("default", factory.createListener());

        //用户名
        BaseUser user = new BaseUser();
        user.setName(username);
        //密码 如果不设置密码就是匿名用户
        user.setPassword(psw);
        //用户主目录
        user.setHomeDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
        List<Authority> authorities = new ArrayList<Authority>();
        //增加写权限
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        //增加该用户
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "ftp!!!!", Toast.LENGTH_SHORT).show();
        try {
            serverFactory.addListener("default", factory.createListener());
            FtpServer server = serverFactory.createServer();
            this.mFtpServer = server;
            server.start();
        } catch (FtpException e) {
            stopSelf();
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ftpServer", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Ftp服务器已开启")
                .setContentText("Ftp服务器正在工作中")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ftpserver)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ftpser
                ))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "服务信息", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        startForeground(3, notification.build());

        localBroadRe = new LocalBroadRe();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localBroadRe, new IntentFilter("stopftp"));

        Log.e("---------", "ftp服务器创建");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("----------", "onStartCommand");
        if (mFtpServer == null || mFtpServer.isStopped()) {
            start();
        }
        ftpSh(true);
        return super.onStartCommand(intent, flags, startId);
    }

    private void ftpSh(boolean b) {
        SharedPreferences.Editor ftp = getSharedPreferences("ftp", MODE_PRIVATE).edit();
        ftp.putBoolean("start", b);
        ftp.apply();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ftpSh(false);
        Log.e("----------", "onDestroy");
        if (mFtpServer != null) {
            mFtpServer.stop();
        }
        localBroadcastManager.unregisterReceiver(localBroadRe);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class LocalBroadRe extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "服务关闭", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }
}
