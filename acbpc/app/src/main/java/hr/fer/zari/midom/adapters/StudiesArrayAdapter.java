package hr.fer.zari.midom.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.model.ConsultationRequest;

public class StudiesArrayAdapter extends ArrayAdapter<ConsultationRequest> {

    public static final String TAG = StudiesArrayAdapter.class.getCanonicalName();

    private final Context context;
    private final List<ConsultationRequest> requests;

    public StudiesArrayAdapter(Context context, List<ConsultationRequest> objects) {
        super(context, 0, objects);
        this.context = context;
        this.requests = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.studies_row, parent, false);
        }
        TextView studyText = (TextView) convertView.findViewById(R.id.studies_text);
        TextView studyOwner = (TextView) convertView.findViewById(R.id.studies_owner);
        TextView studyDate = (TextView) convertView.findViewById(R.id.studies_date);
        ImageView studyAvatar = (ImageView) convertView.findViewById(R.id.studies_avatar);

        ConsultationRequest consultationRequest = requests.get(position);

        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(consultationRequest.getCreationTime()));
        studyDate.setText("" + context.getString(R.string.creation_date) + dateString);

        if(consultationRequest.getStudyObj() != null) {
            studyText.setText("" + context.getString(R.string.name_study) + consultationRequest.getStudyObj().getName());

            if(consultationRequest.getStudyObj().getOwnerObj() != null) {
                studyOwner.setText("" + context.getString(R.string.study_owner) + consultationRequest.getStudyObj().getOwnerObj().getName());
            } else {
                consultationRequest.getStudyObj().loadOwner(this);
            }
        } else {
            studyText.setText("");
            consultationRequest.loadStudy(this);
        }

        if(consultationRequest.getAvatar() != null) {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(consultationRequest.getAvatar());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            studyAvatar.setImageBitmap(bitmap);

        } else {
            studyAvatar.setImageResource(R.drawable.anonymous_avatar);

            if(!consultationRequest.avatarDownloadInProgress) {
                consultationRequest.loadAvatar(this);
            }
        }

        return convertView;
    }


}
