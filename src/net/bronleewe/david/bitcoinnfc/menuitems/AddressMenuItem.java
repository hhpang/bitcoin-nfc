package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import net.bronleewe.david.bitcoin.BitcoinHelper;

public class AddressMenuItem implements IMenuItem
{
	private final BitcoinHelper _bitcoinHelper;
	private final Handler _handler;

	public AddressMenuItem(BitcoinHelper bitcoinHelper, Handler handler)
	{
		_bitcoinHelper = bitcoinHelper;
		_handler = handler;
	}

	public Class<?> getActivity()
	{
		return null;
	}

	public String toString()
	{
		return "Address: " + _bitcoinHelper.getAddress();
	}

	public void run(Context context)
	{
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(_bitcoinHelper.getAddress());
		
		Message message = _handler.obtainMessage();
		message.obj = "Copied to Clipboard";
		_handler.sendMessage(message);
	}
}
