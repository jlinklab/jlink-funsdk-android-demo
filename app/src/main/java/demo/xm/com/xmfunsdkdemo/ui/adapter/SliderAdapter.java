package demo.xm.com.xmfunsdkdemo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private List<String> imageUrls;

    public SliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new SliderAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        Glide.with(viewHolder.itemView)
                .load(imageUrls.get(position))
                .into(viewHolder.imageView);
        viewHolder.tvPicName.setText((position + 1) + "/" + imageUrls.size());
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        PhotoView imageView;
        TextView tvPicName;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvPicName = itemView.findViewById(R.id.tv_pic_name);
        }
    }
}

