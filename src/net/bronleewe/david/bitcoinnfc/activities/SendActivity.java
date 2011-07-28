package net.bronleewe.david.bitcoinnfc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import net.bronleewe.david.R;

public class SendActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);

		final String nfc = NfcActivityBase.receiveMessage(this);
		final TextView addressView = (TextView)findViewById(R.id.address);
		String address = nfc.substring(nfc.indexOf(':') + 1);
		addressView.setText(address);

		final TextView amountView = (TextView)findViewById(R.id.amount);

		final Button send = (Button)findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				boolean sent = false;
				String message;
				String address = addressView.getText().toString();
				String amount = amountView.getText().toString();

				if (MainActivity.getBitcoinHelper().send(address, amount))
				{
					message = "Bitcoins Sent";
					sent = true;
				}
				else
				{
					message = "Failed to Send";
				}

				Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
				
				if (sent)
				{
					finish();
				}
			}
		});
	}
}