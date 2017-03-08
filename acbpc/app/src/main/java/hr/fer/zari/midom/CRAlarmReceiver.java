package hr.fer.zari.midom;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import hr.fer.zari.midom.activities.LoginActivity;
import hr.fer.zari.midom.model.ConsultationRequest;
import hr.fer.zari.midom.rest.MidomService;
import hr.fer.zari.midom.rest.RestClient;
import hr.fer.zari.midom.rest.request.LoginRequest;
import hr.fer.zari.midom.rest.response.ConsultationRequestsResponse;
import hr.fer.zari.midom.rest.response.LoginResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CRAlarmReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName();

    private final static String SHARED_PREFS_LAST_CR_ID = "hr.fer.zari.midom.sharedPrefs.lastCrId";

    @Override
    public void onReceive(final Context context, Intent intent) {

        final SharedPreferences prefs = context.getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        String username = prefs.getString(LoginActivity.SHARED_PREFS_NAME, null);
        String password = prefs.getString(LoginActivity.SHARED_PREFS_PASS, null);

        if(username != null && password != null) {
            MidomService midomService = new RestClient().getMidomService();
            midomService.msLogin(new LoginRequest(username, password), new Callback<LoginResponse>() {
                @Override
                public void success(LoginResponse loginResponse, Response response) {
                    new RestClient().getMidomService().getConsultationRequestsByStatus("pending", new Callback<ConsultationRequestsResponse>() {
                        @Override
                        public void success(ConsultationRequestsResponse consultationRequestsResponse, Response response) {
                            int lastSeenCrId = prefs.getInt(SHARED_PREFS_LAST_CR_ID, 0);
                            int currentCrId = 0;
                            ConsultationRequest cr = null;
                            for(ConsultationRequest consultationRequest : consultationRequestsResponse.getMessage()) {
                                currentCrId = Math.max(currentCrId, consultationRequest.getId());
                                cr = consultationRequest;
                            }

                            Log.d(TAG, "lastSeen = " + lastSeenCrId + "latest = " + currentCrId);
                            if(cr != null && currentCrId > lastSeenCrId) {
                                Log.d(TAG, "Showing new notification");

                                Intent notificationIntent = new Intent(context, LoginActivity.class);

                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                PendingIntent intent = PendingIntent.getActivity(context, 0,
                                        notificationIntent, 0);

                                Notification noti = new Notification.Builder(context)
                                        .setContentTitle("New consultation request")
                                        .setContentText("You have a new pending consultation request")
                                        .setSmallIcon(R.drawable.anonymous_avatar)
                                        .setContentIntent(intent)
                                        .setAutoCancel(true)
                                        .build();

                                // Gets an instance of the NotificationManager service
                                NotificationManager mNotifyMgr =
                                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                // Builds the notification and issues it.
                                mNotifyMgr.notify(currentCrId, noti);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt(SHARED_PREFS_LAST_CR_ID, currentCrId);
                                editor.apply();
                            }

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
        }
    }
}
