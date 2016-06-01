package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import jp.onetake.binzumejigoku.R;

/**
 * 一定時間ごとに描画処理を繰り返す、内部にタイマーを持つView
 */
public abstract class TimerView extends View {
	/**
	 * タイマーの実行状態
	 */
	public enum TimerStatus {
		Stopped,		// 停止
		Execute,		// 実行中
		WaitForStop,	// 停止待ち
	}

	/**
	 * このViewに発生したイベントを受け取るリスナ
	 */
	public interface TimerListener {
		/**
		 * タイマーが開始された
		 * @param view	TimerView
		 */
		void onStarted(TimerView view);

		/**
		 * タイマーに設定された単位時間が経過した
		 * @param view	TimerView
		 */
		void onPeriod(TimerView view);

		/**
		 * タイマーが停止した
		 * @param view	TimerView
		 */
		void onStopped(TimerView view);
	}

	private static int DEFAULT_PERIOD = 500;

	private Handler mHandler;
	private Timer mTimer;
	private TimerStatus mTimerStatus;
	private TimerListener mListener;
	private int mCounter;
	private long mStartMillis;
	private long mPeriod;

	/**
	 * {@inheritDoc}
	 */
	public TimerView(Context context) {
		this(context, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHandler = new Handler();
		mCounter = 0;
		mTimerStatus = TimerStatus.Stopped;

		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimerView);

			mPeriod = (long) array.getInt(R.styleable.TimerView_period, DEFAULT_PERIOD);

			array.recycle();
		}
	}

	/**
	 * 描画を開始する<br />
	 * 描画が行われたら、つまりタイマーが開始したら呼び出し元にtrueが返る
	 * @return	描画が行われた(タイマーが開始した)かどうか
	 */
	public boolean start() {
		if (mTimerStatus == TimerStatus.Stopped) {
			if (mListener != null) {
				mListener.onStarted(this);
			}

			mTimerStatus = TimerStatus.Execute;
			mCounter = 0;
			mStartMillis = System.currentTimeMillis();

			mTimer = new Timer(true);
			mTimer.schedule(new TimerViewTask(), mPeriod, mPeriod);

			return true;
		}

		return false;
	}

	/**
	 * タイマーによるイベントを補足したい場合リスナオブジェクトをセットする
	 * @param listener	タイマーによるイベントを補足するリスナオブジェクト
	 */
	public void setListener(TimerListener listener) {
		mListener = listener;
	}

	/**
	 * startメソッドが実行されてからの経過時間を返す
	 * @return	startメソッドが実行されてからの経過時間
	 */
	protected long getElapsedMillis() {
		return System.currentTimeMillis() - mStartMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// タイマーが実行されているときであれば次回の描画処理を呼び出す
		if (mTimerStatus == TimerStatus.Execute) {
			if (!executeDraw(canvas, ++mCounter)) {
				mTimerStatus = TimerStatus.WaitForStop;
			}
		// タイマーが止まっているときは前回までの描画処理を復元する
		} else if (mTimerStatus == TimerStatus.Stopped) {
			immediate(canvas);
		}
	}

	/**
	 * 指定した時間間隔で描画処理を行うタスク
	 */
	private class TimerViewTask extends TimerTask {
		@Override
		public void run() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mTimerStatus == TimerStatus.Execute) {
						invalidate();

						if (mListener != null) {
							mListener.onPeriod(TimerView.this);
						}
					} else if (mTimerStatus == TimerStatus.WaitForStop) {
						mTimer.cancel();
						mTimerStatus = TimerStatus.Stopped;

						if (mListener != null) {
							mListener.onStopped(TimerView.this);
						}
					}
				}
			});
		}
	};

	/**
	 * calledCountで呼び出された回数に応じた描画処理を実行する<br />
	 * このメソッドはonDrawで呼び出されるので、目的に応じた処理内容をサブクラスで定義する<br />
	 * メソッドからfalseが返されるとタイマーが停止され、次回のinvalidateは実行されない
	 * @param canvas		キャンバス
	 * @param calledCount	このメソッドが呼び出された回数
	 * @return	次回のタイマー処理も実行する場合はtrue
	 */
	protected abstract boolean executeDraw(Canvas canvas, int calledCount);

	/**
	 * 可能な描画処理を全て実行する<br />
	 * つまり、タイマーが終了した時点と同じ状態に描画する
	 * @param canvas	キャンバス
	 */
	protected abstract void immediate(Canvas canvas);
}
