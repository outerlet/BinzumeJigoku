package jp.onetake.binzumejigoku.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import jp.onetake.binzumejigoku.contents.common.ContentsInterface;

/**
 * 全てのアクティビティが継承すべき基底クラス
 * 全画面に適用すべき共通設定などを定義する
 */
public class BasicActivity extends AppCompatActivity {
	/**
	 * Fragmentを配置するためのメソッドに何を使うか指定するための列挙値
	 */
	protected enum FragmentMethod {
		Dialog,		// ダイアログ(DialogFragment#show)
		Add,		// FragmentTransaction#add
		Replace,	// FragmentTransaction#replace
	}

	/**
	 * Fragmentを置換する際、Fragment自体や置換するためのViewID、タグ文字列を保持するクラス
	 */
	private class PendingFragment {
		int containerViewId;
		FragmentMethod method;
		Fragment fragment;
		String tag;

		PendingFragment(int containerViewId, FragmentMethod method, Fragment fragment, String tag) {
			this.containerViewId = containerViewId;
			this.method = method;
			this.fragment = fragment;
			this.tag = tag;
		}
	}

	private PendingFragment mPending = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 縦画面固定。AndroidManifestでも設定できるが個々に設定するとXMLが冗長になるのでここで
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Activityが横断的に利用するオブジェクトが初期化されてなければ初期化する
		ContentsInterface cif = ContentsInterface.getInstance();
		if (!cif.isInitialized()) {
			cif.initialize(getApplicationContext());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostResume() {
		super.onPostResume();

		// Fragmentを表示させようとしてIllegalStateExceptionになったときのフォロー
		if (mPending != null) {
			if (mPending.method == FragmentMethod.Dialog) {
				showDialogFragment((DialogFragment)mPending.fragment, mPending.tag);
			} else {
				postFragment(mPending.containerViewId, mPending.method, mPending.fragment, mPending.tag);
			}

			mPending = null;
		}
	}

	/**
	 * アプリケーションを終了させる
	 */
	protected void finishApplication() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.INTENT_KEY_FINISH_APP, true);
		startActivity(intent);
	}

	/**
	 * Fragmentを置き換える<br />
	 * FragmentTransaction#replaceでは、メソッドを実行するタイミングによってIllegalStateExceptionが発生してしまう<br />
	 * Illegal...が発生したときはそのFragmentをペンディング状態で保持しておきonPostResumeで改めて表示させる
	 * @param containerViewId	Fragmentを配置するViewのID
	 * @param method			Fragmentを配置するのに使うメソッド
	 * @param fragment			配置するFragment
	 * @param tag				Fragmentを配置するときのタグ
	 */
	protected void postFragment(int containerViewId, FragmentMethod method, Fragment fragment, String tag) {
		try {
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

			switch (method) {
				case Add:
					trans.add(containerViewId, fragment, tag);
					break;
				case Replace:
					trans.replace(containerViewId, fragment, tag);
					break;
			}

			trans.commit();
		} catch (IllegalStateException ise) {
			mPending = new PendingFragment(containerViewId, method, fragment, tag);
		}
	}

	/**
	 * DialogFragmentを使ってダイアログを表示する<br />
	 * postFragmentと同様、DialogFragment#showでは、メソッドを実行するタイミングによってIllegalStateExceptionが発生してしまう<br />
	 * Illegal...が発生したときはそのFragmentをペンディング状態で保持しておきonPostResumeで改めて表示させる
	 * @param dialog	表示するダイアログに対応したダイアログフラグメント
	 * @param tag		ダイアログに関連づけるタグ文字列
	 */
	protected void showDialogFragment(DialogFragment dialog, String tag) {
		try {
			dialog.show(getSupportFragmentManager(), tag);
		} catch (IllegalStateException ise) {
			mPending = new PendingFragment(-1, FragmentMethod.Dialog, dialog, tag);
		}
	}
}
