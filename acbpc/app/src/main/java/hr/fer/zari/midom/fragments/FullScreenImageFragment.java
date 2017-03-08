package hr.fer.zari.midom.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.adapters.FullScreenPagerAdapter;
import hr.fer.zari.midom.picture.TouchImageView;
import hr.fer.zari.midom.utils.Constants;
import hr.fer.zari.midom.view.MidomViewPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class FullScreenImageFragment extends Fragment {

    public static final String TAG = FullScreenImageFragment.class.getName();

    private int selectedItem;
    private MidomViewPager viewPager;
    private List<String> locations;
    private FullScreenPagerAdapter adapter;

    public FullScreenImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedItem = getActivity().getIntent().getIntExtra(StudyFragment.SELECTED_ITEM, 0);
        Log.d(TAG, "selectedItem = " + selectedItem);
        locations = new ArrayList<>();
        loadImagesLocations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        viewPager = (MidomViewPager) view.findViewById(R.id.view_pager);
        adapter = new FullScreenPagerAdapter(getActivity(), locations);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedItem);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void loadImagesLocations() {
        File f = new File(Constants.ZIP_EXTRACT + "/images");
        File files[] = f.listFiles();
        for (File file : files) {
            locations.add(file.getAbsolutePath());
        }
    }

}
