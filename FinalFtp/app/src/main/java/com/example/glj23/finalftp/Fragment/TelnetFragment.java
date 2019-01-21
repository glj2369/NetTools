package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.example.glj23.finalftp.R;
import com.example.glj23.finalftp.TelnetActivity;
import com.yyydjk.library.DropDownMenu;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Bean.TelHis;
import adapter.TelAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class TelnetFragment extends Fragment implements View.OnClickListener {


    /**  */
    private EditText mTelIp;
    /**
     * 2222
     */
    private EditText mTelPort;
    /**
     * admin
     */
    private EditText mTelUser;
    /**
     * 123
     */
    private EditText mTelPsw;
    /**
     * 开启Telnet
     */
    private Button mBtnGet;
    private DropDownMenu mTelDropDownMenu;
    private String headers[] = {"Telnet历史连接记录^_^"};
    private List<View> popupViews = new ArrayList<>();
    private TelAdapter adapter;
    private List<TelHis> all;
    private AlertDialog builder;

    public TelnetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_telnet, container, false);
        initView(view);
        ListView listView = new ListView(getContext());
        all = LitePal.order("id desc").find(TelHis.class);
        adapter = new TelAdapter(getContext(), R.layout.ftplistlayout, all);
        if (all!=null&&all.size()>0){
            mTelIp.setText(all.get(0).getIp());
            mTelPort.setText(all.get(0).getPort());
            mTelUser.setText(all.get(0).getUser());
            mTelPsw.setText(all.get(0).getPsw());
        }

        listView.setDividerHeight(0);
        listView.setAdapter(adapter);
        popupViews.add(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TelHis telHis = all.get(position);
                mTelIp.setText(telHis.getIp());
                mTelPort.setText(telHis.getPort());
                mTelUser.setText(telHis.getUser());
                mTelPsw.setText(telHis.getPsw());
                mTelDropDownMenu.closeMenu();
            }
        });
        TextView contentView = new TextView(getContext());
        mTelIp.setText(NetworkUtils.getIPAddress(true));
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTelDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
        return view;
    }


    private void initView(View view) {

        mTelIp = (EditText) view.findViewById(R.id.tel_ip);
        mTelPort = (EditText) view.findViewById(R.id.tel_port);
        mTelUser = (EditText) view.findViewById(R.id.tel_user);
        mTelPsw = (EditText) view.findViewById(R.id.tel_psw);
        mBtnGet = (Button) view.findViewById(R.id.btnGet);
        mBtnGet.setOnClickListener(this);
        mTelDropDownMenu = (DropDownMenu) view.findViewById(R.id.Tel_dropDownMenu);
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btnGet:

                String user = mTelUser.getText().toString().trim();
                String psw = mTelPsw.getText().toString().trim();
                String ip = mTelIp.getText().toString().trim();
                int port = Integer.parseInt((mTelPort.getText() + "").trim());
                if (RegexUtils.isIP(ip) && port > 0 && port <= 65535) {
                    TelHis telHis = new TelHis();
                    telHis.setIp(ip);
                    telHis.setPort(port + "");
                    telHis.setUser(user);
                    telHis.setPsw(psw);
                    telHis.save();
                    Intent intent = new Intent(getActivity(), TelnetActivity.class);
                    //TelHis telHis = new TelHis(ip, port + "", user, psw);
                    intent.putExtra("tel", telHis);
                    startActivity(intent);

                } else {
                    builder = new AlertDialog.Builder(getContext())
                            .setTitle("警告！")
                            .setMessage("IP或端口输入错误，请重新输入！！")
                            .setIcon(R.drawable.warn)
                            .setPositiveButton("确定", null)
                            .setCancelable(false).create();
                    builder.getWindow().setWindowAnimations(R.style.mystyle);
                    builder.show();
                }

                break;
        }
    }
}
