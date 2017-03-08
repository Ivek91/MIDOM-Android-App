package hr.fer.zari.midom.picture;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.BaseAdapter;

import hr.fer.zari.midom.utils.ImageException;

public class ProxyImageBitmap extends ImageBitmap {

    public boolean loading;
    private BaseAdapter adapter;

    public ProxyImageBitmap(String location, BaseAdapter adapter) {
        super(location);
        this.adapter = adapter;
    }

    @Override
    public void loadThumbBitmap() {
        if (loading) return;
        if (getThumbBitmap() != null) return;
        loading = true;

        Log.d(TAG, "thread start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProxyImageBitmap.super.loadThumbBitmap();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loading = false;
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (ImageException e) {
                    // if the error is printed here, it would be multiplied by number of pictures in
                    // the Grid View
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
