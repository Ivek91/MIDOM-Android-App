package hr.fer.zari.midom.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import hr.fer.zari.midom.R;

public class DialogLoading extends DialogFragment {

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.dialog_loading);
        dialog.setTitle(title);
        dialog.show();

        return dialog;
    }

}