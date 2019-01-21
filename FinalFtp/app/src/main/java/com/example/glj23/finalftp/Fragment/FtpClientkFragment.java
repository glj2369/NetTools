package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.example.glj23.finalftp.FtpClientActivity;
import com.example.glj23.finalftp.R;
import com.yyydjk.library.DropDownMenu;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Bean.FtpHis;
import adapter.FtpHisAdapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FtpClientkFragment extends Fragment implements View.OnClickListener {
    /**
     * 只需要ip地址，不需要前面的ftp://
     */
    private static String HOST = "192.168.1.183";
    private static int PORT = 21;
    private String USERNAME = "";
    private String PASSWORD = "";

    private View view;
    /**
     * user
     */
    private EditText mEtUserName;
    /**
     * 123
     */
    private EditText mEtPwd;
    /**
     * start
     */
    private Button mBtnGet;
    /**  */
    private EditText mFtpcliIp;
    /**
     * 2222
     */
    private EditText mFrtpcliPort;
    private DropDownMenu mDropDownMenu;
    private String headers[] = {"Ftp历史连接记录^_^"};
    private List<View> popupViews = new ArrayList<>();
    private FtpHisAdapter adapter;
    private List<FtpHis> all;
    private AlertDialog builder;

    public FtpClientkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_ftp_clientk, container, false);
        initView(inflate);
        mFtpcliIp.setText(NetworkUtils.getIPAddress(true));
        ListView listView = new ListView(getContext());
        all = LitePal.order("id desc").find(FtpHis.class);
        adapter = new FtpHisAdapter(getContext(), R.layout.ftplistlayout, all);
        if (all != null && all.size() > 0) {
            mFtpcliIp.setText(all.get(0).getIp());
            mFrtpcliPort.setText(all.get(0).getPort());
            mEtUserName.setText(all.get(0).getUser());
            mEtPwd.setText(all.get(0).getPsw());
        }

        listView.setDividerHeight(0);
        listView.setAdapter(adapter);
        popupViews.add(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FtpHis ftpHis = all.get(position);
                mFtpcliIp.setText(ftpHis.getIp());
                mFrtpcliPort.setText(ftpHis.getPort());
                mEtUserName.setText(ftpHis.getUser());
                mEtPwd.setText(ftpHis.getPsw());
                mDropDownMenu.closeMenu();
            }
        });
        TextView contentView = new TextView(getContext());
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
        return inflate;
    }


    private void initView(View inflate) {

        mEtUserName = (EditText) inflate.findViewById(R.id.etUserName);
        mEtPwd = (EditText) inflate.findViewById(R.id.etPwd);
        mBtnGet = (Button) inflate.findViewById(R.id.btnGet);
        mBtnGet.setOnClickListener(this);
        mFtpcliIp = (EditText) inflate.findViewById(R.id.ftpcli_ip);
        mFrtpcliPort = (EditText) inflate.findViewById(R.id.frtpcli_port);
        mDropDownMenu = (DropDownMenu) inflate.findViewById(R.id.dropDownMenu);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null && builder.isShowing()) {
            builder.cancel();
        }
        Log.e("---", "ftp客户端碎片销毁");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btnGet:
                USERNAME = mEtUserName.getText().toString().trim();
                PASSWORD = mEtPwd.getText().toString().trim();
                HOST = mFtpcliIp.getText().toString().trim();
                PORT = Integer.parseInt((mFrtpcliPort.getText() + "").trim());
                if (RegexUtils.isIP(HOST) && PORT > 0 && PORT <= 65535) {
                    FtpHis ftpHis = new FtpHis();
                    ftpHis.setIp(HOST);
                    ftpHis.setPort(PORT + "");
                    ftpHis.setUser(USERNAME);
                    ftpHis.setPsw(PASSWORD);
                    ftpHis.save();
                    SharedPreferences.Editor ftpCli = getContext().getSharedPreferences("ftpCli", MODE_PRIVATE).edit();
                    ftpCli.putString("ip", HOST);
                    ftpCli.putString("port", PORT + "");
                    ftpCli.putString("user", USERNAME);
                    ftpCli.putString("psw", PASSWORD);
                    ftpCli.apply();
                    Intent intent = new Intent(getActivity(), FtpClientActivity.class);

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
                    mFtpcliIp.setText(NetworkUtils.getBroadcastIpAddress());


                }

                break;

        }
    }


}
