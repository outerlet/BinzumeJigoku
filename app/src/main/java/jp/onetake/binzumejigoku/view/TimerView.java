package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import jp.onetake.binzumejigoku.R;

/**
 * 一定の時間間隔ごとに描画処理を繰り返すView
 */
public abstract class TimerView extends View {
	/**
	 * タイマーの実行状態
	 */
	public enum TimerStatus {
		Stopped,		// 停止
		WaitForStart,	// 実行待ち
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
		void onTimerStarted(TimerView view);

		/**
		 * タイマーに設定された単位時間が経過した
		 * @param view	TimerView
		 */
		void onTimerPeriod(TimerView view);

		/**
		 * タイマーが停止した
		 * @param view	TimerView
		 */
		void onTimerStopped(TimerView view);
	}

	private static int DEFAULT_PERIOD = 500;

	private Handler mHandler;
	private TimerStatus mTimerStatus;
	private TimerListener mListener;
	private TimerViewThread mCurrentThread;
	private int mCounter;
	private long mStartMillis;
	private long mPeriodMillis;

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
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerView);

			mPeriodMillis = (long) typedArray.getInt(R.styleable.TimerView_period, DEFAULT_PERIOD);

			typedArray.recycle();
		}
	}

	/**
	 * タイマーの実行間隔を指定する
	 * @param millis	実行間隔(ms)
	 */
	public void setPeriod(long millis) {
		mPeriodMillis = millis;
	}

	/**
	 * タイマーの実行間隔を指定する
	 * @return	実行間隔(ms)
	 */
	public long getPeriod() {
		return mPeriodMillis;
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
	 * 描画を開始する<br />
	 * 描画が行われたら、つまりタイマーが開始したら呼び出し元にtrueが返る
	 * @param delay タイマー開始までの遅延時間(ms)
	 * @return	描画が行われた(タイマーが開始した)かどうか
	 */
	public boolean start(long delay) {
		if (mTimerStatus == TimerStatus.Stopped) {
			if (mListener != null) {
				mListener.onTimerStarted(this);
			}

			mTimerStatus = TimerStatus.WaitForStart;
			mCounter = 0;

			mCurrentThread = new TimerViewThread(delay);
			mCurrentThread.start();

			return true;
		}

		return false;
	}

	/**
	 * タイマーによらず、できるものを全て描画する
	 */
	public void immediate() {
		mTimerStatus = TimerStatus.Stopped;
		invalidate();
	}

	/**
	 * タイマーを停止する
	 */
	public void cancel() {
		if (mTimerStatus != TimerStatus.Stopped) {
			mTimerStatus = TimerStatus.WaitForStop;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (mTimerStatus) {
			case Execute:
				if (!drawByPeriod(canvas, ++mCounter)) {
					mTimerStatus = TimerStatus.WaitForStop;
				}
				break;
			case WaitForStop:
				drawByPeriod(canvas, mCounter);
				break;
			case Stopped:
				drawAll(canvas);
				break;
			default:
				// 何もしない
		}
	}

	/**
	 * 指定した時間間隔で描画処理を行うスレッド
	 */
	private class TimerViewThread extends Thread {
		private long mmDelay;

		public TimerViewThread(long delay) {
			mmDelay = delay;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(mmDelay);
			} catch (InterruptedException ire) {}

			mTimerStatus = TimerStatus.Execute;
			mStartMillis = System.currentTimeMillis();

			while (mTimerStatus == TimerStatus.Execute) {
				try {
					Thread.sleep(mPeriodMillis);
				} catch (InterruptedException ire) {}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						invalidate();

						if (mListener != null) {
							mListener.onTimerPeriod(TimerView.this);
						}
					}
				});
			}

			mTimerStatus = TimerStatus.Stopped;

			// コールバックはUIスレッドに戻す
			if (mListener != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mListener.onTimerStopped(TimerView.this);
					}
				});
			}
		}
	}

	/**
	 * calledCountで呼び出された回数に応じた描画処理を実行する<br />
	 * このメソッドはonDrawで呼び出されるので、目的に応じた処理内容をサブクラスで定義する<br />
	 * メソッドからfalseが返されるとタイマーが停止され、次回のinvalidateは実行されない
	 * @param canvas		キャンバス
	 * @param calledCount	このメソッドが呼び出された回数
	 * @return	次回のタイマー処理も実行する場合はtrue
	 */
	protected abstract boolean drawByPeriod(Canvas canvas, int calledCount);

	/**
	 * 描画処理を全て一度に行う<br />
	 * つまりタイマーが終了した時点と同じ状態に描画する
	 * @param canvas	キャンバス
	 */
	protected abstract void drawAll(Canvas canvas);
}
