package net.bronleewe.david.bitcoinnfc.menuitems;

import android.content.Context;

public interface IMenuItem
{
	Class<?> getActivity();
	void run(Context context);
}
