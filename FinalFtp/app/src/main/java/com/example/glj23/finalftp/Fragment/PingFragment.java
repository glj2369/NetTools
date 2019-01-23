package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.example.glj23.finalftp.R;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Bean.IpHis;

/**
 * A simple {@link Fragment} subclass.
 */
public class PingFragment extends Fragment implements View.OnClickListener {


    private View view;
    private TextView mPingTv;
    /**
     * 5
     */
    private EditText mPingTimeEt;
    /**
     * 4
     */
    private EditText mPingCountEt;
    /**
     * 64
     */
    private EditText mPingDataEt;
    /**
     * 主机名或域名
     */
    private AutoCompleteTextView mPingIpEt;
    /**
     * Ping
     */
    private Button mPingStartBt;
    String s[] = new String[4];
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<IpHis> all;
    private AlertDialog builder;
    private QMUITipDialog tipDialog;

    public PingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ping, container, false);
        initView(view);
        Dialog();
        all = LitePal.order("id desc").find(IpHis.class);
        for (IpHis ipHis : all) {
            list.add(ipHis.getIpDomain());
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        mPingIpEt.setAdapter(adapter);
        return view;
    }


    private void Dialog() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载")
                .create();
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

    public String[] Ping(String count, String time, String data, String str) {
        String resault = "";
        Process p;
        String s[] = new String[Integer.parseInt(count) + 6];
        int i = 0;
        StringBuffer buffer = null;
        try {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -w " + time + " -s " + data + " -c " + count + " " + str);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            buffer = new StringBuffer();
//            String line = "";
            // Log.e("----------------", in.readLine());
            while ((s[i] = in.readLine()) != null) {
                System.out.println("-----------------------------------");
                //System.out.println(line);
                i++;
//                buffer.append(line);
            }
            //System.out.println("Return ============" + buffer.toString());

//            if (status == 0) {
//                resault = "success";
//            } else {
//                resault = "faild";
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return s;
    }

    private void initView(View view) {
        mPingTv = view.findViewById(R.id.ping_tv);
        mPingTimeEt = view.findViewById(R.id.ping_timeEt);
        mPingCountEt = view.findViewById(R.id.ping_countEt);
        mPingDataEt = view.findViewById(R.id.ping_dataEt);
        mPingIpEt = view.findViewById(R.id.ping_ipEt);
        mPingStartBt = view.findViewById(R.id.ping_startBt);
        mPingStartBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ping_startBt:

                if (RegexUtils.isIP(mPingIpEt.getText().toString().trim()) || isDomain(mPingIpEt.getText().toString().trim())) {
                    s[0] = mPingCountEt.getText().toString().trim();
                    s[1] = mPingTimeEt.getText().toString().trim();
                    s[2] = mPingDataEt.getText().toString().trim();
                    s[3] = mPingIpEt.getText().toString().trim();
                    IpHis ipHis = new IpHis();
                    ipHis.setIpDomain(mPingIpEt.getText().toString().trim());
                    ipHis.save();
                    adapter.add(mPingIpEt.getText().toString().trim());
                    //UpTextView();
                    new NetPing().execute(s);
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


    private class NetPing extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tipDialog.show();
                }
            });
            String[] s = new String[]{};
            s = Ping(params[0], params[1], params[2], params[3]);
            //Log.i("ping", s);
            publishProgress(s);

            return s;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            StringBuilder s = new StringBuilder();
            //mPingTv.setText(values[0]);

            for (int i = 0; i < values.length - 1; i++) {
                if (values[i] != null) {
                    s.append(values[i] + "\n");

                }
                mPingTv.setText(s);
                System.out.println(values[i]);
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            tipDialog.cancel();
            super.onPostExecute(strings);
        }
    }


}
