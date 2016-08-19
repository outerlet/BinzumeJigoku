package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.fragment.SettingFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;

/**
 * 設定画面を開く<br />
 * この画面でできることは以下
 * <ul>
 *     <li>テキストサイズの変更</li>
 *     <li>文字送り速度の変更</li>
 * </ul>
 */
public class SettingActivity extends BasicActivity
		implements Preference.OnPreferenceClickListener, ConfirmDialogFragment.OnConfirmListener {
	private final String TAG_FRAGMENT_SETTING			= "SettingActivity.TAG_FRAGMENT_SETTING";
	private final String TAG_DIALOG_CONFIRM_DONATION	= "SettingActivity.TAG_DIALOG_CONFIRM_DONATION";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

		postFragment(R.id.layout_fragment_container, FragmentMethod.Replace, new SettingFragment(), TAG_FRAGMENT_SETTING);
	}

	/**
	 * <p>
	 *     SettingFragmentで特定のメニューをタップしたときのイベントを捕捉する
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPreferenceClick(Preference preference) {
		String prefKey = preference.getKey();

		// ご寄付
		// InAppBillingを使って課金するロジックは組み込み済みだが、Playに住所を出さないといけないとか色々あるので当面はコメントアウト
		/*
		if (prefKey.equals(getString(R.string.prefkey_donation_nosave))) {
			showDialogFragment(
					ConfirmDialogFragment.newInstance(
							R.string.phrase_confirm, R.string.message_confirm_for_purchase, R.string.phrase_execute_donation, R.string.phrase_cancel_donation),
					TAG_DIALOG_CONFIRM_DONATION);

			return true;
		}
		*/

		// 「瓶詰地獄」について
		if (prefKey.equals(getString(R.string.prefkey_about_work_nosave))) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		if (dialog.getTag().equals(TAG_DIALOG_CONFIRM_DONATION)) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				startActivity(new Intent(this, PurchaseActivity.class));
			}
		}

		dialog.dismiss();
	}
}
