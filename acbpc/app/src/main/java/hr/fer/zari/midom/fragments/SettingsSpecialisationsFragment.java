package hr.fer.zari.midom.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.GetAllSpecialisationsResponse;
import hr.fer.zari.midom.rest.response.UpdateSpecialisationsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

public class SettingsSpecialisationsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private final String TAG = getClass().getName();

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private AccountDetails accountDetails;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SpecialisationsArrayAdapter adapter;

    // TODO: Rename and change types of parameters
    public static SettingsSpecialisationsFragment newInstance(String param1, String param2) {
        SettingsSpecialisationsFragment fragment = new SettingsSpecialisationsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsSpecialisationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        downloadAccountDetails();
    }

    private void downloadAccountDetails() {
        new RestClient().getMidomService().getAccountDetails(new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                accountDetails = accountDetailsResponse.getMessage();

                downloadAllSpecialisations();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void downloadAllSpecialisations() {
        new RestClient().getMidomService().getAllSpecialisations(new Callback<GetAllSpecialisationsResponse>() {
            @Override
            public void success(GetAllSpecialisationsResponse getAllSpecialisationsResponse, Response response) {
                adapter = new SpecialisationsArrayAdapter(getActivity(), getAllSpecialisationsResponse.getMessage(), accountDetails.getSpecialisations());
                mListView = (AbsListView) getView().findViewById(android.R.id.list);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(SettingsSpecialisationsFragment.this);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void save() {
        Log.d(TAG, "save");

        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i < adapter.getCount(); ++i) {
            Specialisation specialisation = adapter.getItem(i);
            if(specialisation.isSelected()) {
                ids.add(specialisation.getId());
            }
        }

        new RestClient().getMidomService().updateSpecialisations(ids, new Callback<UpdateSpecialisationsResponse>() {
            @Override
            public void success(UpdateSpecialisationsResponse updateSpecialisationsResponse, Response response) {
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings_specialisations, container, false);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Specialisation specialisation = (Specialisation) mListView.getAdapter().getItem(position);
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction((Specialisation) mListView.getAdapter().getItem(position));
        }

        specialisation.setSelected(!specialisation.isSelected());
        ((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
