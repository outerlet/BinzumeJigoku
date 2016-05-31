package jp.onetake.binzumejigoku.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 3つのボタンがある確認用のダイアログを表示するためのDialogFragment
 */
public class MultipleConfirmDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
	private static final String KEY_TITLE			= "MultipleConfirmDialogFragment.KEY_TITLE";
	private static final String KEY_MESSAGE			= "MultipleConfirmDialogFragment.KEY_MESSAGE";
	private static final String KEY_POSITIVE_LABEL	= "MultipleConfirmDialogFragment.KEY_POSITIVE_LABEL";
	private static final String KEY_NEGATIVE_LABEL	= "MultipleConfirmDialogFragment.KEY_NEGATIVE_LABEL";
	private static final String KEY_NEUTRAL_LABEL	= "MultipleConfirmDialogFragment.KEY_NEUTRAL_LABEL";

	/**
	 * このDialogFragmentを生成する<br />
	 * 3択なので規定のボタンラベル("OK"とか"キャンセル")は用意していない
	 * @param title			ダイアログのタイトル
	 * @param message		ダイアログのメッセージ
	 * @param positiveLabel	Positiveボタンのラベル文字列
	 * @param neutralLabel	Neutralボタンのラベル文字列
	 * @param negativeLabel	Negativeボタンのラベル文字列
	 * @return	パラメータに指定した内容に基づくこのクラスのインスタンス
	 */
	public static MultipleConfirmDialogFragment newInstance(int title, int message, int positiveLabel, int negativeLabel, int neutralLabel) {
		Bundle params = new Bundle();
		params.putInt(KEY_TITLE, title);
		params.putInt(KEY_MESSAGE, message);
		params.putInt(KEY_POSITIVE_LABEL, positiveLabel);
		params.putInt(KEY_NEGATIVE_LABEL, negativeLabel);
		params.putInt(KEY_NEUTRAL_LABEL, neutralLabel);

		MultipleConfirmDialogFragment dialog = new MultipleConfirmDialogFragment();
		dialog.setArguments(params);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		return new AlertDialog.Builder(getContext())
				.setTitle(params.getInt(KEY_TITLE))
				.setMessage(params.getInt(KEY_MESSAGE))
				.setPositiveButton(params.getInt(KEY_POSITIVE_LABEL, -1), this)
				.setNegativeButton(params.getInt(KEY_NEGATIVE_LABEL, -1), this)
				.setNeutralButton(params.getInt(KEY_NEUTRAL_LABEL, -1), this)
				.setCancelable(false)
				.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (getActivity() instanceof ConfirmDialogFragment.OnConfirmListener) {
			((ConfirmDialogFragment.OnConfirmListener)getActivity()).onConfirmed(this, which);
		}
	}
}
