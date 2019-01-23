package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.example.glj23.finalftp.R;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Bean.IpHis;
import Bean.TracertBean;
import Util.FileUtils;
import Util.IPAddressUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TracertFragment extends Fragment implements View.OnClickListener {


    private View view;
    /**
     * 服务器返回信息
     */
    private TextView mTraTv;
    /**
     * 域名或ip地址
     */
    private AutoCompleteTextView mTrEt;
    /**
     * 开始
     */
    private Button mTrBt;
    // 最大的ttl跳转 可以自己设定
    private final int MAX_TTL = 30;

    // 都是一些字符串 用于parse 用的
    private static final String PING = "PING";
    private static final String FROM_PING = "From";
    private static final String SMALL_FROM_PING = "from";
    private static final String PARENTHESE_OPEN_PING = "(";
    private static final String PARENTHESE_CLOSE_PING = ")";
    private static final String TIME_PING = "time=";
    private static final String EXCEED_PING = "exceed";
    private static final String UNREACHABLE_PING = "100%";

    // 初始化默认ttl 为1
    private int ttl = 1;
    private String ipToPing;
    // ping耗时
    private float elapsedTime;

    // 存放结果集的tarces
    private List<TracertBean> traces = new ArrayList();
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<IpHis> all;
    private AlertDialog builder;
    private QMUITipDialog tipDialog;

    public TracertFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracert, container, false);
        initView(view);
        Dialog(true);
        String data = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NetToolDate/ipdata/qqwry.dat";
        File file = new File(data);
        Log.e("ftplog", data);
        if (!file.exists()) {
            copyFile();
            Log.e("ftplog", "文件复制完成");
        } else {
            Log.e("ftplog", "文件已经存在");
        }
        all = LitePal.order("id desc").find(IpHis.class);
        for (IpHis ipHis : all) {
            list.add(ipHis.getIpDomain());
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        mTrEt.setAdapter(adapter);
        return view;
    }

    private void initView(View view) {
        mTraTv = view.findViewById(R.id.tra_Tv);
        mTrEt = view.findViewById(R.id.tr_Et);
        mTrBt = view.findViewById(R.id.tr_Bt);
        mTrBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tr_Bt:


                if (RegexUtils.isIP(mTrEt.getText().toString().trim()) || isDomain(mTrEt.getText().toString().trim())) {
                    traces.clear();
                    IpHis ipHis = new IpHis();
                    ipHis.setIpDomain(mTrEt.getText().toString().trim());
                    ipHis.save();
                    adapter.add(mTrEt.getText().toString().trim());
                    tipDialog.show();
                    new ExecuteTracerouteAsyncTask(MAX_TTL, mTrEt.getText().toString()).execute();
                    System.out.println("-------------------------------------------------");
                    //System.out.println(new IPAddressUtils().getIp(getActivity(), ""));
                    System.out.println("-------------------------------------------------");
                } else {
                    builder = new AlertDialog.Builder(getContext())
                            .setTitle("警告！")
                            .setMessage("IP或域名输入错误，请重新输入！！")
                            .setIcon(R.drawable.warn)
                            .setPositiveButton("确定", null)
                            .setCancelable(false).create();
                    builder.getWindow().setWindowAnimations(R.style.mystyle);
                    builder.show();
                }


                break;
        }
    }

    public static boolean isDomain(final CharSequence input) {
        return isMatch("^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$", input);
    }

    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    private void Dialog(Boolean b) {
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.cancel();
        }
        if (b) {
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在加载")
                    .create();
        } else {
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                    .setTipWord("请求失败！")
                    .create();
        }

    }

    private void showResultInLog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TracertBean container : traces) {
            String[] split = container.toString().split(" ");
            Log.e("ftplog", split[1]);
            split[1] = " " + new IPAddressUtils().getIp(getActivity(), split[1]) + " ";
            for (String s : split) {
                stringBuilder.append(s);
            }
            stringBuilder.append("\n");
            Log.v("ccc", container.toString());
        }
        mTraTv.setText(stringBuilder);
        ttl = 1;
    }


    private class ExecuteTracerouteAsyncTask extends AsyncTask<Void, Void, String> {
        private int maxTtl;
        private String url;

        public ExecuteTracerouteAsyncTask(int maxTtl, String url) {
            this.maxTtl = maxTtl;
            this.url = url;
        }


        @Override
        protected String doInBackground(Void... params) {

            String res = "";
            try {
                res = launchPing(url);
            } catch (IOException e1) {
// TODO Auto-generated catch block
                e1.printStackTrace();
            }
            TracertBean trace;

            if (res.contains(UNREACHABLE_PING) && !res.contains(EXCEED_PING)) {
                trace = new TracertBean("", parseIpFromPing(res),
                        elapsedTime);
            } else {
                trace = new TracertBean("", parseIpFromPing(res),
                        ttl == maxTtl ? Float
                                .parseFloat(parseTimeFromPing(res))
                                : elapsedTime);
            }

            InetAddress inetAddr;
            try {
                inetAddr = InetAddress.getByName(trace.getIp());
                String hostname = inetAddr.getHostName();
                trace.setHostname(hostname);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            traces.add(trace);
            return res;
        }

        private String launchPing(String url) throws IOException {
            Process p;
            String command = "";

// 这个实际上就是我们的命令第一封装 注意ttl的值的变化 第一次调用的时候 ttl的值为1
            String format = "ping -c 1 -w 4 -t %d ";
            command = String.format(format, ttl);

            long startTime = System.nanoTime();
// 实际调用命令时 后面要跟上url地址
            p = Runtime.getRuntime().exec(command + url);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
// 这个地方这么做的原因是 有的手机 返回的from 有的手机返回的是From所以要
// 这么去判定 请求结束的事件 算一下 延时
                if (s.contains(FROM_PING) || s.contains(SMALL_FROM_PING)) {
                    elapsedTime = (System.nanoTime() - startTime) / 1000000.0f;
                }
            }
            Log.v("zzz", res);

// 调用结束的时候 销毁这个资源
            p.destroy();

            if (res.equals("")) {
                    return "";
              /*  getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog(false);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (tipDialog != null && tipDialog.isShowing()) {
                                    tipDialog.cancel();
                                }
                            }
                        }, 1500);

                    }
                });*/
            }
// 第一次调用ping命令的时候 记得把取得的最终的ip地址 赋给外面的ipToPing
// 后面要依据这个ipToPing的值来判断是否到达ip数据报的 终点
            if (ttl == 1) {
                ipToPing = parseIpToPingFromPing(res);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
// 如果为空的话就截止吧 过程完毕
            if (TextUtils.isEmpty(result)) {
                tipDialog.cancel();
                return;
            }

// 如果这一跳的ip地址与最终的地址 一致的话 就说明 ping到了终点
            if (traces.get(traces.size() - 1).getIp().equals(ipToPing)) {
                if (ttl < maxTtl) {
                    ttl = maxTtl;
                    traces.remove(traces.size() - 1);
                    new ExecuteTracerouteAsyncTask(maxTtl, url).execute();
                } else {
// 如果ttl ==maxTtl的话 当然就结束了 我们就要打印出最终的结果
                    showResultInLog();
                    tipDialog.cancel();
                }
            } else {
// 如果比较的ip 不相等 哪就说明还没有ping到最后一跳。我们就需要继续ping
// 继续ping的时候 记得ttl的值要加1
                if (ttl < maxTtl) {
                    ttl++;
                    new ExecuteTracerouteAsyncTask(maxTtl, url).execute();
                }
            }

            super.onPostExecute(result);
        }


    }


    private String parseIpFromPing(String ping) {
        String ip = "";
        if (ping.contains(FROM_PING)) {
            int index = ping.indexOf(FROM_PING);

            ip = ping.substring(index + 5);
            if (ip.contains(PARENTHESE_OPEN_PING)) {
                int indexOpen = ip.indexOf(PARENTHESE_OPEN_PING);
                int indexClose = ip.indexOf(PARENTHESE_CLOSE_PING);

                ip = ip.substring(indexOpen + 1, indexClose);
            } else {
                ip = ip.substring(0, ip.indexOf("\n"));
                if (ip.contains(":")) {
                    index = ip.indexOf(":");
                } else {
                    index = ip.indexOf(" ");
                }

                ip = ip.substring(0, index);
            }
        } else {
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);


           if (indexClose==-1&& indexOpen==-1){
                return "";

            }else {

               ip = ping.substring(indexOpen + 1, indexClose);
            }

        }

        return ip;
    }


    private String parseIpToPingFromPing(String ping) {
        String ip = "";
        if (ping.contains(PING)) {
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }


    private String parseTimeFromPing(String ping) {
        String time = "";
        if (ping.contains(TIME_PING)) {
            int index = ping.indexOf(TIME_PING);

            time = ping.substring(index + 5);
            index = time.indexOf(" ");
            time = time.substring(0, index);
        }

        return time;
    }


    private void copyFile() {
        FileUtils.getInstance(getContext()).copyAssetsToSD("ip", "/NetToolDate/ipdata/");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        if (tipDialog != null && tipDialog.isShowing()) {
            tipDialog.cancel();
        }

    }
}
