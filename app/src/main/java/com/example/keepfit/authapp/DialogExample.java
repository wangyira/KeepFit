package com.example.keepfit.authapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.keepfit.R;

public class DialogExample extends AppCompatDialogFragment {

    private EditText edit;
    private DialogExampleListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_dialog, null);

        builder.setView(view).setTitle("Edit").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        })
                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newedit = edit.getText().toString();
                        listener.applyTexts(newedit);
                    }
                });

        edit = view.findViewById(R.id.edit);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogExampleListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogueExampleListener");
        }
    }
    public interface DialogExampleListener{
        void applyTexts(String newedit);
    }
}
