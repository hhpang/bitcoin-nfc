package net.bronleewe.david.nfc;

import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Parcelable;

public class NfcMessageService implements IMessageService
{
	private final Activity _activity;
	private final NfcAdapter _adapter;

	public NfcMessageService(Activity activity)
	{
		_activity = activity;
		NfcManager manager = (NfcManager) _activity.getSystemService(Context.NFC_SERVICE);
		_adapter = manager.getDefaultAdapter();
	}

	public void sendMessage(String message)
	{
		NfcManager manager = (NfcManager) _activity.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "application/vnd.bitcoin".getBytes(), new byte[0], message.getBytes());
		NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});
		adapter.enableForegroundNdefPush(_activity, ndefMessage);
	}

	public void stopSending()
	{
		NfcManager manager = (NfcManager) _activity.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		adapter.disableForegroundNdefPush(_activity);
	}

	public String receiveMessage()
	{
		String message = "";
		String action = _activity.getIntent().getAction();
		if (action != null && action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
		{
			Parcelable[] parcelables = _activity.getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
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