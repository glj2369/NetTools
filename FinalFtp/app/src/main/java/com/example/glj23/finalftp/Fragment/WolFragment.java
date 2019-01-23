package com.example.glj23.finalftp.Fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.example.glj23.finalftp.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Bean.MacHis;
import Util.WolWakeUtil;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class WolFragment extends Fragment implements View.OnClickListener {


    private View view;
    private EditText mWolIpEt;
    private AutoCompleteTextView mWolMacEt;
    /**
     * 开始唤醒
     */
    private Button mWolBt;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<MacHis> all;
    private AlertDialog builder;

    public WolFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wol, container, false);
        initView(view);
        mWolIpEt.setText(NetworkUtils.getBroadcastIpAddress());
        all = LitePal.findAll(MacHis.class);
        for (MacHis macHis: all) {
            list.add(macHis.getMac());
        }
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        mWolMacEt.setAdapter(adapter);
        return view;
    }

    private void initView(View view) {
        mWolIpEt = view.findViewById(R.id.wol_ipEt);
        mWolMacEt =  view.findViewById(R.id.wol_macEt);
        mWolBt = view.findViewById(R.id.wol_Bt);
        mWolBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.wol_Bt:
                String ip = mWolIpEt.getText().toString().trim();
                String mac = mWolMacEt.getText().toString().trim();


                if (RegexUtils.isIP(ip) && isMac(mac)) {
                    new WolWakeUtil(ip, mac).start();
                    MacHis macHis = new MacHis();
                    macHis.setMac(mac);
                    macHis.save();
                    adapter.add(mac);
                    //Toast.makeText(getActivity(), "唤醒成功！！", Toast.LENGTH_SHORT).show();
                    Toasty.success(getActivity(), "唤醒成功！！", Toast.LENGTH_SHORT, true).show();
                } else {
                    builder = new AlertDialog.Builder(getContext())
                            .setTitle("警告！")
                            .setMessage("IP或MAC输入错误，请重新输入！！")
                            .setIcon(R.drawable.warn)
                            .setPositiveButton("确定", null)
                            .setCancelable(false).create();
                    builder.getWindow().setWindowAnimations(R.style.mystyle);
                    builder.show();
                }
                break;
        }
    }


    public static boolean isMac(final CharSequence input) {
        return isMatch("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}", input);
    }

    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
}
