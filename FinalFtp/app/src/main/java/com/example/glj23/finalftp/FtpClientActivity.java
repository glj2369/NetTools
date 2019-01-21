package com.example.glj23.finalftp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Bean.FtpFileBean;
import adapter.FtpListAdapter;
import dev.utils.app.UriUtils;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;
import service.FtpClientService;

public class FtpClientActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mFtpList;
    private List<FtpFileBean> list = new ArrayList<FtpFileBean>();
    private FtpListAdapter adapter;
    private FtpClientService.FtpBinder ftpBinder;
    private Boolean flag1 = false;
    private int currType;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ftpBinder = (FtpClientService.FtpBinder) service;
            FTPClient ftp = ftpBinder.getFtp();
            Log.e("-------------", "ServiceConnected");
            if (ftp != null) {
                tipDialog("正在加载", 3);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final FTPFile[] ftpFile = ftpBinder.getFtpFile("/");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    upDateList(ftpFile, "/");
                                    tipDialog.cancel();
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } catch (Exception e) {
                            Log.e("!!!!!!!!", e.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error("Ftp服务器获取错误,请重新尝试！");
                                    builder.show();
                                }
                            });


                        }
                    }
                }).start();

            } else {
                error("Ftp服务器错误，请重新尝试！！");
                builder.show();

            }

        }

    };
    private AlertDialog alertDialog1;
    private FloatingActionButton mFloataction;
    private View view;
    private NotificationManager notificationManager;
    private QMUIDialog longclick;
    private View view1;
    private String delname;
    private Button delBt;
    private String current;
    private View nameBt;
    private QMUITipDialog tipDialog;
    private String longname;

    private void upDateList(FTPFile[] ftpFile, String path) {
        if (ftpFile == null) {
            error("Ftp服务器错误，请重新尝试！");
            builder.show();
        } else {
            list.clear();
            if (!path.equals("/")) {
                FtpFileBean ftpFileBean = new FtpFileBean();
                ftpFileBean.setName("...");
                ftpFileBean.setIcon(R.drawable.file);
                ftpFileBean.setTime("父目录");
                ftpFileBean.setDir(2);
                list.add(ftpFileBean);
            }

            for (int i = 0; i < ftpFile.length; i++) {

                FtpFileBean ftpFileBean = new FtpFileBean();
                ftpFileBean.setName(ftpFile[i].getName());
                ftpFileBean.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ftpFile[i].getModifiedDate()));
                if (ftpFile[i].getType() == 1) {
                    ftpFileBean.setIcon(R.drawable.file);
                    ftpFileBean.setSize("");
                    ftpFileBean.setDir(1);
                } else {
                    ftpFileBean.setDir(0);
                    ftpFileBean.setSize(ConvertUtils.byte2FitMemorySize(ftpFile[i].getSize()));
                    ftpFileBean.setOldSize(ftpFile[i].getSize());
                    Log.e("-------", FileUtils.getFileExtension(ftpFile[i].getName()));
                    String s = FileUtils.getFileExtension(ftpFile[i].getName()).toLowerCase();
                    if (isMusic(s)) {
                        ftpFileBean.setIcon(R.drawable.music);
                    } else if (isPic(s)) {
                        ftpFileBean.setIcon(R.drawable.pic);
                    } else if (isVideo(s)) {
                        ftpFileBean.setIcon(R.drawable.video);
                    } else if (isZip(s)) {
                        ftpFileBean.setIcon(R.drawable.ys);
                    } else if (s.equals("doc") || s.equals("docx")) {
                        ftpFileBean.setIcon(R.drawable.word);
                    } else if (s.equals("xls") || s.equals("xlsx")) {
                        ftpFileBean.setIcon(R.drawable.excel);
                    } else if (s.equals("ppt") || s.equals("pptx")) {
                        ftpFileBean.setIcon(R.drawable.ppt);
                    } else if (s.equals("pdf")) {
                        ftpFileBean.setIcon(R.drawable.pdf);
                    } else if (s.equals("txt")) {
                        ftpFileBean.setIcon(R.drawable.txt);
                    } else {
                        ftpFileBean.setIcon(R.drawable.other);
                    }
                }
                list.add(ftpFileBean);

            }
        }

    }

    private Intent intent1;
    private AlertDialog builder;
    private AlertDialog alertDialog;

    public static boolean isZip(final CharSequence input) {
        return isMatch("^zip|iso|7z|rar$", input);
    }

    public static boolean isMusic(final CharSequence input) {
        return isMatch("^mp3|wav|wma|ogg|ape|acc|flac$", input);
    }

    public static boolean isPic(final CharSequence input) {
        return isMatch("^gif|jpg|jpeg|bmp|png$", input);
    }

    public static boolean isVideo(final CharSequence input) {
        return isMatch("^swf|flv|mp4|rmvb|avi|mpeg|ra|ram|mov|wmv$", input);
    }

    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    private void error(String s) {
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        builder = new AlertDialog.Builder(FtpClientActivity.this)
                .setTitle("警告！")
                .setMessage(s)
                .setIcon(R.drawable.warn)
                .setPositiveButton("重新尝试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FtpClientActivity.this, MainActivity.class);
                        intent.putExtra("flag", 1);
                        finish();
                        startActivity(intent);
                    }
                })
                .setCancelable(false).create();
        builder.getWindow().setWindowAnimations(R.style.mystyle);


    }

    private Toolbar mFtpToolbar;
    private Intent intent;
    long progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_client);
        initView();
        setSupportActionBar(mFtpToolbar);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mFtpList.setAdapter(adapter);
        Log.e("-----------", "ftp传递");
        intent = new Intent(this, FtpClientService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        bindService(intent, connection, BIND_AUTO_CREATE);
        flag1 = true;

        Log.e("--------------", "onCreat");
        mFtpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (list.get(position).getDir() == 1) {
                    Toast.makeText(FtpClientActivity.this, "我是一个文件夹^_^", Toast.LENGTH_SHORT).show();
                    if (ftpBinder != null) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("-----------", ftpBinder.getCurrent() + "");
                                if (ftpBinder.getCurrent().equals("/")) {
                                    final FTPFile[] ftpFile = ftpBinder.getFtpFile(ftpBinder.getCurrent() + list.get(position).getName() + File.separator);
                                    final String s = ftpBinder.getCurrent() + list.get(position).getName() + File.separator;
                                    //ftpBinder.changeDir(ftpBinder.getCurrent() + list.get(position).getName() + "/");

                                    // upDateList(ftpBinder.getFtpFile("/1/"));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            upDateList(ftpFile, s);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                } else {
                                    //ftpBinder.changeDir(ftpBinder.getCurrent() + "/" + list.get(position).getName() + "/");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final FTPFile[] ftpFile = ftpBinder.getFtpFile(ftpBinder.getCurrent() + File.separator + list.get(position).getName() + File.separator);
                                            final String s = ftpBinder.getCurrent() + File.separator + list.get(position).getName() + File.separator;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    upDateList(ftpFile, s);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    }).start();

                                }
                            }
                        }).start();
                    } else {
                        Log.e("------", "ftpBinder为空");
                    }


                } else if (list.get(position).getDir() == 0) {

                    alertDialog1 = new AlertDialog.Builder(FtpClientActivity.this).setTitle("文件下载提醒").setMessage("是否开始下载?").setIcon(R.drawable.wh).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fileDown(position);
                        }
                    }).setCancelable(false).create();
                    alertDialog1.getWindow().setWindowAnimations(R.style.mystyle);
                    alertDialog1.show();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ftpBinder.upDir();

                            final String current = ftpBinder.getCurrent();
                            final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    upDateList(ftpFile, current);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();

                }
            }

        });
        mFtpList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                ViewGroup parents = (ViewGroup) view1.getParent();
                if (parents != null) {
                    parents.removeAllViews();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        current = ftpBinder.getCurrent();
                        Log.e("-----------------", current);
                        if (!current.equals("/")) {
                            if (position != 0) {
                                longname = list.get(position).getName();
                                delname = current + File.separator + longname;
                                currType = list.get(position).getDir();
                                delDialog();
                            }

                        } else {
                            longname = list.get(position).getName();
                            delname = current + longname;
                            currType = list.get(position).getDir();
                            delDialog();
                        }


                    }
                }).start();
                return true;
            }
        });
    }

    private void delDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (longclick != null && longclick.isShowing()) {
                    longclick.cancel();
                }
                longclick = new QMUIDialog(FtpClientActivity.this);
                longclick.setContentView(view1);
                longclick.show();
            }
        });

    }

    private void fileDown(int position) {
        final long fileSize = list.get(position).getOldSize();
        String dir = Environment.getExternalStorageDirectory()
                + "/FtpDownload/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String path = dir + list.get(position).getName();
        final File file = new File(path);
        if (file.exists()) {
            file.delete();
            Log.i("delete", "original file deleted");
        }

        ftpBinder.FtpDown(list.get(position).getName(), path, new MyListener("down", fileSize));
    }

    class MyListener implements FTPDataTransferListener {
        private String s;
        private long fileSize;

        public MyListener() {
        }

        public MyListener(String s, long fileSize) {
            this.s = s;
            this.fileSize = fileSize;
            progress = 0;
        }

        @Override
        public void started() {
            if (s.equals("down")) {
                notificationManager.notify(2, getNotification("开始下载", 0));
            } else {
                notificationManager.notify(2, getNotification("开始上传", 0));
            }

        }

        @Override
        public void transferred(int i) {
            Log.e("----------", i + "");
            int length = (int) ((progress += i) * 100 / fileSize);
            Log.e("----------", progress + " " + length);
            notificationManager.notify(2, getNotification("请稍后", length));
        }

        @Override
        public void completed() {
            if (s.equals("down")) {
                notificationManager.notify(2, getNotification("下载完成！", 100));
                tipDialog("下载完成!", 1);
            } else {
                notificationManager.notify(2, getNotification("上传完成！", 100));
                tipDialog("上传完成!", 1);
                resh();
            }

        }

        @Override
        public void aborted() {
            if (s.equals("down")) {
                tipDialog("下载失败!", 0);

            } else {
                tipDialog("上传失败!", 0);
            }
        }

        @Override
        public void failed() {
            if (s.equals("down")) {
                tipDialog("下载失败!", 0);

            } else {
                tipDialog("上传失败!", 0);
            }
        }
    }

    private void tipDialog(final String s, final int b) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tipDialog != null && tipDialog.isShowing()) {
                    tipDialog.cancel();
                }
                if (b == 1) {
                    tipDialog = new QMUITipDialog.Builder(FtpClientActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                            .setTipWord(s)
                            .create();
                } else if (b == 0) {
                    tipDialog = new QMUITipDialog.Builder(FtpClientActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                            .setTipWord(s)
                            .create();
                } else {
                    tipDialog = new QMUITipDialog.Builder(FtpClientActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(s)
                            .create();
                }
                tipDialog.show();
            }
        });
        if (b == 1 || b == 0) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tipDialog != null && tipDialog.isShowing()) {
                        tipDialog.cancel();
                    }
                }
            }, 1000);
        }


    }


    public Notification getNotification(String title, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }


    private void initView() {
        mFtpList = (ListView) findViewById(R.id.ftp_list);
        adapter = new FtpListAdapter(this, R.layout.ftpclistitem, list);
        mFtpToolbar = (Toolbar) findViewById(R.id.ftp_toolbar);
        mFloataction = (FloatingActionButton) findViewById(R.id.floataction);
        mFloataction.setOnClickListener(this);
        view1 = LayoutInflater.from(this).inflate(R.layout.ftpdel, null);

        delBt = view1.findViewById(R.id.ftp_del);
        delBt.setOnClickListener(this);
        nameBt = view1.findViewById(R.id.ftp_rename);
        nameBt.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("-----------", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("-----------", "FtpActivity 销毁");
        if (connection != null) {
            if (flag1) {
                unbindService(connection);
                stopService(intent);
                flag1 = false;
            }

        }
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        if (alertDialog1 != null && alertDialog1.isShowing()) {
            alertDialog.cancel();
        }
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.cancel();
        }
        if (longclick != null && longclick.isShowing()) {
            longclick.cancel();
        }


    }


    /**
     * 返回键不销毁活动
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.floataction:
                showSimpleBottomSheetGrid();
                break;
            case R.id.ftp_del:
                longclick.cancel();
                showMessageNegativeDialog();
                break;
            case R.id.ftp_rename:
                longclick.cancel();
                Log.e("------", "ftp_rename");
                showEditTextDialog(longname);

                break;
        }
    }

    private void showEditTextDialog(final String s) {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("请输入名称");
        builder.setPlaceholder("在此输入名称");
        builder.setInputType(InputType.TYPE_CLASS_TEXT);
        if (!s.equals("new")) {
            builder.setDefaultText(s);
        }
        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        Toast.makeText(FtpClientActivity.this, text, Toast.LENGTH_SHORT).show();
                        if (text != null && text.length() > 0) {
                            if (s.equals("new")) {
                                fTPnewDir(text.toString().trim());
                            } else {
                                ftpRename(text);
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(FtpClientActivity.this, "请填入名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create().show();
    }


    private void showMessageNegativeDialog() {
        new QMUIDialog.MessageDialogBuilder(FtpClientActivity.this)
                .setTitle("删除确认")
                .setMessage("确定要删除吗？")

                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ftpBinder.del(delname, currType);
                                final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        upDateList(ftpFile, current);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void showSimpleBottomSheetGrid() {
        final int TAG_1 = 0;
        final int TAG_2 = 1;
        final int TAG_3 = 2;
        final int TAG_4 = 3;
        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(FtpClientActivity.this);
        builder.addItem(R.drawable.ftpnew, "新建文件夹", TAG_1, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.ftpfile, "本地上传", TAG_2, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.sx, "刷新", TAG_3, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.exit, "退出", TAG_4, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView) {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_1:
                                showEditTextDialog("new");
                                break;
                            case TAG_2:
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("*/*");
                                startActivityForResult(intent, 1);
                                break;
                            case TAG_3:
                                resh();
                                tipDialog("刷新完成!", 1);
                                break;
                            case TAG_4:
                                alertDialog = new AlertDialog.Builder(FtpClientActivity.this).setCancelable(false).setTitle("关闭ftp服务").setMessage("是否关闭?").setPositiveButton("确实", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(FtpClientActivity.this, MainActivity.class);
                                        intent.putExtra("flag", 1);
                                        finish();
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).setIcon(R.drawable.wh).create();
                                alertDialog.getWindow().setWindowAnimations(R.style.mystyle);
                                alertDialog.show();

                                break;
                        }
                    }
                }).build().show();


    }

    private void resh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String current = ftpBinder.getCurrent();
                final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upDateList(ftpFile, current);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void ftpRename(final CharSequence text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ftpBinder.reName(delname, text + "");
                final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upDateList(ftpFile, current);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void fTPnewDir(final String name) {
        Log.e("!!", "fTPnewDir");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String newdirpath;
                final String current = ftpBinder.getCurrent();
                if (current.equals("/")) {
                    newdirpath = current + name;
                } else {
                    newdirpath = current + File.separator + name;
                }
                Log.e("!!!", current);
                Log.e("!!!", newdirpath);
                ftpBinder.newdir(newdirpath);
                final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upDateList(ftpFile, current);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (data != null) {
                Uri uri = data.getData();
                final String path = UriUtils.getFilePathByUri(this, uri);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String current = ftpBinder.getCurrent();
                        ftpBinder.upLoad(new File(path), current, new MyListener("up", dev.utils.common.FileUtils.getFileLength(path)));
                        final FTPFile[] ftpFile = ftpBinder.getFtpFile(current);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                upDateList(ftpFile, current);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }

        }
    }
}
