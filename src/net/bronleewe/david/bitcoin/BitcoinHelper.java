package net.bronleewe.david.bitcoin;

import android.content.Context;
import android.os.*;
import android.os.Message;
import com.google.bitcoin.core.*;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.discovery.IrcDiscovery;
import com.google.bitcoin.discovery.PeerDiscovery;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BoundedOverheadBlockStore;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;

public class BitcoinHelper
{
	private final boolean Testnet = false;
	private final Context _context;
	private final Handler _handler;
	private final Runnable _refresh;
	private final NetworkParameters _network;
	private final String _walletFile;
	private final String _blockChainFile;
	private PeerGroup _peers;
	private Wallet _wallet;
	private long _currentBlocks;
	private long _totalBlocks;
	private int _peerCount;

	public BitcoinHelper(Context context, Handler handler, Runnable refresh)
	{
		_handler = handler;
		_refresh = refresh;
		_context = context;
		_network = Testnet ? NetworkParameters.testNet() : NetworkParameters.prodNet();

		String file = Testnet ? "testnet" : "bitcoin";
		_walletFile = file + ".wallet";
		_blockChainFile = file + ".blockchain";
	}

	private void displayMessage(String text)
	{
		Message message = _handler.obtainMessage();
		message.obj = text;
		_handler.sendMessage(message);
	}

	private void refreshUi()
	{
		_handler.post(_refresh);
	}

	public void init()
	{
		loadWallet();
		loadBlockChain();
	}

	private void loadWallet()
	{
		String[] files = _context.fileList();

		boolean exists = false;

		for (String file : files)
		{
			if (file.equals(_walletFile))
			{
				exists = true;
				break;
			}
		}

		if (exists)
		{
			try
			{
				_wallet = Wallet.loadFromFileStream(_context.openFileInput(_walletFile));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				_wallet = null;
				return;
			}
		}
		else
		{
			_wallet = new Wallet(_network);
			_wallet.addKey(new ECKey());
			saveWallet();
		}

		_wallet.addEventListener(new WalletEventListener()
		{
			public void onCoinsReceived(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance)
			{
				saveWallet();
				refreshUi();

				String amount = Utils.bitcoinValueToFriendlyString(tx.getValueSentToMe(_wallet));
				String message = String.format("Received %s Bitcoins", amount);
				displayMessage(message);
			}

			public void onDeadTransaction(Transaction deadTx, Transaction replacementTx)
			{
				saveWallet();
				refreshUi();
			}

			public void onReorganize()
			{
				saveWallet();
				refreshUi();
			}
		});

		refreshUi();
	}

	private void saveWallet()
	{
		try
		{
			_wallet.saveToFileStream(_context.openFileOutput(_walletFile, Context.MODE_PRIVATE));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadBlockChain()
	{
		try
		{
			File blockChainFile = _context.getDir(_blockChainFile, Context.MODE_PRIVATE);
			BlockStore blockStore = new BoundedOverheadBlockStore(_network, blockChainFile);
			BlockChain blockChain = new BlockChain(_network,  _wallet,  blockStore);
			_peers = new PeerGroup(blockStore, _network, blockChain);

			//PeerDiscovery discovery = Testnet ? new IrcDiscovery("#bitcoinTEST") : new IrcDiscovery("#bitcoin");
			//PeerDiscovery discovery = Testnet ? new DnsDiscovery(new String[] { "bronleewe.net" }, _network) : new DnsDiscovery(_network);
			PeerDiscovery discovery = new DnsDiscovery(new String[] { "bronleewe.net" }, _network);

            for (InetSocketAddress address : discovery.getPeers())
            {
	            PeerAddress peerAddress = new PeerAddress(address);
	            _peers.addAddress(peerAddress);
            }

			final PeerEventListener listener = new PeerEventListener()
			{
				public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft)
				{
					_currentBlocks = blocksLeft;
					if (_currentBlocks > _totalBlocks)
						_totalBlocks = _currentBlocks;
					refreshUi();
				}

				public void onChainDownloadStarted(Peer peer, int blocksLeft)
				{
					_totalBlocks = _currentBlocks = blocksLeft;
					refreshUi();
				}

				public void onPeerConnected(Peer peer, int peerCount)
				{
					_peerCount = peerCount;
					refreshUi();
					displayMessage("Peer Connected");
				}

				public void onPeerDisconnected(Peer peer, int peerCount)
				{
					_peerCount = peerCount;
					refreshUi();
					displayMessage("Peer Disconnected");
				}
			};

			_peers.addEventListener(listener);
			_peers.start();
			_peers.startBlockChainDownload(listener);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		refreshUi();
	}

	public void deleteBlockChain()
	{
		_context.getDir(_blockChainFile, Context.MODE_PRIVATE).delete();
		displayMessage("Block Chain Deleted");
	}

	public boolean send(String address, String coins)
	{
		boolean sent = false;

		BigInteger nanocoins = Utils.toNanoCoins(coins);

		try
		{
			Address a = new Address(_network,  address);
			Transaction tx = _wallet.sendCoins(_peers, a, nanocoins);
			if (tx != null)
				sent = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return sent;
	}

	public String getBalance()
	{
		String balance = "<null>";

		if (_wallet != null)
		{
			BigInteger estimated = _wallet.getBalance(Wallet.BalanceType.ESTIMATED);
			BigInteger available = _wallet.getBalance(Wallet.BalanceType.AVAILABLE);
			balance = Utils.bitcoinValueToFriendlyString(estimated) + " (" + Utils.bitcoinValueToFriendlyString(available) + " available)";
		}

		return balance;
	}

	public String getAddress()
	{
		String address = "<null>";

		if (_wallet != null && _wallet.keychain.size() > 0)
		{
			address = _wallet.keychain.get(0).toAddress(_network).toString();
		}

		return address;
	}

	public int getPeers()
	{
		return _peerCount;
	}

	public long getCurrentBlocks()
	{
		return _currentBlocks;
	}

	public long getTotalBlocks()
	{
		return _totalBlocks;
	}

	public void finish()
	{
		_peers.stop();
		saveWallet();
	}
}