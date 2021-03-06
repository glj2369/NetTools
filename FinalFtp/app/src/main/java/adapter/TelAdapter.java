package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.glj23.finalftp.R;

import java.util.List;

import Bean.FtpHis;
import Bean.TelHis;

public class TelAdapter extends ArrayAdapter<TelHis> {
    private Context context;

    public TelAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TelHis item = getItem(position);
        View inflate = LayoutInflater.from(context).inflate(R.layout.ftplistlayout, parent, false);
        TextView ip = inflate.findViewById(R.id.ftp_ip);
        TextView port = inflate.findViewById(R.id.ftp_port);
        TextView user = inflate.findViewById(R.id.ftp_user);
        TextView psw = inflate.findViewById(R.id.ftp_psw);
        ip.setText(item.getIp());
        port.setText(item.getPort());
        user.setText(item.getUser());
        psw.setText(item.getPsw());
        return inflate;
    }
}
