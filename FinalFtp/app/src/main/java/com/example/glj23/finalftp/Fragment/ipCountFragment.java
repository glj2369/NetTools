package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.glj23.finalftp.R;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Bean.DomainBean;
import Bean.IpHis;
import dev.utils.app.NetWorkUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * A simple {@link Fragment} subclass.
 */
public class ipCountFragment extends Fragment implements View.OnClickListener {


    private View view;
    /**
     * 重置
     */
    private Button mResBt;
    /**
     * 确定
     */
    private Button mCountBt;
    /**
     * 域名
     */
    private AutoCompleteTextView mIpDomain;
    /**
     * IP
     */
    private TextView mIpIp;
    /**  */
    private TextView mIpNature;
    /**  */
    private TextView mIpIcp;
    /**  */
    private TextView mIpIndexUrl;
    /**  */
    private TextView mIpSitename;
    /**  */
    private TextView mIpBadomain;
    /**  */
    private TextView mIpNowIcp;
    /**  */
    private TextView mIpCheckDate;
    /**  */
    private TextView mIpName;
    private LinearLayout mDomainLayout;
    /**
     * error
     */
    private TextView mError;
    private LinearLayout mDimainError;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<IpHis> all;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mDimainError.setVisibility(View.GONE);
                    mDomainLayout.setVisibility(View.VISIBLE);
                    DomainBean bean = (DomainBean) msg.obj;
                    mIpNature.setText(bean.getNature());
                    mIpIcp.setText(bean.getIcp());
                    mIpIndexUrl.setText(bean.getIndexUrl());
                    mIpSitename.setText(bean.getSitename());
                    mIpBadomain.setText(bean.getDomain());
                    mIpNowIcp.setText(bean.getNowIcp());
                    mIpCheckDate.setText(bean.getCheckDate());
                    mIpName.setText(bean.getName());
                    break;
                case 1:
                    DomainBean bean1 = (DomainBean) msg.obj;
                    mError.setText(bean1.getMessage());
                    mDomainLayout.setVisibility(View.GONE);
                    mDimainError.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private AlertDialog builder;

    public ipCountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ip_count, container, false);
        initView(view);
        mDimainError.setVisibility(View.GONE);
        Log.e("---", NetWorkUtils.getDomainAddress("baidu.com"));

        all = LitePal.order("id desc").find(IpHis.class);
        for (IpHis ipHis : all) {
            list.add(ipHis.getIpDomain());
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        mIpDomain.setAdapter(adapter);
        return view;
    }


    private void initView(View view) {

        mResBt = view.findViewById(R.id.resBt);
        mResBt.setOnClickListener(this);
        mCountBt = view.findViewById(R.id.countBt);
        mCountBt.setOnClickListener(this);
        mIpDomain = view.findViewById(R.id.ip_domain);
        mIpIp = view.findViewById(R.id.ip_ip);
        mIpNature = view.findViewById(R.id.ip_nature);
        mIpIcp = view.findViewById(R.id.ip_icp);
        mIpIndexUrl = view.findViewById(R.id.ip_indexUrl);
        mIpSitename = view.findViewById(R.id.ip_sitename);
        mIpBadomain = view.findViewById(R.id.ip_badomain);
        mIpNowIcp = view.findViewById(R.id.ip_nowIcp);
        mIpCheckDate = view.findViewById(R.id.ip_checkDate);
        mIpName = view.findViewById(R.id.ip_name);
        mDomainLayout = view.findViewById(R.id.domainLayout);
        mError = view.findViewById(R.id.error);
        mDimainError = view.findViewById(R.id.dimainError);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

            case R.id.resBt:
                mIpDomain.setText("");
                break;
            case R.id.countBt:
                String s = mIpDomain.getText().toString().trim();
                if (isDomain(s)) {
                    getInfo(s);
                    mIpIp.setText(NetWorkUtils.getDomainAddress(s));
                    IpHis ipHis = new IpHis();
                    ipHis.setIpDomain(mIpDomain.getText().toString().trim());
                    ipHis.save();
                    adapter.add(mIpDomain.getText().toString().trim());
                } else {

                    mIpDomain.setText("");

                    builder = new AlertDialog.Builder(getContext())
                            .setTitle("警告！")
                            .setMessage("域名输入错误，请重新输入！！")
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

    public void getInfo(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request build = new Request.Builder().url("https://www.sojson.com/api/beian/" + s).addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36").build();
                try {
                    String string = okHttpClient.newCall(build).execute().body().string();
                    Gson gson = new Gson();
                    DomainBean bean = gson.fromJson(string, DomainBean.class);
                    Message message = new Message();
                    message.obj = bean;
                    if (bean.getType() == 200) {
                        message.what = 0;
                    } else {
                        message.what = 1;
                    }
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
