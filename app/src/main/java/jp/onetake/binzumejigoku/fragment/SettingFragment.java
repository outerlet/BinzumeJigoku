package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import jp.onetake.binzumejigoku.R;

/**
 * 設定画面の表示を担当するフラグメント
 */
public class SettingFragment extends PreferenceFragmentCompat {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.preferences);

		if (getActivity() instanceof Preference.OnPreferenceClickListener) {
			Preference.OnPreferenceClickListener listener = (Preference.OnPreferenceClickListener)getActivity();

			// findPreference(getString(R.string.prefkey_donation_nosave)).setOnPreferenceClickListener(listener);
			findPreference(getString(R.string.prefkey_about_work_nosave)).setOnPreferenceClickListener(listener);
		}
	}
}
