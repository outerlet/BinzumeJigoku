package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.parser.ContentsDbParser;
import jp.onetake.binzumejigoku.fragment.dialog.SectionFragment;

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

		ContentsDbParser parser = new ContentsDbParser(this);
		List<SectionElement> list = parser.parse(sectionIndex);
		for (SectionElement e : list) {
			android.util.Log.i("QUERY", e.toString());
		}

		SectionFragment fragment = SectionFragment.newInstance(sectionIndex);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_fragment_container, fragment);
		trans.commit();
	}
}
