package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.vm.DevConfigViewModel;

/**
 * 设备配置列表的adapter,包括图片,标题及描述
 * adapter for the device configuration list, including an image, title, and description
 * Created by jiangping on 2018-10-23.
 */
public class DevConfigAdapter extends ArrayAdapter {
    private final int resourceId;

    public DevConfigAdapter(Context context, int textViewResourceId, List<DevConfigViewModel> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DevConfigViewModel picture = (DevConfigViewModel) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        ImageView ImageName = view.findViewById(R.id.imgItemIcon);
        TextView TitleName = view.findViewById(R.id.txtItemTitle);
        TextView DescName = view.findViewById(R.id.txtItemDesc);

        if (picture.getImageId() != 0) {
            ImageName.setImageResource(picture.getImageId());
        }

        TitleName.setText(picture.getName());
        if (picture.getDes() != -1) {
            DescName.setText(picture.getDes());
        }

        return view;
    }
}
