package jp.onetake.binzumejigoku.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * 全てのアクティビティが継承すべき基底クラス
 * 全画面に適用すべき共通設定などを定義する
 */
public class BasicActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 縦画面固定。AndroidManifestでも設定できるが個々に設定するとXMLが冗長になるのでここで
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// フルスクリーン表示
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// ActionBarを非表示
		getSupportActionBar().hide();
	}
}
