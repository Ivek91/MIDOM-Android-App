package hr.fer.zari.midom.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.model.AccountDetails;
import hr.fer.zari.midom.model.Specialisation;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.utils.FileHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class SettingsAvatarFragment extends Fragment implements ImageChooserListener {

    private final String TAG = getClass().getName();

    private OnFragmentInteractionListener mListener;


    private EditText mPassword;
    private EditText mPassword2;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;
    private String filePath;

    private ImageView imageViewThumbnail;

    private ProgressBar pbar;

    private AccountDetails accountDetails;

    // TODO: Rename and change types of parameters
    public static SettingsAvatarFragment newInstance(String param1, String param2) {
        SettingsAvatarFragment fragment = new SettingsAvatarFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsAvatarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        downloadAccountDetails();
    }

    private void downloadAccountDetails() {
        new RestClient().getMidomService().getAccountDetails(new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                accountDetails = accountDetailsResponse.getMessage();

                downloadAvatar();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Unable to download account details",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void downloadAvatar() {
        new RestClient().getMidomService().getMsAvatar(accountDetails.getId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {

                    if(filePath == null) {
                        byte[] bytes = FileHelper.getBytesFromStream(response.getBody().in());

                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageViewThumbnail.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void save() {
        Log.d(TAG, "save");

        if(filePath != null && filePath.endsWith(".jpg")) {
            TypedFile image = new TypedFile("multipart/form-data", new File(filePath));
            new RestClient().getMidomService().changeAvatar(image, new Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    SettingsAvatarFragment.this.getActivity().finish();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {
            Toast.makeText(getActivity(), "Please choose image in JPG format",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings_avatar, container, false);

        Button buttonChooseImage = (Button) view
                .findViewById(R.id.buttonChooseImage);
        buttonChooseImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        Button buttonTakePicture = (Button) view.findViewById(R.id.buttonTakePicture);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        imageViewThumbnail = (ImageView) view.findViewById(R.id.imageViewThumb);

        pbar = (ProgressBar) view.findViewById(R.id.progressBar);
        pbar.setVisibility(View.GONE);

        return view;
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "On Activity Result " + requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pbar.setVisibility(View.GONE);
                if (image != null) {
                    filePath = image.getFilePathOriginal();
                    imageViewThumbnail.setImageURI(Uri.parse(new File(image
                            .getFileThumbnail()).toString()));
                }
            }
        });
    }

    @Override
    public void onError(final String reason) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pbar.setVisibility(View.GONE);
                Toast.makeText(SettingsAvatarFragment.this.getActivity(), reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Specialisation specialisation);
    }

}
