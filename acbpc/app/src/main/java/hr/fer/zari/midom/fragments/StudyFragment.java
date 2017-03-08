package hr.fer.zari.midom.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.activities.FullScreenImageActivity;
import hr.fer.zari.midom.activities.MainActivity;
import hr.fer.zari.midom.adapters.GridViewImageAdapter;
import hr.fer.zari.midom.picture.ImageBitmap;
import hr.fer.zari.midom.picture.ProxyImageBitmap;
import hr.fer.zari.midom.rest.MidomService;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.GetCrMesssagesResponse;
import hr.fer.zari.midom.rest.response.GetStudyResponse;
import hr.fer.zari.midom.task.AsyncDownloadStudy;
import hr.fer.zari.midom.utils.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class StudyFragment extends Fragment implements AsyncDownloadStudy.unzipCompleted {

    public static final String TAG = StudyFragment.class.getName();
    public static final String SELECTED_ITEM = "hr.fer.zari.midom.selected_item";
    public static final String CONSULTATION_REQ = "hr.fer.zari.midom.studyFragment.CONS_REQ";

    private String studyName;
    private String studyOwner;
    private String studyOwnerOrganisation;
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private List<ImageBitmap> imageBitmaps;
    private int consultationRequestID;
    private TextView tvMessage;

    public StudyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBitmaps = new ArrayList<>();

        Bundle args = getActivity().getIntent().getExtras();
        consultationRequestID = args.getInt(MainActivity.EXTRA_CR_ID);
        int studyID = args.getInt(MainActivity.EXTRA_STUDY_ID);
        try {
            AsyncDownloadStudy asyncDownloadStudy = new AsyncDownloadStudy(getActivity(), studyID);
            asyncDownloadStudy.execute();
            asyncDownloadStudy.setListener(this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final MidomService midomService = new RestClient().getMidomService();
        midomService.getStudy(studyID, new Callback<GetStudyResponse>() {
            @Override
            public void success(GetStudyResponse getStudyResponse, Response response) {
                studyName = getStudyResponse.getMessage().getName().trim();
                midomService.getAccount(getStudyResponse.getMessage().getOwnerId(), new Callback<AccountDetailsResponse>() {
                    @Override
                    public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                        studyOwner = accountDetailsResponse.getMessage().getName().trim();
                        studyOwnerOrganisation = accountDetailsResponse.getMessage().getOrganisation().trim();
                        setText();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        midomService.getCrMessages(consultationRequestID, new Callback<GetCrMesssagesResponse>() {
            @Override
            public void success(GetCrMesssagesResponse getCrMesssagesResponse, Response response) {
                Log.d(TAG, "Consultation message success!");
                tvMessage.setText("Specialist comment: "
                        + getCrMesssagesResponse.getMessage().get(0).getComment().trim());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Consultation message failure!");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        tvMessage = (TextView) view.findViewById(R.id.study_text_message);

        gridView = (GridView) view.findViewById(R.id.grid_view);
        initializeGridLayout();
        // Gridview adapter
        adapter = new GridViewImageAdapter(getActivity(), imageBitmaps, columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick = " + position);
                Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                intent.putExtra(CONSULTATION_REQ, consultationRequestID);
                intent.putExtra(SELECTED_ITEM, position);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setText() {
        TextView tvStudyProvider = (TextView) getActivity().findViewById(R.id.study_text_sp);
        TextView tvStudyName = (TextView) getActivity().findViewById(R.id.study_text_name_study);

        tvStudyProvider.setText("Study provider: " + studyOwner + ", " + studyOwnerOrganisation);
        tvStudyName.setText("Study name: " + studyName);
        Log.d(TAG, "study name: " + studyName);
    }


    @Override
    public void unzipFinished() {
        File f = new File(Constants.ZIP_EXTRACT + "/images");
        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                ImageBitmap imageBitmap = new ProxyImageBitmap(file.getAbsolutePath(), adapter);
                imageBitmaps.add(imageBitmap);
                Log.d("Files", "FileName:" + file.getName());
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void initializeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constants.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((getScreenWidth() -
                ((Constants.NUM_OF_COLUMNS_GRID + 1) * padding)) / Constants.NUM_OF_COLUMNS_GRID);

        gridView.setNumColumns(Constants.NUM_OF_COLUMNS_GRID);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    /**
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}
