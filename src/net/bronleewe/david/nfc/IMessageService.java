package net.bronleewe.david.nfc;

public interface IMessageService
{
	void sendMessage(String message);
	void stopSending();
	String receiveMessage();
}
