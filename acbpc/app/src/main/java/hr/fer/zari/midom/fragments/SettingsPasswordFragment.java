package hr.fer.zari.midom.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.adapters.SpecialisationsArrayAdapter;
import hr.fer.zari.midom.model.AccountDetails;
import hr.fer.zari.midom.model.Specialisation;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.request.ChangePasswordRequest;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.ChangePasswordResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingsPasswordFragment extends PreferenceFragment {

    private final String TAG = getClass().getName();

    private OnFragmentInteractionListener mListener;


    private EditText mPassword;
    private EditText mPassword2;

    private AccountDetails accountDetails;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SpecialisationsArrayAdapter adapter;

    // TODO: Rename and change types of parameters
    public static SettingsPasswordFragment newInstance(String param1, String param2) {
        SettingsPasswordFragment fragment = new SettingsPasswordFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsPasswordFragment() {
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
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void save() {
        Log.d(TAG, "save");

        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();

        if(password.equals(password2)) {
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(password, password2);
            new RestClient().getMidomService().changePassword(changePasswordRequest, new Callback<ChangePasswordResponse>() {
                @Override
                public void success(ChangePasswordResponse changePasswordResponse, Response response) {
                    if(changePasswordResponse.getCode() == 0) {
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), changePasswordResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            Toast.makeText(getActivity(), "Passwords do not match",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings_password, container, false);

        mPassword = (EditText) view.findViewById(R.id.password);
        mPassword2 = (EditText) view.findViewById(R.id.password2);

        return view;
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
