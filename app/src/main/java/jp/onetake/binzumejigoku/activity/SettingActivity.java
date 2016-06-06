package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;

import jp.onetake.binzumejigoku.fragment.SettingFragment;

public class SettingActivity extends BasicActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingFragment())
				.commit();
	}

	@Override
	protected boolean shouldActionBarShown() {
		return true;
	}
}
