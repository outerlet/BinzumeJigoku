package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.fragment.SettingFragment;

/**
 * 設定画面を開く<br />
 * この画面でできることは以下
 * <ul>
 *     <li>テキストサイズの変更</li>
 *     <li>文字送り速度の変更</li>
 * </ul>
 */
public class SettingActivity extends BasicActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.layout_fragment_container, new SettingFragment())
				.commit();
	}
}
