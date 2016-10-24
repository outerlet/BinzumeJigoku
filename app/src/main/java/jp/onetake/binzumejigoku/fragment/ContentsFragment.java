package jp.onetake.binzumejigoku.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.activity.BacklogActivity;
import jp.onetake.binzumejigoku.activity.SaveActivity;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.SaveData;
import jp.onetake.binzumejigoku.contents.element.ClearText;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.contents.element.Wait;
import jp.onetake.binzumejigoku.view.ContentsImageView;
import jp.onetake.binzumejigoku.view.ContentsTextView;
import jp.onetake.binzumejigoku.view.ContentsTitleView;
import jp.onetake.binzumejigoku.view.TimerView;

/**
 * ContentsActivity上に配置され、ユーザーからのインタラクションに応じて
 * 物語の進行を制御するフラグメント
 */
public class ContentsFragment extends BasicFragment implements TimerView.TimerListener, ContentsImageView.EffectListener {
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
		 * @param slotIndex	ロードされる対象のセーブデータに対応するインデックス値
		 */
		void onLoadRequested(int slotIndex);
	}

	/**
	 * Wait要素によって発生する待機時間が終了したあとに投げられるメッセージを捕捉するハンドラクラス
	 */
	private class WaitHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_WHAT_WAIT_FINISHED) {
				mIsOngoing = false;
				advance();
			}
		}
	}

	private static final String KEY_SECTION_INDEX	= "ContentsFragment.KEY_SECTION_INDEX";
	private static final String KEY_SAVE_DATA		= "ContentsFragment.KEY_SAVE_DATA";

	private final int MESSAGE_WHAT_WAIT_FINISHED	= 10001;

	private ContentsImageView mImageView;
	private ContentsTitleView mTitleView;
	private ContentsTextView mTextView;

	private ImageView mIndicatorView;
	private ObjectAnimator mIndicatorAnimation;

	private ContentsHolder mHolder;
	private boolean mIsOngoing;
	private int mAdvancedCount;

	private Handler mWaitHandler = new WaitHandler();

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

	/**
	 * {@inheritDoc}
	 */
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

		mIndicatorView = (ImageView)view.findViewById(R.id.imageview_advance_indicator);

		return view;
	}

	/**
	 * {@inheritDoc}
	 */
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

		// タップ待ちであることを示すインジケータ
		mIndicatorAnimation = ObjectAnimator.ofFloat(mIndicatorView, "alpha", 0.0f, 1.0f);
		mIndicatorAnimation.setRepeatMode(ValueAnimator.REVERSE);
		mIndicatorAnimation.setRepeatCount(ValueAnimator.INFINITE);
		mIndicatorAnimation.setDuration(getResources().getInteger(R.integer.duration_indicator_switch));

		mIsOngoing = false;
		mAdvancedCount = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume() {
		super.onResume();

		// 開始と同時にひとつだけ物語を進める
		// セーブ・ロード画面やバックログから戻ってきた場合は例外
		if (mAdvancedCount == 0) {
			advance();
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
	@SuppressWarnings("ConstantConditions")
	public void advance() {
		if (!mIsOngoing) {
			if (mIndicatorAnimation.isRunning()) {
				mIndicatorAnimation.cancel();
				mIndicatorView.setAlpha(0.0f);
			}

			if (mHolder.hasNext()) {
				SectionElement element = mHolder.next();

				mIsOngoing = !(element instanceof ClearText);

				// onResumeで物語を進めるとエフェクトに引っかかりができてしまうので、最初だけは遅延時間を設けておく
				long delay = (mAdvancedCount == 0) ? getResources().getInteger(R.integer.delay_millis_section_start) : 0;

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
					case Image:
						mImageView.setImage((Image)element);
						mImageView.start(delay);
						break;
					case Wait:
						final long duration = ((Wait)element).getDuration();
						(new Thread() {
							@Override
							public void run() {
								try {
									Thread.sleep(duration);
								} catch (InterruptedException ignored) {}

								mWaitHandler.obtainMessage(MESSAGE_WHAT_WAIT_FINISHED).sendToTarget();
							}
						}).start();
						break;
					case ClearText:
						mTextView.clear();
						break;
					default:
						// Do nothing.
				}

				++mAdvancedCount;

				// "chain"属性が"immediate"なら更に進める
				if (element.getChainType() == SectionElement.ChainType.Immediate) {
					advance();
				}
			} else {
				// セクションが最後まで進んだのでオートセーブデータは利用できないものとしてマーク
				ContentsInterface.getInstance().getSaveData(0).markAsUnusable();

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
	 * 今しがた処理した要素に"chain"属性が指定されているかどうか判定し、指定されていれば
	 * ユーザーからのインタラクション無しでも物語を先に進める
	 * @return 物語を先に進めた(chain属性が指定されていた)場合はtrue
	 */
	private boolean advanceIfChained() {
		mIsOngoing = false;

		if (mHolder.current().getChainType() == SectionElement.ChainType.Wait) {
			advance();
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimerStarted(TimerView view) {
		if (!(view instanceof ContentsTitleView) && mTitleView.getVisibility() == View.VISIBLE) {
			mTitleView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param view	TimerView
	 */
	@Override
	public void onTimerPeriod(TimerView view) {
		// Do nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTimerStopped(TimerView view) {
		if (view instanceof ContentsTitleView) {
			view.setVisibility(View.INVISIBLE);
		} else if (view instanceof ContentsTextView) {
			if (mTextView.getPeriod() == getResources().getInteger(R.integer.text_period_millis_fast_forward)) {
				mTextView.setPeriod(ContentsInterface.getInstance().getTextPeriod());
			}
		}

		// 自動的に物語が進まないならページを進められることを示すアニメーション
		if (!advanceIfChained()) {
			mIndicatorAnimation.start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEffectFinished(ContentsImageView view) {
		// 自動的に物語が進まないならページを進められることを示すアニメーション
		if (!advanceIfChained()) {
			mIndicatorAnimation.start();
		}
	}
}
