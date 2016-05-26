package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.fragment.SectionFragment;

public class ContentsActivity extends BasicActivity {
	public static String KEY_SECTION_INDEX	= "ContentsActivity.KEY_SECTION_INDEX";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contents);

		int sectionIndex = getIntent().getIntExtra(KEY_SECTION_INDEX, -1);
		if (sectionIndex == -1) {
			throw new UnsupportedOperationException(this.getClass().getName() + " : Invalid section index.");
		}

		SectionFragment fragment = SectionFragment.newInstance(sectionIndex);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_fragment_container, fragment);
		trans.commit();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				android.util.Log.i("ContentsActivity", "Action : DOWN");
				break;
			case MotionEvent.ACTION_UP:
				android.util.Log.i("ContentsActivity", "Action : UP");
				break;
		}

		return true;
	}
}
