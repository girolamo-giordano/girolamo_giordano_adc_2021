package it.semanticharmony;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

public class SemanticHarmonySocialNetworkImpl implements SemanticHarmonySocialNetwork {
	
	@SuppressWarnings("serial")
	final private Map<String, Integer> questionsandanswers = new HashMap<String,Integer>(){{
			put("Quanti sono i tipi primitivi in Java? \n 1 - 8 \n 2 - 6 \n 3 - 10 \n",1);
			put("I file compilati Java hanno estensione:\n 1 - .java \n 2 - .class \n 3 - .jdk\n",2);
			put("Con quale parola chiave di Java si ottiene l'ereditarietà?\n 1 - extends \n 2 - inherits \n 3 - expand\n",1);
			put("Nel linguaggio Java, per inserire commenti su più righe consecutive si usano:\n 1 - # \n 2 - //\n 3 - /* */\n",3);
			put("Nel linguaggio Java, se si tenta di dividere un numero di tipo \"integer\" per zero:\n 1 - viene lanciata un'eccezione \n 2 - viene restituito 0 \n 3 - il numero non viene diviso\n",1);
	}};
	

	final private List<String> questions= new ArrayList<>(questionsandanswers.keySet());
	final private Peer peer;
	private static List<PeerDHT> dhtlist=new ArrayList<PeerDHT>();
	final private PeerDHT _dht;
	final private int DEFAULT_MASTER_PORT=4000;
	final private String all_accounts="all_accounts";
	private PeerUser peeruser;

	/*
	 * Al costruttore vengono passati l'id che avrà il peer, il riferimento al master peer ed un _listener
	 * che provvederà a processare i messaggi. 
	 */
	public SemanticHarmonySocialNetworkImpl(int _id, String _master_peer, final MessageListener _listener) throws Exception {
		 peer= new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT+_id).start();
			_dht = new PeerBuilderDHT(peer).start();	
			dhtlist.add(_dht);
			FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
			fb.awaitUninterruptibly();
			if(fb.isSuccess()) {
				peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
			}else {
				throw new Exception("Error in master peer bootstrap.");
			}
			
			
			peer.objectDataReply(new ObjectDataReply() {
		
				/*
				 * Al metodo reply è possibile inviare una lista di oggetti, in questo modo abbiamo la possibilità
				 * di inserire oggetti che possano aggiornare lo stato del peer che riceve il messaggio
				 */
				public Object reply(PeerAddress sender, Object request) throws Exception {
					
					if(request instanceof ArrayList<?>)
					{
						@SuppressWarnings("unchecked")
						ArrayList<Object> list=(ArrayList<Object>) request;
						if(!list.isEmpty() && list.get(0) instanceof PeerUser)
						{
							peeruser=(PeerUser) list.get(0);
							list.remove(0);
							if(!list.isEmpty() && list.get(0) instanceof String)
							return _listener.parseMessage(list.get(0));
						}
						else if(!list.isEmpty() && list.get(0) instanceof String)
							return _listener.parseMessage(list.get(0));
					}
					
					return _listener.parseMessage(request);
				}
			});
		
	}

	/*
	 * Questo metodo permette di restituire le domande a cui l'utente dovrà rispondere
	 */
	@Override
	public List<String> getUserProfileQuestions() {
		return questions;
	}
	

	/*
	 * Questo metodo permette di ricevere la chiave di accesso al gruppo, dando in input una lista di interi
	 * che rappresentano lo score a ciascuna domanda risposta dall'utente. 
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createAuserProfileKey(List<Integer> _answer) {
		
		if(peeruser == null || _answer == null || peeruser.getKey() != null) return null;
		if(questionsandanswers.values().size() != _answer.size()) return null;
		
		int score=0;
		for(int i=0;i<questions.size();i++)
		{
			if( questionsandanswers.get(questions.get(i)) == _answer.get(i))
				score+=2;
		}
		peeruser.setScore(score);
		String key= Integer.toString(score);
		peeruser.setKey(key);
		FutureGet futureGet= _dht.get(Number160.createHash(all_accounts)).start();
		futureGet.awaitUninterruptibly();
		Map<String,PeerUser> nickstoadd;
		try {
			nickstoadd = (Map<String,PeerUser>) futureGet.dataMap().values().iterator().next().object();
			nickstoadd.replace(peeruser.getNickname(), peeruser);
			_dht.put(Number160.createHash(all_accounts)).data(new Data(nickstoadd)).start().awaitUninterruptibly();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return key;
	}

	
	/*
	 * Questo metodo permette di unirsi ad un nuovo gruppo, nel caso il gruppo non esistesse viene creato
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean join(String _profile_key, String _nick_name) {
		try {
			
			if(_profile_key==null || _nick_name == null || peeruser == null || _profile_key.isEmpty() || _nick_name.isEmpty()) return false;		
			if(peeruser.isBusy()) return false;
			if(_nick_name != peeruser.getNickname()) return false;
			FutureGet futureGet = _dht.get(Number160.createHash(_profile_key)).start();
			
			futureGet.awaitUninterruptibly();
			FutureGet futureGetAcc= _dht.get(Number160.createHash(all_accounts)).start();
			futureGetAcc.awaitUninterruptibly();
			Map<String,PeerUser> nickstoadd;
			nickstoadd = (Map<String,PeerUser>) futureGetAcc.dataMap().values().iterator().next().object();
			
			
			if (futureGet.isSuccess() && futureGet.isEmpty()) 
			{
				Map<String,PeerUser> nicks_peer=new HashMap<String,PeerUser>();
				peeruser.setBusy(true);
				peeruser.setKey(_profile_key);
				nicks_peer.put(_nick_name, peeruser);
				nickstoadd.replace(_nick_name, peeruser);
				_dht.put(Number160.createHash(_profile_key)).data(new Data(nicks_peer)).start().awaitUninterruptibly();
				if(futureGetAcc.isSuccess())
					_dht.put(Number160.createHash(all_accounts)).data(new Data(nickstoadd)).start().awaitUninterruptibly();
				else 
					return false;
				return true;
			}
			else if(futureGet.isSuccess() && !futureGet.isEmpty())
			{
				Map<String,PeerUser> nicks;
				nicks = (Map<String,PeerUser>) futureGet.dataMap().values().iterator().next().object();
				ArrayList<Object> tosend=new ArrayList<>();
				tosend.add(_nick_name+" si è unito al gruppo\n");
				for(PeerUser peer: nicks.values())
				{
					FutureDirect futureDirect = _dht.peer().sendDirect(peer.getPeeradd()).object(tosend).start();
					futureDirect.awaitUninterruptibly();
				}
				peeruser.setBusy(true);
				peeruser.setKey(_profile_key);
				nicks.put(_nick_name, peeruser);
				nickstoadd.replace(_nick_name, peeruser);
				_dht.put(Number160.createHash(_profile_key)).data(new Data(nicks)).start().awaitUninterruptibly();
				if(futureGetAcc.isSuccess())
					_dht.put(Number160.createHash(all_accounts)).data(new Data(nickstoadd)).start().awaitUninterruptibly();
				else 
					return false;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Questo metodo restituisce la lista di amici presenti all'interno del gruppo
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getFriends() {
		try {
			if (peeruser == null || peeruser.getKey()== null) return null;
			FutureGet futureGet= _dht.get(Number160.createHash(peeruser.getKey())).start();
			futureGet.awaitUninterruptibly();
			List<String> myfriends;
			if (!futureGet.isEmpty()) 
			{
				myfriends= new ArrayList<>(((Map<String,PeerUser>)futureGet.dataMap().values().iterator().next().object()).keySet());
				myfriends.remove(peeruser.getNickname());
				return myfriends;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Questo metodo permette di aggiungere un utente al gruppo corrente. L'utente che si vuole aggiungere
	 * non deve stare in nessun altro gruppo
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addUserToGroup(String nickname) {
		try {
			if(peeruser == null || nickname == null || nickname.isEmpty())return false;
			if (peeruser.getKey()== null) return false;
			FutureGet futureGet= _dht.get(Number160.createHash(all_accounts)).start();
			futureGet.awaitUninterruptibly();
			Map<String,PeerUser> nicks=new HashMap<String, PeerUser>();
			if(futureGet.isSuccess() && !futureGet.isEmpty())
			{
				nicks = (Map<String,PeerUser>) futureGet.dataMap().values().iterator().next().object();
				PeerUser peertoadd=nicks.get(nickname);
				if(peertoadd== null  || peertoadd.isBusy())
					return false;
				
				FutureGet futureGetAcc= _dht.get(Number160.createHash(peeruser.getKey())).start();
				futureGetAcc.awaitUninterruptibly();
				Map<String,PeerUser> nickstoadd=new HashMap<String, PeerUser>();
				if(futureGetAcc.isSuccess())
				{
					nickstoadd = (Map<String,PeerUser>) futureGetAcc.dataMap().values().iterator().next().object();
					peertoadd.setKey(peeruser.getKey());
					peertoadd.setBusy(true);
					nickstoadd.put(nickname, peertoadd);
					nicks.replace(nickname, peertoadd);
					_dht.put(Number160.createHash(peeruser.getKey())).data(new Data(nickstoadd)).start().awaitUninterruptibly();
					_dht.put(Number160.createHash(all_accounts)).data(new Data(nicks)).start().awaitUninterruptibly();
					ArrayList<Object> tosend=new ArrayList<>();
					tosend.add(peertoadd);
					tosend.add(peeruser.getNickname() + " ti ha aggiunto al gruppo "+peeruser.getKey());
					FutureDirect futureDirect = _dht.peer().sendDirect(peertoadd.getPeeradd()).object(tosend).start();
					futureDirect.awaitUninterruptibly();
					return true;
				}
				return false;
				
			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Questo metodo permette di registrarsi con un nickname all'interno della rete
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean registerNickname(String nickname) {
		try {
			
			if(nickname == null || nickname.isEmpty() || peeruser != null)return false;
			FutureGet futureGetAcc = _dht.get(Number160.createHash(all_accounts)).start();
			futureGetAcc.awaitUninterruptibly();
			if(futureGetAcc.isSuccess() && futureGetAcc.isEmpty())
			{
				Map<String,PeerUser> nicks_peer=new HashMap<String,PeerUser>();
				peeruser=new PeerUser(_dht.peer().peerAddress(),nickname);
				nicks_peer.put(nickname, peeruser);
				 _dht.put(Number160.createHash(all_accounts)).data(new Data(nicks_peer)).start().awaitUninterruptibly();
				return true;
			}
			else if(futureGetAcc.isSuccess() && !futureGetAcc.isEmpty())
			{
				Map<String,PeerUser> nicks=new HashMap<>();
				nicks = (Map<String,PeerUser>) futureGetAcc.dataMap().values().iterator().next().object();
				if(nicks.keySet().contains(nickname)) return false;
				peeruser=new PeerUser(_dht.peer().peerAddress(),nickname);
				nicks.put(nickname, peeruser);
				_dht.put(Number160.createHash(all_accounts)).data(new Data(nicks)).start().awaitUninterruptibly();
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/*
	 * Questo metodo permette di inviare un messaggio all'interno del gruppo corrente
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean sendMessageToGroup(String message) {
		try {
			if(peeruser == null || message == null || message.isEmpty()) return false;
			if (peeruser.getKey()== null || !peeruser.isBusy()) return false;
			FutureGet futureGet= _dht.get(Number160.createHash(peeruser.getKey())).start();
			futureGet.awaitUninterruptibly();
			Map<String,PeerUser> myfriends;
			if (futureGet.isSuccess() && !futureGet.isEmpty()) 
			{
				myfriends= (Map<String,PeerUser>)futureGet.dataMap().values().iterator().next().object();
				for(PeerUser peer: myfriends.values())
				{
					ArrayList<Object> tosend=new ArrayList<>();
					tosend.add(peeruser.getNickname()+":"+message+"\n");
					FutureDirect futureDirect = _dht.peer().sendDirect(peer.getPeeradd()).object(tosend).start();
					futureDirect.awaitUninterruptibly();
					
				}
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Questo metodo permette di lasciare il gruppo corrente
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean leaveGroup() {
		try {
			if(peeruser == null) return false;
			if (peeruser.getKey()== null) return false;
			FutureGet futureGet= _dht.get(Number160.createHash(peeruser.getKey())).start();
			futureGet.awaitUninterruptibly();
			Map<String,PeerUser> myfriends;
			if (!futureGet.isEmpty()) 
			{
				myfriends= (Map<String,PeerUser>)futureGet.dataMap().values().iterator().next().object();
				myfriends.remove(peeruser.getNickname());
				if(myfriends.isEmpty())
					_dht.remove(Number160.createHash(peeruser.getKey())).start().awaitUninterruptibly();
				else
				_dht.put(Number160.createHash(peeruser.getKey())).data(new Data(myfriends)).start().awaitUninterruptibly();
				
				
				peeruser.setBusy(false);
				peeruser.setKey(null);
				FutureGet futureGetAcc= _dht.get(Number160.createHash(all_accounts)).start();
				futureGetAcc.awaitUninterruptibly();
				if(futureGetAcc.isSuccess() && !futureGetAcc.isEmpty())
				{
					Map<String,PeerUser> myfriends_ = (Map<String, PeerUser>) futureGet.dataMap().values().iterator().next().object();
					myfriends_.replace(peeruser.getNickname(), peeruser);
					ArrayList<Object> tosend=new ArrayList<>();
					tosend.add(peeruser.getNickname()+" ha lasciato il gruppo\n");
					for(PeerUser peer: myfriends_.values())
					{
						FutureDirect futureDirect = _dht.peer().sendDirect(peer.getPeeradd()).object(tosend).start();
						futureDirect.awaitUninterruptibly();
						
					}
					_dht.put(Number160.createHash(all_accounts)).data(new Data(myfriends_)).start().awaitUninterruptibly();
					return true;
				}
				
				return false;
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Questo metodo permette di creare un gruppo data una key
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean createGroup(String key) {
		try {
			if(peeruser == null || key== null || key.isEmpty()) return false;
			if(peeruser.isBusy()) return false;
			FutureGet futureGet = _dht.get(Number160.createHash(key)).start();
			
			futureGet.awaitUninterruptibly();
			FutureGet futureGetAcc= _dht.get(Number160.createHash(all_accounts)).start();
			futureGetAcc.awaitUninterruptibly();
			Map<String,PeerUser> nickstoadd= new HashMap<String, PeerUser>();
			if(futureGetAcc.isSuccess() && !futureGetAcc.isEmpty())
				nickstoadd = (Map<String,PeerUser>) futureGetAcc.dataMap().values().iterator().next().object();
			else return false;
			
			if (futureGet.isSuccess() && futureGet.isEmpty()) 
			{
				Map<String,PeerUser> nicks_peer=new HashMap<String,PeerUser>();
				peeruser.setBusy(true);
				peeruser.setKey(key);
				nicks_peer.put(peeruser.getNickname(), peeruser);
				nickstoadd.replace(peeruser.getNickname(),peeruser);
				_dht.put(Number160.createHash(key)).data(new Data(nicks_peer)).start().awaitUninterruptibly();
				_dht.put(Number160.createHash(all_accounts)).data(new Data(nickstoadd)).start().awaitUninterruptibly();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Questo metodo permette di uscire dal network
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean leaveNetwork() {
		try {
			if(peeruser == null)
			{
				_dht.peer().announceShutdown().start().awaitUninterruptibly();
				return true;
			}
				
			if(peeruser.isBusy())
			{
				if(!leaveGroup())
					return false;
			}
			
			FutureGet futureGetAcc= _dht.get(Number160.createHash(all_accounts)).start();
			futureGetAcc.awaitUninterruptibly();
			if(futureGetAcc.isSuccess() && !futureGetAcc.isEmpty() && !futureGetAcc.dataMap().values().isEmpty())
			{
				Map<String, PeerUser> myfriends = (Map<String, PeerUser>) futureGetAcc.dataMap().values().iterator().next().object();
				myfriends.remove(peeruser.getNickname());
				if(myfriends.isEmpty())
					_dht.remove(Number160.createHash(all_accounts)).start().awaitUninterruptibly();
				else
					_dht.put(Number160.createHash(all_accounts)).data(new Data(myfriends)).start().awaitUninterruptibly();
			
			}
			_dht.peer().announceShutdown().start().awaitUninterruptibly();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/*
	 * Questo metodo permette di effettuare lo shutdown su tutte le dht dei peer.
	 */
	static void resetDht() throws InterruptedException
	{
		for(PeerDHT peerdht:dhtlist)
		{
			peerdht.shutdown().awaitUninterruptibly();
		}
			
	}
	
	
	

}
