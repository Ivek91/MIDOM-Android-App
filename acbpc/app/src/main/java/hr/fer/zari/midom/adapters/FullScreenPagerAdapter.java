package hr.fer.zari.midom.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.picture.ImageBitmap;
import hr.fer.zari.midom.picture.TouchImageView;
import hr.fer.zari.midom.utils.ImageException;

public class FullScreenPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> locations;
    private LayoutInflater layoutInflater;

    public FullScreenPagerAdapter(Context context, List<String> locations) {
        this.context = context;
        this.locations = locations;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

        TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.pgmImage);
        ImageBitmap bitmap = new ImageBitmap(locations.get(position));
        try {
            bitmap.loadBitmap();
            imageView.setImageBitmap(bitmap.getBitmap());
            container.addView(itemView);
        } catch (ImageException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.wrong_picture_format, Toast.LENGTH_LONG).show();
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
