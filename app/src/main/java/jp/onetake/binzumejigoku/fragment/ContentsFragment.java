package jp.onetake.binzumejigoku.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.activity.BacklogActivity;
import jp.onetake.binzumejigoku.activity.SaveActivity;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.SaveData;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.view.ContentsImageView;
import jp.onetake.binzumejigoku.view.ContentsTextView;
import jp.onetake.binzumejigoku.view.ContentsTitleView;
import jp.onetake.binzumejigoku.view.TimerView;

/**
 * 物語を進めるフラグメント
 */
public class ContentsFragment extends Fragment implements TimerView.TimerListener, ContentsImageView.EffectListener {
	/**
	 * コンテンツを進行させるうえで発生しうるイベントを捕捉するためのリスナ
	 */
	public interface ContentsEventListener {
		/**
		 * 章が終了したイベントを捉えるリスナメソッド
		 */
		void onSectionFinished();

		/**
		 * セーブ＆ロード画面で任意のスロットが指定されてロードが要求されたイベントを捉えるリスナメソッド
		 * @param slotIndex
		 */
		void onLoadRequested(int slotIndex);
	}

	private static final String KEY_SECTION_INDEX	= "ContentsFragment.KEY_SECTION_INDEX";
	private static final String KEY_SAVE_DATA		= "ContentsFragment.KEY_SAVE_DATA";

	private ContentsImageView mImageView;
	private ContentsTitleView mTitleView;
	private ContentsTextView mTextView;

	private ContentsHolder mHolder;
	private boolean mIsOngoing;
	private int mAdvanceCount;

	/**
	 * 章番号からインスタンスを生成する<br />
	 * 章を最初から始める場合はこちら
	 * @param sectionIndex	章番号
	 * @return	このクラスのインスタンス
	 */
	public static ContentsFragment newInstance(int sectionIndex) {
		Bundle params = new Bundle();
		params.putInt(KEY_SECTION_INDEX, sectionIndex);

		ContentsFragment fragment = new ContentsFragment();
		fragment.setArguments(params);
		return fragment;
	}

	/**
	 * セーブデータからインスタンスを生成する<br />
	 * 章を途中から始める場合はこちら
	 * @param saveData	セーブデータ
	 * @return	このクラスのインスタンス
	 */
	public static ContentsFragment newInstance(SaveData saveData) {
		Bundle params = new Bundle();
		params.putSerializable(KEY_SAVE_DATA, saveData);

		ContentsFragment fragment = new ContentsFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contents, container, false);

		mImageView = (ContentsImageView)view.findViewById(R.id.imageview_contents);
		mImageView.setListener(this);

		mTitleView = (ContentsTitleView)view.findViewById(R.id.titleview_title);
		mTitleView.setListener(this);

		ContentsInterface cif = ContentsInterface.getInstance();
		mTextView = (ContentsTextView)view.findViewById(R.id.textview_contents);
		mTextView.setListener(this);
		mTextView.setPeriod(cif.getTextPeriod());
		mTextView.setTextSize(cif.getTextSize(), cif.getRubySize());

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle params = getArguments();

		// 最初から
		if (params.containsKey(KEY_SECTION_INDEX)) {
			mHolder = new ContentsHolder(getActivity(), params.getInt(KEY_SECTION_INDEX));
		// セーブデータから
		} else if (params.containsKey(KEY_SAVE_DATA)) {
			SaveData saveData = (SaveData)params.getSerializable(KEY_SAVE_DATA);

			mHolder = new ContentsHolder(getActivity(), saveData);

			for (SectionElement e : mHolder.getLatestElementList()) {
				android.util.Log.i("CHECK", "Type = " + e.getContentsType().toString());

				switch (e.getContentsType()) {
					case Title:
						mTitleView.setTitle((Title)e);
						mTitleView.immediate();
						break;
					case Text:
						mTextView.setText((Text)e);
						mTextView.immediate();
						break;
					case Image:
						Image img = (Image)e;
						mImageView.setImage(img);
						mImageView.immediate();
						break;
				}
			}
		// それ以外の呼び出し方法(newでインスタンスを作成したとか)は無効
		} else {
			throw new IllegalArgumentException(getString(R.string.exception_illegal_contents_fragment));
		}

		mIsOngoing = false;
		mAdvanceCount = 0;
	}

	@Override
	public void onResume() {
		super.onResume();

		// 開始と同時にひとつだけ物語を進める
		// セーブ・ロード画面やバックログから戻ってきた場合は例外
		if (mAdvanceCount == 0) {
			advance();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		// スレッドが動作しているようなら止めておく
		mTitleView.cancel();
		mTextView.cancel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_save_activity)) {
			// セーブもしくはロードした場合はEXTRA_SAVE_MODEにboolean値が入っている
			if (data != null && data.hasExtra(SaveActivity.EXTRA_SAVE_MODE)) {
				boolean isSave = data.getBooleanExtra(SaveActivity.EXTRA_SAVE_MODE, false);

				// セーブ
				if (isSave) {
					int msgId = (resultCode == Activity.RESULT_OK) ? R.string.message_save_successful : R.string.message_save_failure;
					Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
					// ロード
				} else {
					int slotIndex = data.getIntExtra(SaveActivity.EXTRA_SLOT_INDEX, -1);

					if (slotIndex != -1 && getActivity() instanceof ContentsFragment.ContentsEventListener) {
						((ContentsEventListener)getActivity()).onLoadRequested(slotIndex);
					} else {
						Toast.makeText(getActivity(), R.string.message_load_failure, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	/**
	 * 物語をひとつ先に進める
	 */
	public void advance() {
		if (!mIsOngoing) {
			if (mHolder.hasNext()) {
				SectionElement element = mHolder.next();

				mIsOngoing = (element instanceof Title || element instanceof Text || element instanceof Image);

				// onResumeで物語を進めるとエフェクトに引っかかりができてしまうので、最初だけは遅延時間を設けておく
				long delay = (mAdvanceCount == 0) ? getResources().getInteger(R.integer.delay_millis_section_start) : 0;

				switch (element.getContentsType()) {
					case Title:
						mTitleView.setTitle((Title)element);
						mTitleView.setVisibility(View.VISIBLE);
						mTitleView.start(delay);
						break;
					case Text:
						mTextView.setText((Text)element);
						mTextView.start(delay);
						break;
					case ClearText:
						mTextView.clear();
						break;
					case Image:
						mImageView.setImage((Image)element);
						mImageView.start(delay);
						break;
					default:
						// Do nothing.
				}

				++mAdvanceCount;

				// "chain"属性が"immediate"なら更に進める
				if (element.getChainType() == SectionElement.ChainType.Immediate) {
					advance();
				}
			} else {
				if (getActivity() instanceof ContentsEventListener) {
					((ContentsEventListener)getActivity()).onSectionFinished();
				}
			}
		} else {
			mTextView.setPeriod(getResources().getInteger(R.integer.text_period_millis_fast_forward));
		}
	}

	/**
	 * セーブ画面を開く<br />
	 * テキストが進んでいるときなど、画面上で何らかの演出が行われているときは開かない
	 * @return	セーブ画面が開いたかどうか。開いた場合はtrue
	 */
	public boolean requestSaveActivity() {
		if (!mIsOngoing) {
			startActivityForResult(new Intent(getActivity(), SaveActivity.class),
					getResources().getInteger(R.integer.request_code_save_activity));
			getActivity().overridePendingTransition(0, 0);

			return true;
		}

		return false;
	}

	/**
	 * バックログを開く<br />
	 * テキストが進んでいるときなど、画面上で何らかの演出が行われているときは開かない
	 * @return	バックログが開いたかどうか。開いた場合はtrue
	 */
	public boolean requestBacklogActivity() {
		if (!mIsOngoing) {
			startActivity(new Intent(getActivity(), BacklogActivity.class));
			return true;
		}

		return false;
	}

	/**
	 * "chain"属性が指定されているならユーザーからのインタラクションがなくても物語を先に進める
	 */
	private void advanceIfChained() {
		mIsOngoing = false;

		if (mHolder.current().getChainType() == SectionElement.ChainType.Wait) {
			advance();
		}
	}

	@Override
	public void onTimerStarted(TimerView view) {
		if (!(view instanceof ContentsTitleView) && mTitleView.getVisibility() == View.VISIBLE) {
			mTitleView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onTimerPeriod(TimerView view) {
		// Do nothing.
	}

	@Override
	public void onTimerStopped(TimerView view) {
		if (view instanceof ContentsTitleView) {
			view.setVisibility(View.INVISIBLE);
		} else if (view instanceof ContentsTextView) {
			if (mTextView.getPeriod() == getResources().getInteger(R.integer.text_period_millis_fast_forward)) {
				mTextView.setPeriod(ContentsInterface.getInstance().getTextPeriod());
			}
		}

		advanceIfChained();
	}

	@Override
	public void onEffectFinished(ContentsImageView view) {
		advanceIfChained();
	}
}
