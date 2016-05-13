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
	private static final String KEY_DIALOG_TITLE			= "MultipleConfirmDialogFragment.KEY_DIALOG_TITLE";
	private static final String KEY_DIALOG_MESSAGE			= "MultipleConfirmDialogFragment.KEY_DIALOG_MESSAGE";
	private static final String KEY_DIALOG_POSITIVE_LABEL	= "MultipleConfirmDialogFragment.KEY_DIALOG_POSITIVE_LABEL";
	private static final String KEY_DIALOG_NEGATIVE_LABEL	= "MultipleConfirmDialogFragment.KEY_DIALOG_NEGATIVE_LABEL";
	private static final String KEY_DIALOG_NEUTRAL_LABEL	= "MultipleConfirmDialogFragment.KEY_DIALOG_NEUTRAL_LABEL";

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
	public static MultipleConfirmDialogFragment newInstance(String title, String message, String positiveLabel, String negativeLabel, String neutralLabel) {
		Bundle params = new Bundle();
		params.putString(KEY_DIALOG_TITLE, title);
		params.putString(KEY_DIALOG_MESSAGE, message);
		params.putString(KEY_DIALOG_POSITIVE_LABEL, positiveLabel);
		params.putString(KEY_DIALOG_NEGATIVE_LABEL, negativeLabel);
		params.putString(KEY_DIALOG_NEUTRAL_LABEL, neutralLabel);

		MultipleConfirmDialogFragment dialog = new MultipleConfirmDialogFragment();
		dialog.setArguments(params);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		return new AlertDialog.Builder(getContext())
				.setTitle(params.getString(KEY_DIALOG_TITLE))
				.setMessage(params.getString(KEY_DIALOG_MESSAGE))
				.setPositiveButton(params.getString(KEY_DIALOG_POSITIVE_LABEL, null), this)
				.setNegativeButton(params.getString(KEY_DIALOG_NEGATIVE_LABEL, null), this)
				.setNeutralButton(params.getString(KEY_DIALOG_NEUTRAL_LABEL, null), this)
				.setCancelable(false)
				.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (getActivity() instanceof OnConfirmListener) {
			((OnConfirmListener)getActivity()).onConfirm(this, which);
		}
	}

	/**
	   MultipleConfirmDialogFragmentを使って表示したダイアログのボタンが押下されたイベントを補足するためのリスナインターフェイス
	 */
	public interface OnConfirmListener {
		/**
		 * ダイアログのボタンが押下された時に呼び出されるリスナメソッド
		 * @param dialog	ダイアログ表示に使ったMultipleConfirmDialogFragmentのインスタンス
		 * @param which		どのボタンが押されたかを示すDialogInterfaceの定数
		 */
		void onConfirm(MultipleConfirmDialogFragment dialog, int which);
	}
}
