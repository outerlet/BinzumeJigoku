package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;

public class MainActivity extends BasicActivity
		implements View.OnClickListener, ConfirmDialogFragment.OnConfirmListener {
	public static final String INTENT_KEY_FINISH_APP	= "MainActivity.INTENT_KEY_FINISH_APP";

	private Handler mHandler;
	private int mBackPressCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(INTENT_KEY_FINISH_APP, false)) {
			finish();
		} else {
			setContentView(R.layout.activity_main);

			findViewById(R.id.button_section0).setOnClickListener(this);
			findViewById(R.id.button_section1).setOnClickListener(this);
			findViewById(R.id.button_section2).setOnClickListener(this);
			findViewById(R.id.button_section3).setOnClickListener(this);
			findViewById(R.id.button_query_database).setOnClickListener(this);

			mHandler = new Handler();
			mBackPressCount = 0;
		}
	}

	@Override
	public void onBackPressed() {
		if (mBackPressCount > 0) {
			finish();
		} else {
			++mBackPressCount;

			Toast.makeText(this, R.string.message_confirm_finish_application, Toast.LENGTH_LONG).show();

			// 一定時間(1秒)のうちにバックキーを2回押したら終了
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {}

					mBackPressCount = 0;
				}
			}).start();
		}
	}

	@Override
	protected boolean shouldActionBarShown() {
		return true;
	}

	@Override
	public void onClick(View view) {
		int sectionIndex = -1;

		switch (view.getId()) {
			case R.id.button_section0:
				sectionIndex = 0;
				break;
			case R.id.button_section1:
				sectionIndex = 1;
				break;
			case R.id.button_section2:
				sectionIndex = 2;
				break;
			case R.id.button_section3:
				sectionIndex = 3;
				break;
			case R.id.button_query_database:
				(new ContentsDbOpenHelper(this)).debugPrint();
				return;
			default:
				break;
		}

		if (sectionIndex != -1) {
			Intent intent = new Intent(this, ContentsActivity.class);
			intent.putExtra(ContentsActivity.KEY_SECTION_INDEX, sectionIndex);
			startActivity(intent);
		} else {
			throw new UnsupportedOperationException(this.getClass().getName() + " : Section index is invalid");
		}
	}

	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			finish();
		}
	}
}
