package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import jp.onetake.binzumejigoku.R;

/**
 * 設定画面の表示を担当するフラグメント
 */
public class SettingFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.preferences);
	}
}
