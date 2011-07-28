package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import net.bronleewe.david.bitcoin.BitcoinHelper;

public class PeersMenuItem implements IMenuItem
{
	private final BitcoinHelper _bitcoin;

	public PeersMenuItem(BitcoinHelper bitcoin)
	{
		_bitcoin = bitcoin;
	}

	public String toString()
	{
		return "Peers: " + _bitcoin.getPeers();
	}

	public Class<?> getActivity()
	{
		return null;
	}

	public void run(Context context)
	{

	}
}
