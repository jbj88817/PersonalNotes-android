package com.bojie.personalnotes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by bojiejiang on 6/22/15.
 */
public class ErrorDialogFragment extends DialogFragment{

    public ErrorDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int errorCode = getArguments().getInt(AppConstant.DIALOG_ERROR);
        return GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), errorCode);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        getActivity().finish();
    }
}
