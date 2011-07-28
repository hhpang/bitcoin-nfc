package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;
import net.bronleewe.david.bitcoinnfc.activities.AboutActivity;

public class AboutMenuItem implements IMenuItem
{
	public String toString()
	{
		return "About...";
	}

	public Class<?> getActivity()
	{
		return AboutActivity.class;
	}

	public void run(Context context)
	{

	}
}
