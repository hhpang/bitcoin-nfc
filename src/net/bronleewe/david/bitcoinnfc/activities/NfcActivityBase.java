package net.bronleewe.david.bitcoinnfc.activities;

import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;

public abstract class NfcActivityBase extends Activity
{
	private NfcAdapter _adapter;
	private String _message;
	private boolean _foreground;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
		_adapter = manager.getDefaultAdapter();
	}

	public void onResume()
	{
		super.onResume();
		_foreground = true;
		EnableNfc();
	}

	protected void setNfcMessage(String message)
	{
		_message = message;
		EnableNfc();
	}

	private void EnableNfc()
	{
		if (_foreground && _message != null)
		{
			NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "application/vnd.bitcoin".getBytes(), new byte[0], _message.getBytes());
			NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});
			_adapter.enableForegroundNdefPush(this, ndefMessage);
		}
	}
	
	public void onPause()
	{
		super.onPause();
		_foreground = false;
		_adapter.disableForegroundNdefPush(this);
	}

	public static String receiveMessage(Activity activity)
	{
		String message = "";
		String action = activity.getIntent().getAction();

		if (action != null && action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
		{
			Parcelable[] parcelables = activity.getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] messages = null;

			if (parcelables != null)
			{
				messages = new NdefMessage[parcelables.length];
				for (int i = 0; i < parcelables.length; ++i)
					messages[i] = (NdefMessage) parcelables[i];
			}

			if (messages != null && messages.length > 0)
			{
				NdefRecord[] records = messages[0].getRecords();
				if (records != null && records.length > 0)
					message = new String(records[0].getPayload());
			}
		}

		return message;
	}
}