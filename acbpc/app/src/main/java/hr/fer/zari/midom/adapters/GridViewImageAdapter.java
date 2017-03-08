package hr.fer.zari.midom.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.picture.ImageBitmap;
import hr.fer.zari.midom.utils.ImageException;

public class GridViewImageAdapter extends BaseAdapter {

    public static final String TAG = GridViewImageAdapter.class.getName();

    private Activity activity;
    private List<ImageBitmap> imageBitmaps;
    private int imageWidth;

    public GridViewImageAdapter(Activity activity, List<ImageBitmap> imageBitmaps, int imageWidth) {
        this.activity = activity;
        this.imageBitmaps = imageBitmaps;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return imageBitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return imageBitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView = " + position);

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(activity);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));

        if(imageBitmaps.get(position).getThumbBitmap() == null) {
            imageView.setImageResource(R.drawable.anonymous_avatar);
            try {
                imageBitmaps.get(position).loadThumbBitmap();
            } catch (ImageException e) {
                e.printStackTrace();
                Toast.makeText(activity, R.string.wrong_picture_format, Toast.LENGTH_LONG).show();
            }
        } else {
            imageView.setImageBitmap(imageBitmaps.get(position).getThumbBitmap());
        }

        return imageView;
    }
}
