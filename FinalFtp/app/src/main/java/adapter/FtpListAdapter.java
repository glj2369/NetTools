package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glj23.finalftp.R;

import java.util.List;

import Bean.FtpFileBean;
import Util.MyTextView;

public class FtpListAdapter extends ArrayAdapter<FtpFileBean> {
    private Context context;

    public FtpListAdapter(Context context, int resource, List<FtpFileBean> objects) {
        super(context, resource, objects);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FtpFileBean item = getItem(position);
        View inflate;
        ViewHolder viewHolder;
        if (convertView == null) {
            inflate = LayoutInflater.from(context).inflate(R.layout.ftpclistitem, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = inflate.findViewById(R.id.ftp_icon);
            viewHolder.title  = inflate.findViewById(R.id.ftp_title);
            viewHolder.time  = inflate.findViewById(R.id.ftp_time);
            viewHolder.size = inflate.findViewById(R.id.ftp_size);
            inflate.setTag(viewHolder);
        } else {
            inflate = convertView;
            viewHolder = (ViewHolder) inflate.getTag();
        }

        viewHolder.imageView.setImageResource(item.getIcon());
        viewHolder.title.setText(item.getName());
        viewHolder.time.setText(item.getTime());
        viewHolder.size.setText(item.getSize());
        viewHolder.title.setSelected(true);
        return inflate;
    }

    class ViewHolder {
        ImageView imageView;
        MyTextView title;
        TextView time;
        TextView size;
    }
}
