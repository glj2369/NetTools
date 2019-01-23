package service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.example.glj23.finalftp.R;
import com.example.glj23.finalftp.TelnetActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Bean.TelHis;
import Util.TelnetUtil;

public class TelnetService extends Service {
    private ExecutorService singleThreadExecutor;
    private InputMethodManager imm;
    private TelnetUtil telnet;
    private TelBinder telBinder = new TelBinder();
    private TelHis tel;
    private LocalBroadcastManager localBroadcastManager;
    private TelBroad telBroad = new TelBroad();

    public TelnetService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(telBroad, new IntentFilter("tel"));
        Intent intent = new Intent(this, TelnetActivity.class);
        intent.putExtra("ftpServer", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Telnet已开启")
                .setContentText("Telnet客户端正在工作中")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.telneticon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.telnet
                ))
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "服务信息", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        startForeground(4, notification.build());


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return telBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tel = (TelHis) intent.getSerializableExtra("tel");
        singleThreadExecutor = Executors.newFixedThreadPool(3);
        if (telnet == null) {
            telnet = new TelnetUtil();
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    telnet.connect(tel.getIp(), Integer.parseInt(tel.getPort()), tel.getUser(), tel.getPsw());
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class TelBinder extends Binder {
        public TelnetUtil getTel() {
            if (telnet != null) {
                return telnet;
            }

            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (telnet != null && telnet.isComm()) {
            telnet.disconnect();
        }
        singleThreadExecutor.shutdown();//关闭线程
        localBroadcastManager.unregisterReceiver(telBroad);
        Log.e("---------", "Tel服务销毁！");
    }

    class TelBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String com = intent.getStringExtra("com");
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (com.equals(""))
                        return;
                    telnet.sendCommand(com);
                    // Toast.makeText(mContext, "发送！！！", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
