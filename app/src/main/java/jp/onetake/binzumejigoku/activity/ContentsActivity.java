package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.fragment.ContentsFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.fragment.dialog.MultipleConfirmDialogFragment;

public class ContentsActivity extends BasicActivity
		implements ContentsFragment.SectionListener, ConfirmDialogFragment.OnConfirmListener {
	public static String KEY_SECTION_INDEX		= "ContentsActivity.KEY_SECTION_INDEX";

	private final String TAG_SECTION_FRAGMENT	= "ContentsActivity.TAG_SECTION_FRAGMENT";
	private final String TAG_DIALOG_SECTION		= "ContentsActivity.TAG_DIALOG_SECTION";
	private final String TAG_DIALOG_BACKKEY		= "ContentsActivity.TAG_DIALOG_BACKKEY";

	private ContentsHolder mHolder;
	private int mSectionIndex;
	private boolean mIsOngoing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contents);

		mSectionIndex = getIntent().getIntExtra(KEY_SECTION_INDEX, -1);
		if (mSectionIndex == -1) {
			throw new UnsupportedOperationException(this.getClass().getName() + " : Invalid section index.");
		}

		mHolder = new ContentsHolder(this, mSectionIndex);
		mIsOngoing = false;

		replaceFragment();
	}

	@Override
	public void onBackPressed() {
		if (!mIsOngoing) {
			ConfirmDialogFragment.newInstance(
					R.string.phrase_confirm,
					R.string.message_confirm_back,
					R.string.phrase_back,
					R.string.phrase_cancel).show(getSupportFragmentManager(), TAG_DIALOG_BACKKEY);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && !mIsOngoing) {
			advance();
		}

		return true;
	}

	private void advance() {
		if (mHolder.hasNext()) {
			while (true) {
				SectionElement elm = mHolder.next();

				mIsOngoing = (elm instanceof Title || elm instanceof Text || elm instanceof Image);

				ContentsFragment fragment = (ContentsFragment)getSupportFragmentManager().findFragmentByTag(TAG_SECTION_FRAGMENT);
				fragment.advance(elm);

				// "chain"属性が"immediate"以外ならとっとと抜ける
				if (elm.getChainType() != SectionElement.ChainType.Immediate) {
					break;
				}
			}
		} else {
			mIsOngoing = false;

			MultipleConfirmDialogFragment.newInstance(
					R.string.phrase_confirm,
					R.string.message_section_finished,
					R.string.phrase_next_section,
					R.string.phrase_back,
					R.string.phrase_finish_application).show(getSupportFragmentManager(), TAG_DIALOG_SECTION);
		}
	}

	@Override
	public void onAdvanced() {
		// "chain"属性が"wait"ならもう1つ先に進める
		if (mHolder.current().getChainType() == SectionElement.ChainType.Wait) {
			advance();
		} else {
			mIsOngoing = false;
		}
	}

	@Override
	public void onConfirm(DialogFragment dialog, int which) {
		// Backキーを押した時のダイアログ
		if (dialog.getTag().equals(TAG_DIALOG_BACKKEY)) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
			}
		// 章が終わった時に表示されるダイアログ
		} else if (dialog.getTag().equals(TAG_DIALOG_SECTION)) {
			// 次の章へ
			if (which == DialogInterface.BUTTON_POSITIVE) {
				mHolder = new ContentsHolder(this, ++mSectionIndex);
				replaceFragment();
			// 戻る
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				finish();
			// アプリを終了
			} else {
				finishApplication();
			}
		}
	}

	private void replaceFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.layout_fragment_container, new ContentsFragment(), TAG_SECTION_FRAGMENT);
		transaction.commit();
	}
}
