package hr.fer.zari.midom.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.widget.Toast;
import hr.fer.zari.midom.CRAlarmReceiver;
import hr.fer.zari.midom.fragments.MainFragment;
import hr.fer.zari.midom.MidomApplication;
import hr.fer.zari.midom.R;
import hr.fer.zari.midom.model.AccountDetails;
import hr.fer.zari.midom.model.ConsultationRequest;
import hr.fer.zari.midom.rest.MidomService;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.request.ChangeStatusRequest;
import hr.fer.zari.midom.rest.response.AccountDetailsResponse;
import hr.fer.zari.midom.rest.response.ChangeStatusResponse;
import hr.fer.zari.midom.utils.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements MainFragment.OnFragmentInteractionListener {

    public final static String EXTRA_STUDY_ID = "hr.fer.zari.midom.STUDY_ID";
    public final static String EXTRA_CR_ID = "hr.fer.zari.midom.CR_ID";
    public final static String EXTRA_CR_OWNER = "hr.fer.zari.midom.CR_OWNER";
    public final static String EXTRA_CR_TIME = "hr.fer.zari.midom.CR_TIME";

    private final String TAG = getClass().getName();

    private Spinner mSpinnerStatus;
    private AccountDetails accountDetails;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAlarm();
    }

    private void setAlarm() {
        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MainActivity.this, CRAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.ALARM_INTERVAL, pendingIntent);
    }

    private void downloadAccountDetails() {
        MidomService midomService = new RestClient().getMidomService();

        midomService.getAccountDetails(new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                accountDetails = accountDetailsResponse.getMessage();

                onAccount(accountDetails);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void onAccount(final AccountDetails accountDetails) {
        Log.d(TAG, accountDetails.toString());

        ArrayAdapter adapter = (ArrayAdapter) mSpinnerStatus.getAdapter();
        mSpinnerStatus.setSelection(accountDetails.isAvailable() ? adapter.getPosition("Available") : adapter.getPosition("Not available"));

        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isAvailable = mSpinnerStatus.getSelectedItem().equals("Available");

                Log.d(TAG, "isAvailable: " + isAvailable);

                if(isAvailable != accountDetails.isAvailable()) {
                    updateAccountStatus(isAvailable);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateAccountStatus(final boolean isAvailable) {
        MidomService midomService = new RestClient().getMidomService();

        final ChangeStatusRequest changeStatusRequest = new ChangeStatusRequest(isAvailable ? "available" : "not available");
        midomService.changeStatus(changeStatusRequest, new Callback<ChangeStatusResponse>() {
            @Override
            public void success(ChangeStatusResponse changeStatusResponse, Response response) {
                if (changeStatusResponse.getCode() == 0) {
                    accountDetails.setIsAvailable(isAvailable);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_action_bar, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        MenuItem spinnerItem = menu.findItem(R.id.my_switch_item);
        mSpinnerStatus = (Spinner) spinnerItem.getActionView().findViewById(R.id.spinner_status);

        downloadAccountDetails(); // used to be in onStart, but there was race condition between this mSpinnerStatus view creation
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_update_specialisations) {
            Log.v(TAG, "Open update specialisations");
            Intent intent = new Intent(this, SettingsSpecialisationsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_account_details) {
            Log.v(TAG, "Open update account details");
            Intent intent = new Intent(this, SettingsAccountDetailsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_password) {
            Log.v(TAG, "Open update password");
            Intent intent = new Intent(this, SettingsPasswordActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_avatar) {
            Log.v(TAG, "Open update avatar");
            Intent intent = new Intent(this, SettingsAvatarActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_set_download_type) {
            Log.v(TAG, "Open set download type");
            Intent intent = new Intent(this, SetDownloadType.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_sign_out) {
            MidomApplication midomApplication = (MidomApplication) getApplication();
            midomApplication.getCookieManager().getCookieStore().removeAll();

            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(ConsultationRequest consultationRequest) {
        if(consultationRequest.getStatus().equals("Accepted")) {
            Log.d(TAG, "Click on: " + consultationRequest);
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra(EXTRA_STUDY_ID, consultationRequest.getStudy());
            intent.putExtra(EXTRA_CR_ID, consultationRequest.getId());
            startActivity(intent);
        }
        if(consultationRequest.getStatus().equals("Pending")) {
            Log.d(TAG, "Click on: " + consultationRequest);
            Intent intent = new Intent(this, CrInfoActivity.class);
            intent.putExtra(EXTRA_STUDY_ID, consultationRequest.getStudy());
            intent.putExtra(EXTRA_CR_ID, consultationRequest.getId());
            intent.putExtra(EXTRA_CR_OWNER,consultationRequest.getStudyOwner());
            intent.putExtra(EXTRA_CR_TIME, consultationRequest.getCreationTime());
            startActivity(intent);
        }
    }
}
