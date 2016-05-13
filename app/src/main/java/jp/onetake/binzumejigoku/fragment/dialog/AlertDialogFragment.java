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
	private static final String KEY_DIALOG_TITLE	= "AlertDialogFragment.KEY_DIALOG_TITLE";
	private static final String KEY_DIALOG_MESSAGE	= "AlertDialogFragment.KEY_DIALOG_MESSAGE";

	/**
	 * このFragmentのインスタンスを生成する
	 * @param title		ダイアログのタイトル文字列
	 * @param message	ダイアログのメッセージ
	 * @return	このFragmentのインスタンス
	 */
	public static AlertDialogFragment newInstance(String title, String message) {
		Bundle params = new Bundle();
		params.putString(KEY_DIALOG_TITLE, title);
		params.putString(KEY_DIALOG_MESSAGE, message);

		AlertDialogFragment dialog = new AlertDialogFragment();
		dialog.setArguments(params);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		return new AlertDialog.Builder(getContext())
				.setTitle(params.getString(KEY_DIALOG_TITLE))
				.setMessage(params.getString(KEY_DIALOG_MESSAGE))
				.setPositiveButton(R.string.phrase_ok, this)
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
