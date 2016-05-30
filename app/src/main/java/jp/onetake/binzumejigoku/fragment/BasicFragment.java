package jp.onetake.binzumejigoku.fragment;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;

/**
 * 全Fragmentが継承すべき基底Fragment
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
