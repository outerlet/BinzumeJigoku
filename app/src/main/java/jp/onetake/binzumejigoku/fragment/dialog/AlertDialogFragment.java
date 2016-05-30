package jp.onetake.binzumejigoku.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.onetake.binzumejigoku.R;

/**
 * 「OK」と「キャンセル」のボタンがある、確認用のダイアログを表示するためのDialogFragment
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
	private static final String KEY_TITLE			= "AlertDialogFragment.KEY_TITLE";
	private static final String KEY_MESSAGE			= "AlertDialogFragment.KEY_MESSAGE";
	private static final String KEY_BUTTON_LABEL	= "AlertDialogFragment.KEY_BUTTON_LABEL";

	/**
	 * このFragmentのインスタンスを生成する
	 * @param title		ダイアログのタイトル文字列
	 * @param message	ダイアログのメッセージ
	 * @return	このFragmentのインスタンス
	 */
	public static AlertDialogFragment newInstance(int title, int message, int buttonLabel) {
		Bundle params = new Bundle();
		params.putInt(KEY_TITLE, title);
		params.putInt(KEY_MESSAGE, message);
		params.putInt(KEY_BUTTON_LABEL, buttonLabel);

		AlertDialogFragment dialog = new AlertDialogFragment();
		dialog.setArguments(params);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		int buttonLabel = params.getInt(KEY_BUTTON_LABEL, -1);

		return new AlertDialog.Builder(getContext())
				.setTitle(params.getInt(KEY_TITLE))
				.setMessage(params.getInt(KEY_MESSAGE))
				.setPositiveButton(buttonLabel, this)
				.setCancelable(false)
				.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (getActivity() instanceof OnAlertListener) {
			((OnAlertListener)getActivity()).onAlert(this);
		}
	}

	/**
	 * ConfirmDialogFragmentを使って表示したダイアログのボタンが押下されたイベントを補足するためのリスナインターフェイス
	 */
	public interface OnAlertListener {
		/**
		 * ダイアログのボタンが押下された時に呼び出されるリスナメソッド
		 * @param dialog		ダイアログ表示に使ったAlertDialogFragmentのインスタンス
		 */
		void onAlert(AlertDialogFragment dialog);
	}
}
