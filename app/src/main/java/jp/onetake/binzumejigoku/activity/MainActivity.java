package jp.onetake.binzumejigoku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.db.ContentsDbOpenHelper;

public class MainActivity extends BasicActivity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button_section0).setOnClickListener(this);
		findViewById(R.id.button_section1).setOnClickListener(this);
		findViewById(R.id.button_section2).setOnClickListener(this);
		findViewById(R.id.button_section3).setOnClickListener(this);
		findViewById(R.id.button_query_database).setOnClickListener(this);
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
}
