package service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.glj23.finalftp.FtpClientActivity;
import com.example.glj23.finalftp.R;

import java.io.File;

import Bean.FtpBean;
import Util.FtpCli;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;

public class FtpClientService extends Service {
    private FtpClientService.FtpBinder ftpBinder = new FtpClientService.FtpBinder();
    private FtpCli ftpCli;
    private FtpBean ftp;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent1 = new Intent(this, FtpClientActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Ftp客户端已开启")
                .setContentText("Ftp客户端正在连接中")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ftpclient)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ftpcli
                ))
                .setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "服务信息", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        startForeground(1, notification.build());


        Log.e("---------", "ftp服务创建");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("---------", Thread.currentThread().getName());
        SharedPreferences ftpCli = getSharedPreferences("ftpCli", MODE_PRIVATE);
        final String ip = ftpCli.getString("ip", "");
        final String port = ftpCli.getString("port", "");
        final String user = ftpCli.getString("user", "");
        final String psw = ftpCli.getString("psw", "");

        if (this.ftpCli == null) {
            Log.e("-----", "intent 尝试连接ftp");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("---------", Thread.currentThread().getName() + "开始");
                    FtpClientService.this.ftpCli = new FtpCli(ip, Integer.parseInt(port), user, psw);
                    Log.e("---------", Thread.currentThread().getName() + "结束");

                }
            }).start();
            Log.e("---------", Thread.currentThread().getName());
            Log.e("-----", "intent 尝试连接ftp结束");

        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return ftpBinder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ftpCli != null && ftpCli.getIsConnect()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ftpCli.logout();
                }
            }).start();

        }
        Log.e("----------", "ftp服务销毁");
    }
    public class FtpBinder extends Binder {

        FtpBinder() {
        }

        public FtpCli LoginFtp(final FtpBean ftpBean) {
            ftpCli = new FtpCli(ftpBean.getUrl(), ftpBean.getPort(), ftpBean.getUser(), ftpBean.getPsw());
            return ftpCli;
        }

        public FTPClient getFtp() {
            if (ftpCli != null) {

                Log.e("------------", " return ftpCli");
                return ftpCli.getFTPInstance();
            } else {
                //new LoginFtp().execute(ftpBean);
                return null;
            }
        }

        public FTPFile[] getFtpFile(final String s) {
            if (ftpCli != null) {
                return ftpCli.getFileList(s);
            }
            return new FTPFile[0];
        }

        public void FtpDown(final String remopath, final String localpath, final FTPDataTransferListener ftpDataTransferListener) {

            if (ftpCli != null) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ftpCli.FTPdownload(remopath, localpath, ftpDataTransferListener);
                    }
                }).start();


            }
        }

        public void upLoad(final File srcfile, final String path, final FTPDataTransferListener UpLoadListener) {
            if (ftpCli != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ftpCli.FTPupload(srcfile, path, UpLoadListener);
                    }
                }).start();
            }

        }

        public void del(String path, int type) {
            if (ftpCli != null) {
                if (type == 1) {
                    ftpCli.deleteFolder(path);
                } else {
                    ftpCli.deleteFile(path);
                }


            }
        }

        public void changeDir(final String s) {
            if (ftpCli != null) {
                ftpCli.changeDir(s);
            }
        }

        public String getCurrent() {

            if (ftpCli != null) {
                return ftpCli.getCurPath();
            }
            return "";
        }

        public void upDir() {

            if (ftpCli != null) {
                ftpCli.changeUpDir();

            }
        }

        public void newdir(String s) {
            if (ftpCli != null) {
                ftpCli.NewFolder(s);
            }
        }

        public void reName(String old, String ne) {
            if (ftpCli != null) {
                ftpCli.RenameFTPfile(old, ne);
            }
        }
    }


}

