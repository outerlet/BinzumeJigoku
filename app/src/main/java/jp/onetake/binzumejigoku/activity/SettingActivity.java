package jp.onetake.binzumejigoku.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.fragment.SettingFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;

/**
 * アプリの設定画面。この画面でできることは以下の通り
 * <ul>
 *     <li>テキストサイズの変更</li>
 *     <li>文字送り速度の変更</li>
 *     <li>チュートリアルを読む</li>
 *     <li>「瓶詰地獄」という作品についての説明</li>
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

		// チュートリアル
		if (prefKey.equals(getString(R.string.prefkey_tutorial_nosave))) {
			startActivity(new Intent(this, TutorialActivity.class));
			return true;
		}

		// 「瓶詰地獄」について
		if (prefKey.equals(getString(R.string.prefkey_about_work_nosave))) {
			Point point = new Point(0, 0);
			Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			display.getSize(point);
			int marginWidth = getResources().getDimensionPixelSize(R.dimen.popup_margin_width_about_work);
			int marginHeight = getResources().getDimensionPixelSize(R.dimen.popup_margin_height_about_work);

			PopupWindow popup = new PopupWindow(this);
			popup.setWidth(point.x - marginWidth * 2);
			popup.setHeight(point.y - marginHeight * 2);
			popup.setOutsideTouchable(false);
			popup.setFocusable(true);

			View view = LayoutInflater.from(this).inflate(R.layout.popup_about_work, null);
			((TextView)view.findViewById(R.id.textview_about_work)).setText(getString(R.string.text_about_this_work));
			popup.setContentView(view);

			popup.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);

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
