package jp.onetake.binzumejigoku.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.parser.ContentsParser;

public class LaunchActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		findViewById(R.id.button_check).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				parseContents();
			}
		});
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
