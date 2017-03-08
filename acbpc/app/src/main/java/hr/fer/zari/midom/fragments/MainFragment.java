package hr.fer.zari.midom.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.adapters.StudiesArrayAdapter;
import hr.fer.zari.midom.model.ConsultationRequest;
import hr.fer.zari.midom.rest.MidomService;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.response.ConsultationRequestsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MainFragment extends Fragment implements AbsListView.OnItemClickListener {

    private final String TAG = getClass().getName();



    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private StudiesArrayAdapter adapter;

    // TODO: Rename and change types of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item, container, false);

        final Spinner consultationRequest = (Spinner) view.findViewById(R.id.spinner_request);
        consultationRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onStatus((String) consultationRequest.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
            }
        });

        return view;
    }

    private void onStatus(String status) {
        final MidomService midomService = new RestClient().getMidomService();

        midomService.getConsultationRequestsByStatus(status, new Callback<ConsultationRequestsResponse>() {
            @Override
            public void success(ConsultationRequestsResponse consultationRequestsResponse, Response response) {

                if(consultationRequestsResponse.getCode() == 0) {
                    adapter = new StudiesArrayAdapter(getActivity(), consultationRequestsResponse.getMessage());
                    mListView = (AbsListView) getView().findViewById(android.R.id.list);
                    mListView.setAdapter(adapter);
                    mListView.setOnItemClickListener(MainFragment.this);
                } else {
                    getActivity().finish();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction((ConsultationRequest) mListView.getAdapter().getItem(position));
        }
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
        public void onFragmentInteraction(ConsultationRequest consultationRequest);
    }


}
