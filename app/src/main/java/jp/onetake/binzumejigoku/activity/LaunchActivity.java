package jp.onetake.binzumejigoku.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.data.ContentsDbOpenHelper;
import jp.onetake.binzumejigoku.contents.parser.ContentsParser;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		findViewById(R.id.button_parse_contents).setOnClickListener(this);
		findViewById(R.id.button_query_database).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_parse_contents:
				parseContents();
				break;
			case R.id.button_query_database:
				query((new ContentsDbOpenHelper(this)).getReadableDatabase());
				break;
		}
	}

	private void query(SQLiteDatabase db) {
		Cursor c = db.rawQuery("select id, section, sequence, type, value1, contents_text from tbl_contents", null);
		c.moveToFirst();
		for (int i = 0 ; i < c.getCount() ; i++) {
			android.util.Log.i("CONTENTS-DB", "id = " + c.getString(0) + ", section = " + c.getString(1) + ", sequence = " + c.getString(2)
					+ ", type = " + c.getString(3) + ", value1 = " + c.getString(4) + ", contents_text = " + c.getString(5));
			c.moveToNext();
		}
		c.close();
	}

	private void parseContents() {
		ContentsParser parser = new ContentsParser(this);

		try {
			parser.parse("contents_binzume_jigoku.xml");
		} catch (IOException | XmlPullParserException ex) {
			ex.printStackTrace();
		}
	}
}
