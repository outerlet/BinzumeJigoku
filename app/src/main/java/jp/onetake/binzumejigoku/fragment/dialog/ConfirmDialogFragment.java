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
	/**
	 * ConfirmDialogFragment、もしくはMultipleConfirmDialogFragmentを使って表示した
	 * ダイアログのボタンが押下されたイベントを捕捉するためのリスナインターフェイス
	 */
	public interface OnConfirmListener {
		/**
		 * ダイアログのボタンが押下された時に呼び出されるリスナメソッド
		 * @param dialog	ダイアログ表示に使ったConfirmDialogFragmentのインスタンス
		 * @param which		押されたボタンを示すint値
		 */
		void onConfirmed(DialogFragment dialog, int which);
	}

	private static final String KEY_TITLE			= "ConfirmDialogFragment.KEY_TITLE";
	private static final String KEY_MESSAGE			= "ConfirmDialogFragment.KEY_MESSAGE";
	private static final String KEY_POSITIVE_LABEL	= "ConfirmDialogFragment.KEY_POSITIVE_LABEL";
	private static final String KEY_NEGATIVE_LABEL	= "ConfirmDialogFragment.KEY_NEGATIVE_LABEL";

	/**
	 * このFragmentのインスタンスを生成する<br />
	 * PositiveラベルとNegativeラベルはそれぞれ「OK」と「キャンセル」
	 * @param title		ダイアログのタイトルリソースID
	 * @param message	ダイアログのメッセージリソースID
	 * @return	このFragmentのインスタンス
	 */
	public static ConfirmDialogFragment newInstance(int title, int message) {
		return newInstance(title, message, -1, -1);
	}

	/**
	 * このFragmentのインスタンスを生成する
	 * @param title		ダイアログのタイトルリソースID
	 * @param message	ダイアログのメッセージリソースID
	 * @param positive	ダイアログのPositiveボタンラベルのリソースID
	 * @param negative	ダイアログのNegativeボタンラベルのリソースID
	 * @return	このFragmentのインスタンス
	 */
	public static ConfirmDialogFragment newInstance(int title, int message, int positive, int negative) {
		Bundle params = new Bundle();
		params.putInt(KEY_TITLE, title);
		params.putInt(KEY_MESSAGE, message);
		params.putInt(KEY_POSITIVE_LABEL, positive);
		params.putInt(KEY_NEGATIVE_LABEL, negative);

		ConfirmDialogFragment dialog = new ConfirmDialogFragment();
		dialog.setArguments(params);
		dialog.setCancelable(false);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
				.setTitle(params.getInt(KEY_TITLE))
				.setMessage(params.getInt(KEY_MESSAGE));

		int positive = params.getInt(KEY_POSITIVE_LABEL, -1);
		builder.setPositiveButton((positive != -1) ? positive : R.string.phrase_ok, this);

		int negative = params.getInt(KEY_NEGATIVE_LABEL, -1);
		builder.setNegativeButton((negative != -1) ? negative : R.string.phrase_cancel, this);

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (getActivity() instanceof OnConfirmListener) {
			((OnConfirmListener)getActivity()).onConfirmed(this, which);
		}
	}
}
