package net.bronleewe.david.bitcoinnfc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import android.os.Message;
import net.bronleewe.david.R;
import net.bronleewe.david.bitcoin.BitcoinHelper;
import net.bronleewe.david.bitcoinnfc.menuitems.*;

import java.util.ArrayList;

public class MainActivity extends NfcActivityBase
{
	private static BitcoinHelper _bitcoin;
	private ArrayAdapter<?> _adapter;

	public static BitcoinHelper getBitcoinHelper()
	{
		return _bitcoin;
	}

	private final Handler _handler = new Handler()
	{
		public void handleMessage(Message message)
		{
			String str = message.obj.toString();

			if (str.startsWith("address:"))
			{
				setNfcMessage(str);
			}
			else
			{
				Toast.makeText(getApplicationContext(), message.obj.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	private final Runnable _refresh = new Runnable()
	{
		public void run()
		{
			if (_adapter != null)
			{
				_adapter.notifyDataSetChanged();
			}
		}
	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		_bitcoin = new BitcoinHelper(getApplicationContext(), _handler, _refresh);

		final ArrayList<IMenuItem> items = new ArrayList<IMenuItem>();
		items.add(new AddressMenuItem(_bitcoin, _handler));
		items.add(new BalanceMenuItem(_bitcoin));
		items.add(new BlockMenuItem(_bitcoin));
		items.add(new PeersMenuItem(_bitcoin));
		items.add(new SendMenuItem(_bitcoin));
		items.add(new AboutMenuItem());

		final ListView view = (ListView)findViewById(R.id.list);

		_adapter = new ArrayAdapter<IMenuItem>(this, android.R.layout.simple_list_item_1, items);
		view.setAdapter(_adapter);

		view.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
			{
				final IMenuItem item = items.get(position);
				Class<?> activity = item.getActivity();

				if (activity != null)
				{
					startActivity(new Intent(view.getContext(), activity));
				}
				else
				{
					new Thread(new Runnable()
					{
						public void run()
						{
							item.run(getApplicationContext());
						}
					}).start();
				}
			}
		});

		new Thread(new Runnable()
		{
			public void run()
			{
				_bitcoin.init();
			}
		}).start();
	}

	public void onDestroy()
	{
		super.onDestroy();
		_bitcoin.finish();
	}
}