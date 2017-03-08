package hr.fer.zari.midom.activities;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import hr.fer.zari.midom.R;

public class FullScreenImageActivity extends ActionBarActivity {

    public static final String TAG = FullScreenImageActivity.class.getName();

    private MediaRecorder mediaRecorder = null;
    private String comment;
    private int consultationRequestID;
    private boolean audioRecoreded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);


//        consultationRequestID = getIntent().getExtras().getInt(StudyFragment.CONSULTATION_REQ);
//        Log.d(TAG, "consultationRequestID = " + consultationRequestID);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_full_screen_image, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.menu_study_text) {
//            createTextDialog();
//            return true;
//        } else if (id == R.id.menu_study_record) {
//            createRecordDialog();
//            return true;
//        } else if (id == R.id.menu_study_send) {
//            if (comment == null) {
//                Toast.makeText(this, "Can't send consultation without text message!", Toast.LENGTH_LONG).show();
//            } else {
//                sendConfirmation();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void sendConfirmation() {
//        String withAudio = "";
//        if (audioRecoreded) {
//            withAudio = "+ audio comment";
//        }
//        Log.d(TAG, "sendConfirmation()");
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Send consultation");
//        builder.setMessage("Your consultation comment: \n" + comment + "\n" + withAudio);
//        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                sendCommentAndAudio();
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }
//
//
//    private void createRecordDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Audio recorder");
//        builder.setPositiveButton("Start Recording", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Boolean wantToCloseDialog = false;
//                //Do stuff, possibly set wantToCloseDialog to true then...
//                if (wantToCloseDialog)
//                    dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("Stop Recording", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "Clicked Stop recording");
//                stopRecordVoice();
//                dialog.dismiss();
//            }
//        });
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Clicked start recording");
//                recordVoice();
//                Boolean wantToCloseDialog = false;
//                //Do stuff, possibly set wantToCloseDialog to true then...
//                if (wantToCloseDialog)
//                    dialog.dismiss();
//                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
//            }
//        });
//    }
//
//    private void createTextDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter your comment");
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
//        input.setMinLines(6);
//        input.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
//        input.setHint(R.string.enter_comment_hint);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                comment = null;
//            }
//        });
//
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                comment = input.getText().toString();
//                Log.d(TAG, "Comment: " + comment);
//            }
//        });
//        builder.show();
//    }
//
//    private boolean recordVoice() {
//        Log.d(TAG, "recordVoice");
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setOutputFile(Constants.AUDIO_NAME);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mediaRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(TAG, "prepare() failed");
//            Toast.makeText(this, "Problem with recording, try again", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        mediaRecorder.start();
//        Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
//        return true;
//    }
//
//    private void stopRecordVoice() {
//        Log.d(TAG, "stopRecordVoice");
//        if (mediaRecorder != null) {
//            mediaRecorder.stop();
//            mediaRecorder.release();
//            mediaRecorder = null;
//            Toast.makeText(this, "Recording stopped", Toast.LENGTH_LONG).show();
//            audioRecoreded = true;
//        }
//    }
//
//    private void sendCommentAndAudio() {
//        SetConsultationAnswerRequest setConsultationAnswerRequest = new SetConsultationAnswerRequest(comment, "" + consultationRequestID);
//
//        new RestClient().getMidomService().setCrAnswer(setConsultationAnswerRequest, new Callback<SetConsultationAnswerResponse>() {
//            @Override
//            public void success(SetConsultationAnswerResponse setConsultationAnswerResponse, Response response) {
//                int crAnswerID = setConsultationAnswerResponse.getMessage().getId();
//
//                if (audioRecoreded) {
//                    TypedFile audio = new TypedFile("multipart/form-data", new File(Constants.AUDIO_NAME));
//                    new RestClient().getMidomService().uploadAudioFile(crAnswerID, audio, new Callback<Void>() {
//                        @Override
//                        public void success(Void aVoid, Response response) {
//                            finish();
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//
//                        }
//                    });
//                } else {
//                    finish();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
//    }
}
