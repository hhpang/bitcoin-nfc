package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import net.bronleewe.david.bitcoin.BitcoinHelper;
import net.bronleewe.david.bitcoinnfc.activities.SendActivity;

public class SendMenuItem implements IMenuItem
{
	private final BitcoinHelper _bitcoin;

	public SendMenuItem(BitcoinHelper bitcoin)
	{
		_bitcoin = bitcoin;
	}

	public String toString()
	{
		return "Send...";
	}

	public Class<?> getActivity()
	{
		return SendActivity.class;
	}

	public void run(Context context)
	{
	}
}