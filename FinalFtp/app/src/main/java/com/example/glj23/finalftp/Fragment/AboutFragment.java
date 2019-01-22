package com.example.glj23.finalftp.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.glj23.finalftp.R;
import com.github.glomadrian.grav.GravView;

import org.litepal.LitePal;

import Bean.FtpHis;
import Bean.IpHis;
import Bean.MacHis;
import Bean.TelHis;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {


    private View view;
    /**
     * 清空SQL
     */
    private Button mClear;
    private GravView mGrav;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about2, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mClear = (Button) view.findViewById(R.id.clear);
        mClear.setOnClickListener(this);
        mGrav = (GravView) view.findViewById(R.id.grav);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.clear:
                LitePal.deleteAll(FtpHis.class);
                LitePal.deleteAll(IpHis.class);
                LitePal.deleteAll(MacHis.class);
                LitePal.deleteAll(TelHis.class);
                //Toast.makeText(getContext(), "SQL清理完成!", Toast.LENGTH_SHORT).show();
                Toasty.success(getContext(), "SQL清理完成!", Toast.LENGTH_SHORT, true).show();
                break;
        }
    }
}
