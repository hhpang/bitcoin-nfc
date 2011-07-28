package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import net.bronleewe.david.bitcoin.BitcoinHelper;

public class BlockMenuItem implements IMenuItem
{
	private final BitcoinHelper _bitcoin;

	public BlockMenuItem(BitcoinHelper bitcoin)
	{
		_bitcoin = bitcoin;
	}

	public String toString()
	{
		long current = _bitcoin.getCurrentBlocks();
		long total = _bitcoin.getTotalBlocks();
		int percent = 100;

		if (total > current)
			percent -= (int)(100.0 * (double)current/(double)total);

		return String.format("Blocks: %d/%d (%d%%)", total - current, total, percent);
	}

	public Class<?> getActivity()
	{
		return null;
	}

	public void run(Context context)
	{
		_bitcoin.deleteBlockChain();
	}
}