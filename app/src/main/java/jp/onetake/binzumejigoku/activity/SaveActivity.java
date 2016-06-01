package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.transition.Fade;
import android.view.Window;

import jp.onetake.binzumejigoku.R;

public class SaveActivity extends BasicActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
	}
}
