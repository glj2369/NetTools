package adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glj23.finalftp.R;

import java.util.List;

import Bean.ArpBean;
import es.dmoral.toasty.Toasty;

public class ArpList extends ArrayAdapter<ArpBean> {
    private int resid;
    private Context context;

    public ArpList(Context context, int resource, List<ArpBean> objects) {
        super(context, resource, objects);
        this.resid = resource;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ArpBean item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resid, parent, false);
        TextView ip = view.findViewById(R.id.arp_ip_li);
        TextView mac = view.findViewById(R.id.arp_mac_li);
        ip.setText(item.getIp());
        mac.setText(item.getMac());
        mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(item.getMac());
                //Toast.makeText(context, "复制成功!", Toast.LENGTH_LONG).show();
                Toasty.success(context,"mac复制成功!", Toast.LENGTH_SHORT, true).show();

            }
        });
        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(item.getIp());
               // Toast.makeText(context, "复制成功!", Toast.LENGTH_LONG).show();
                Toasty.success(context,"ip复制成功!", Toast.LENGTH_SHORT, true).show();
            }
        });
        return view;
    }
}
