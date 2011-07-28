package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import net.bronleewe.david.bitcoin.BitcoinHelper;

public class BalanceMenuItem implements IMenuItem
{
	private String _balance;
	private final BitcoinHelper _bitcoin;

	public BalanceMenuItem(BitcoinHelper bitcoin)
	{
		_bitcoin = bitcoin;
	}

	public String toString()
	{
		return "Balance: " + _bitcoin.getBalance();
	}

	public Class<?> getActivity()
	{
		return null;
	}

	public void run(Context context)
	{
	}
}
