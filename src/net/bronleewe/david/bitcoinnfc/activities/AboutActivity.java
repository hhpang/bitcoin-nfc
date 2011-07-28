package net.bronleewe.david.bitcoinnfc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import net.bronleewe.david.R;

public class AboutActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ListView view = (ListView)findViewById(R.id.list);
		String[] list = new String[]
		{
			"Bitcoin NFC 1.0.0",
			"© 2011 David Bronleewe",
			"https://david.bronleewe.net/",
			"david@bronleewe.net",
		};
		final ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		view.setAdapter(adapter);
	}
}