package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import jp.onetake.binzumejigoku.R;

public class SettingFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.preferences);
	}
}
