package jp.onetake.binzumejigoku.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	private static final String DIALOG_MESSAGE = "ProgressDialogFragment.DIALOG_MESSAGE";

	public static ProgressDialogFragment createInstance(String message) {
		Bundle params = new Bundle();
		params.putString(DIALOG_MESSAGE, message);


		ProgressDialogFragment fragment = new ProgressDialogFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getContext());
		dialog.setMessage(getArguments().getString(DIALOG_MESSAGE));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);

		return dialog;
	}
}
