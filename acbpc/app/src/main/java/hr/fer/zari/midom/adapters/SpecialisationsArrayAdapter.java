package hr.fer.zari.midom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.model.Specialisation;

import java.util.List;

public class SpecialisationsArrayAdapter extends ArrayAdapter<Specialisation> {

    public static final String TAG = SpecialisationsArrayAdapter.class.getCanonicalName();

    private final Context context;

    public SpecialisationsArrayAdapter(Context context, List<Specialisation> objects, List<Specialisation> preselected) {
        super(context, 0, objects);
        this.context = context;

        for(Specialisation specialisation : preselected) {
            for(Specialisation sp : objects) {
                if(sp.getId() == specialisation.getId()) {
                    sp.setSelected(true);
                    break;
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_specialisation, parent, false);
        }

        Specialisation specialisation = getItem(position);

        TextView specialisationText = (TextView) convertView.findViewById(R.id.specialisation_name);
        CheckBox specialisationSelected = (CheckBox) convertView.findViewById(R.id.specialisation_selected);

        specialisationText.setText(specialisation.getName());
        specialisationSelected.setChecked(specialisation.isSelected());
//        TextView studyText = (TextView) convertView.findViewById(R.id.studies_text);
//        TextView studyOwner = (TextView) convertView.findViewById(R.id.studies_owner);
//        TextView studyDate = (TextView) convertView.findViewById(R.id.studies_date);
//
//        Specialisation consultationRequest = specialisations.get(position);
//
//        studyDate.setText("" + context.getString(R.string.creation_date) + consultationRequest.getCreationTime());
//
//        if(consultationRequest.getStudyObj() != null) {
//            studyText.setText("" + context.getString(R.string.name_study) + consultationRequest.getStudyObj().getName());
//
//            if(consultationRequest.getStudyObj().getOwnerObj() != null) {
//                studyOwner.setText("" + context.getString(R.string.study_owner) + consultationRequest.getStudyObj().getOwnerObj().getName());
//            } else {
//                consultationRequest.getStudyObj().loadOwner(this);
//            }
//        } else {
//            studyText.setText("");
//            consultationRequest.loadStudy(this);
//        }

        return convertView;
    }


}
