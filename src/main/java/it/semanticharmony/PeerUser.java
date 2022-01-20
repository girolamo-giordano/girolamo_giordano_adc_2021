package it.semanticharmony;

import java.io.Serializable;

import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class PeerUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2756781447416793816L;
	
	private PeerAddress peeradd;
	private boolean busy;
	private Integer score;
	private String nickname;
	private String key;
	

	
	public PeerUser(PeerAddress peeradd,String nickname)
	{
		this.peeradd = peeradd;
		this.busy=false;
		this.score=-1;
		this.nickname=nickname;
	}
	
	public PeerAddress getPeeradd() {
		return peeradd;
	}
	public void setPeeradd(PeerAddress peeradd) {
		this.peeradd = peeradd;
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "PeerUser [peeradd=" + peeradd + ", busy=" + busy + ", score=" + score + ", nickname=" + nickname
				+ ", key=" + key + "]";
	}


}
