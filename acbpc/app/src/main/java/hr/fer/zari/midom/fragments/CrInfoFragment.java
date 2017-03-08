package hr.fer.zari.midom.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.activities.MainActivity;
import hr.fer.zari.midom.activities.StudyActivity;
import hr.fer.zari.midom.rest.MidomService;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.request.AcceptConsultationRequest;
import hr.fer.zari.midom.rest.request.RejectConsultationRequest;
import hr.fer.zari.midom.rest.response.AcceptConsultationResponse;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.GetCrMesssagesResponse;
import hr.fer.zari.midom.rest.response.GetStudyResponse;
import hr.fer.zari.midom.rest.response.RejectConsultationResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class CrInfoFragment extends Fragment {

    public static final String TAG = CrInfoFragment.class.getName();

    private int crID;
    private int studyID;
    private int studyOwnerID;
    private long creationTime;

    public CrInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cr_info, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getActivity().getIntent().getExtras();
        studyID = args.getInt(MainActivity.EXTRA_STUDY_ID);
        crID = args.getInt(MainActivity.EXTRA_CR_ID);
        studyOwnerID = args.getInt(MainActivity.EXTRA_CR_OWNER);
        creationTime = args.getLong(MainActivity.EXTRA_CR_TIME);

    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView tvCrName = (TextView) getActivity().findViewById(R.id.crInfo_name);
        final TextView tvCrOwner = (TextView) getActivity().findViewById(R.id.crInfo_owner);
        TextView tvCrDate = (TextView) getActivity().findViewById(R.id.crInfo_createDate);
        final TextView tvMessage = (TextView) getActivity().findViewById(R.id.crInfo_message);

        final MidomService midomService = new RestClient().getMidomService();
        midomService.getStudy(studyID, new Callback<GetStudyResponse>() {
            @Override
            public void success(GetStudyResponse getStudyResponse, Response response) {
                tvCrName.setText("" + getActivity().getString(R.string.name_study)
                        + getStudyResponse.getMessage().getName());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        midomService.getAccount(studyOwnerID, new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                tvCrOwner.setText("" + getActivity().getString(R.string.study_owner)
                        + accountDetailsResponse.getMessage().getName());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(creationTime));
        tvCrDate.setText("" + getActivity().getString(R.string.creation_date) + dateString);


        midomService.getCrMessages(crID, new Callback<GetCrMesssagesResponse>() {
            @Override
            public void success(GetCrMesssagesResponse getCrMesssagesResponse, Response response) {
                Log.d(TAG, "Consultation message success!");
                tvMessage.setText("\nSpecialist comment: \n" + getCrMesssagesResponse.getMessage().get(0).getComment());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Consultation message failure!");
            }
        });


        Button buttonAccept = (Button) getActivity().findViewById(R.id.crInfo_accept_button);
        Button buttonReject = (Button) getActivity().findViewById(R.id.crInfo_reject_button);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptConsultationRequest();
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectConsultationRequest();
            }
        });
    }

    public void acceptConsultationRequest() {
        final MidomService midomService = new RestClient().getMidomService();
        AcceptConsultationRequest acceptConsultationRequest = new AcceptConsultationRequest("" + crID);

        midomService.acceptConsultationRequest(acceptConsultationRequest, new Callback<AcceptConsultationResponse>() {
            @Override
            public void success(AcceptConsultationResponse acceptConsultationResponse, Response response) {
                //Spinner spinner = (Spinner) getView().findViewById(R.id.spinner_request);
                //spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition("Accepted"));

                Intent intent = new Intent(getActivity(), StudyActivity.class);
                intent.putExtra(MainActivity.EXTRA_STUDY_ID, studyID);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void rejectConsultationRequest() {
        final MidomService midomService = new RestClient().getMidomService();
        RejectConsultationRequest rejectConsultationRequest = new RejectConsultationRequest("" + crID);

        midomService.rejectConsultationRequest(rejectConsultationRequest, new Callback<RejectConsultationResponse>() {
            @Override
            public void success(RejectConsultationResponse rejectConsultationResponse, Response response) {
                getActivity().finish();
                //onStatus("Pending");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
