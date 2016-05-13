package jp.onetake.binzumejigoku.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.onetake.binzumejigoku.R;

/**
 * 2つのボタンがある確認用のダイアログを表示するためのDialogFragment
 */
public class ConfirmDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
	private static final String KEY_DIALOG_TITLE			= "ConfirmDialogFragment.KEY_DIALOG_TITLE";
	private static final String KEY_DIALOG_MESSAGE			= "ConfirmDialogFragment.KEY_DIALOG_MESSAGE";
	private static final String KEY_DIALOG_POSITIVE_LABEL	= "ConfirmDialogFragment.KEY_DIALOG_POSITIVE_LABEL";
	private static final String KEY_DIALOG_NEGATIVE_LABEL	= "ConfirmDialogFragment.KEY_DIALOG_NEGATIVE_LABEL";

	/**
	 * このFragmentのインスタンスを生成する<br />
	 * ボタンのラベルは「OK」と「キャンセル」になる
	 * @param title		ダイアログのタイトル文字列
	 * @param message	ダイアログのメッセージ
	 * @return	このFragmentのインスタンス
	 */
	public static ConfirmDialogFragment newInstance(String title, String message) {
		return newInstance(title, message, null, null);
	}

	/**
	 * このFragmentのインスタンスを生成する<br />
	 * 第3引数、第4引数に与えた文字列がそれぞれPositiveボタンとNegativeボタンのラベルとなる
	 * @param title			ダイアログのタイトル文字列
	 * @param message		ダイアログのメッセージ
	 * @param positiveLabel	ダイアログのPositiveボタン用ラベル文字列
	 * @param negativeLabel	ダイアログのNegativeボタン用ラベル文字列
	 * @return				このFragmentのインスタンス
	 */
	public static ConfirmDialogFragment newInstance(String title, String message, String positiveLabel, String negativeLabel) {
		Bundle params = new Bundle();
		params.putString(KEY_DIALOG_TITLE, title);
		params.putString(KEY_DIALOG_MESSAGE, message);
		params.putString(KEY_DIALOG_POSITIVE_LABEL, positiveLabel);
		params.putString(KEY_DIALOG_NEGATIVE_LABEL, negativeLabel);

		ConfirmDialogFragment dialog = new ConfirmDialogFragment();
		dialog.setArguments(params);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
				.setTitle(params.getString(KEY_DIALOG_TITLE))
				.setMessage(params.getString(KEY_DIALOG_MESSAGE))
				.setCancelable(false);

		String posLabel = params.getString(KEY_DIALOG_POSITIVE_LABEL, null);
		if (posLabel != null) {
			builder.setPositiveButton(posLabel, this);
		} else {
			builder.setPositiveButton(R.string.phrase_ok, this);
		}

		String negLabel = params.getString(KEY_DIALOG_NEGATIVE_LABEL, null);
		if (negLabel != null) {
			builder.setNegativeButton(negLabel, this);
		} else {
			builder.setNegativeButton(R.string.phrase_cancel, this);
		}

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (getActivity() instanceof OnConfirmListener) {
			((OnConfirmListener)getActivity()).onConfirm(this, which == DialogInterface.BUTTON_POSITIVE);
		}
	}

	/**
	 * ConfirmDialogFragmentを使って表示したダイアログのボタンが押下されたイベントを補足するためのリスナインターフェイス
	 */
	public interface OnConfirmListener {
		/**
		 * ダイアログのボタンが押下された時に呼び出されるリスナメソッド
		 * @param dialog		ダイアログ表示に使ったConfirmDialogFragmentのインスタンス
		 * @param isPositive	Positiveボタンを押されたかどうか。trueならPositive、falseならNegative
		 */
		void onConfirm(ConfirmDialogFragment dialog, boolean isPositive);
	}
}
