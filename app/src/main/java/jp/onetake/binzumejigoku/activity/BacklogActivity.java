package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.view.BacklogListAdapter;

/**
 * テキスト履歴を表示するアクティビティ
 */
public class BacklogActivity extends AppCompatActivity {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backlog);

		BacklogListAdapter adapter = new BacklogListAdapter(
				this, ContentsInterface.getInstance().getSaveData(0).getBacklogList());

		ListView listView = (ListView)findViewById(R.id.listview_backlogs);
		listView.setAdapter(adapter);
		listView.setSelection(adapter.getCount() - 1);
	}
}
