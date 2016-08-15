package jp.onetake.binzumejigoku.fragment;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;

/**
 * このアプリで用いられるFragmentが基本的に継承する基底Fragment<br />
 * PreferenceFragmentなどがあるので「全てのFragment」が継承する訳ではない
 */
public class BasicFragment extends Fragment {
	/**
	 * Activityで発生したイベントをFragmentで処理したい場合はこれを実装<br />
	 * Activityのイベントハンドル用メソッドでこれを呼び出すようにする
	 */
	public interface ActivityEventListener {
		void onActivityTouched(MotionEvent event);
	}
}
