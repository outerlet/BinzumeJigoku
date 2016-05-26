package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import jp.onetake.binzumejigoku.R;


public abstract class TimerView extends View {
	public enum TimerStatus {
		Stopped,
		Execute,
		WaitForStop,
	}

	public interface TimerListener {
		void onStarted(TimerView view);
		void onPeriod(TimerView view);
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

	public TimerView(Context context) {
		this(context, null);
	}

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

	public void setListener(TimerListener listener) {
		mListener = listener;
	}

	public TimerStatus getStatus() {
		return mTimerStatus;
	}

	protected long getElapsedMillis() {
		return System.currentTimeMillis() - mStartMillis;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mTimerStatus == TimerStatus.Execute && !executeDraw(canvas, ++mCounter)) {
			mTimerStatus = TimerStatus.WaitForStop;
		}
	}

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
	 * onDrawで実行したい処理を定義する<br />
	 * このメソッドからfalseが返されるとタイマーが停止され次回のinvalidateは行われなくなる
	 * @param canvas
	 * @param calledCount
	 * @return	次回のタイマー処理も実行する場合はtrue
	 */
	protected abstract boolean executeDraw(Canvas canvas, int calledCount);
}
