package hr.fer.zari.midom.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.adapters.SpecialisationsArrayAdapter;
import hr.fer.zari.midom.model.AccountDetails;
import hr.fer.zari.midom.model.Specialisation;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.request.ChangeAccountDetailsRequest;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.ChangeAccountDetailsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingsAccountDetailsFragment extends PreferenceFragment {

    private final String TAG = getClass().getName();

    private OnFragmentInteractionListener mListener;


    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mOrganisation;
    private EditText mDescription;
    private EditText mLocation;
    private EditText mTelephone;
    private EditText mOtherContact;

    private AccountDetails accountDetails;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SpecialisationsArrayAdapter adapter;

    // TODO: Rename and change types of parameters
    public static SettingsAccountDetailsFragment newInstance(String param1, String param2) {
        SettingsAccountDetailsFragment fragment = new SettingsAccountDetailsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsAccountDetailsFragment() {
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

                mFirstName.setText(accountDetails.getFirstName());
                mLastName.setText(accountDetails.getLastName());
                mEmail.setText(accountDetails.getEmail());
                mOrganisation.setText(accountDetails.getOrganisation());
                mDescription.setText(accountDetails.getDescription());
                mLocation.setText(accountDetails.getLocation());
                mTelephone.setText(accountDetails.getTelephon());
                mOtherContact.setText(accountDetails.getTelephon());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void save() {
        Log.d(TAG, "save");

        ChangeAccountDetailsRequest changeAccountDetailsRequest = new ChangeAccountDetailsRequest();
        changeAccountDetailsRequest.setFirstName(mFirstName.getText().toString());
        changeAccountDetailsRequest.setLastName(mLastName.getText().toString());
        changeAccountDetailsRequest.setEmail(mEmail.getText().toString());
        changeAccountDetailsRequest.setOrganisation(mOrganisation.getText().toString());
        changeAccountDetailsRequest.setDescription(mDescription.getText().toString());
        changeAccountDetailsRequest.setLocation(mLocation.getText().toString());
        changeAccountDetailsRequest.setTelephone(mTelephone.getText().toString());
        changeAccountDetailsRequest.setOtherContact(mOtherContact.getText().toString());

        new RestClient().getMidomService().updateAccountDetails(changeAccountDetailsRequest, new Callback<ChangeAccountDetailsResponse>() {
            @Override
            public void success(ChangeAccountDetailsResponse changeAccountDetailsResponse, Response response) {
                if(changeAccountDetailsResponse.getCode() == 0) {
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), changeAccountDetailsResponse.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings_account_details, container, false);

        mFirstName = (EditText) view.findViewById(R.id.first_name);
        mLastName = (EditText) view.findViewById(R.id.last_name);
        mEmail = (EditText) view.findViewById(R.id.username);
        mOrganisation = (EditText) view.findViewById(R.id.organisation);
        mDescription = (EditText) view.findViewById(R.id.description);
        mLocation = (EditText) view.findViewById(R.id.location);
        mTelephone = (EditText) view.findViewById(R.id.telephone);
        mOtherContact = (EditText) view.findViewById(R.id.other_contact);

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
