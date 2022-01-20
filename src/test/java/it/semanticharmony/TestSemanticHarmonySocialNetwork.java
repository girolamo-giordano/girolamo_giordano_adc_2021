package it.semanticharmony;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestSemanticHarmonySocialNetwork  {
	

	private SemanticHarmonySocialNetworkImpl peer0, peer1, peer2, peer3;
	
	public TestSemanticHarmonySocialNetwork () throws Exception{
		
		
	}
	
	@BeforeEach
	 void setBefore() throws Exception
	{
		class MessageListenerImpl implements MessageListener{
			int peerid;
			public MessageListenerImpl(int peerid)
			{
				this.peerid=peerid;
			}
			public Object parseMessage(Object obj) {
				return "success";
			}
			
		}
		peer0 = new SemanticHarmonySocialNetworkImpl(0, "127.0.0.1", new MessageListenerImpl(0));	
		 peer1 = new SemanticHarmonySocialNetworkImpl(1, "127.0.0.1", new MessageListenerImpl(1));
		 peer2 = new SemanticHarmonySocialNetworkImpl(2, "127.0.0.1", new MessageListenerImpl(2));
		 peer3 = new SemanticHarmonySocialNetworkImpl(3, "127.0.0.1", new MessageListenerImpl(3));
		
	}
	
	@AfterEach
	void setAfter() throws InterruptedException
	{
		peer0.leaveNetwork();
		peer1.leaveNetwork();
		peer2.leaveNetwork();
		peer3.leaveNetwork();
		SemanticHarmonySocialNetworkImpl.resetDht();
	}

	@Test
	void testCaseGetUserProfileQuestions()
	{
		//Il peer0 chiama la getUserProfileQuestions dove gli verrà restituita una lista con le domande
		peer0.registerNickname("Alice");
		assertTrue(!peer0.getUserProfileQuestions().isEmpty());
	
	}
	
	
	@Test
	void testCaseRegisterNickname() throws InterruptedException{
		//Il peer0 prova a registrarsi dando in input null, viene restituito false
		assertFalse(peer0.registerNickname(null));
		//Il peer0 prova a registrarsi dando in input una stringa vuota, viene restituito false
		assertFalse(peer0.registerNickname(""));
		//Il peer0 si registra col nickname "Alice".
		assertTrue(peer0.registerNickname("Alice"));
		//Il peer0 prova a registrarsi col nickname "Gianni" ma ha esito fallito poichè già registrato col nome "Alice" 
		assertFalse(peer0.registerNickname("Gianni")); 
		//Il peer1 prova a registrarsi col nickname "Alice" ma ha esito fallito poichè il nickname "Alice" è già registrato
		assertFalse(peer1.registerNickname("Alice"));
		//Il peer2 riesce a registrarsi col nickname "Nunzio"
		assertTrue(peer2.registerNickname("Nunzio"));
		//Il peer0 prova di nuovo a registrarsi con null, ma viene restituito false.
		assertFalse(peer0.registerNickname(null));
		//Il peer0 prova a registrarsi dando in input una stringa vuota, viene restituito false
		assertFalse(peer0.registerNickname(""));
		//Il peer3 prova a registrarsi dando in input una stringa vuota, viene restituito false
		assertFalse(peer3.registerNickname(""));
	}
	

	@Test
	void testCaseCreateAUserProfileKey()
	{
		List<Integer> integers=Arrays.asList(2,2,2,2,2);
		
		//Ci attendiamo che venga restituito null poichè peer0 passa al metodo createAuserProfileKey il valore null
		assertNull(peer0.createAuserProfileKey(null));
		
		/*Ci attendiamo che venga restituito null poichè peer0 passa al metodo createAuserProfileKey il valore null
		 * poichè il peer0 non si è registrato.
		 */
		assertNull(peer0.createAuserProfileKey(integers));
		
		peer0.registerNickname("Alice");
		
		//Ci attendiamo che la chiave restituita sia pari alla stringa 2
		assertEquals("2",peer0.createAuserProfileKey(integers)); 
		
		//Ci attendiamo che venga restituito null, poichè gli è stata già assegnata la chiave
		assertNull(peer0.createAuserProfileKey(integers)); 
		
		peer1.registerNickname("Fabio");
		integers=Arrays.asList(2,2,2,2,2,1);
		
		//Ci attendiamo che venga restituito null, poichè la lista di interi è più grande rispetto a quelle delle domande
		assertNull(peer1.createAuserProfileKey(integers));
		
		peer2.registerNickname("Carlo");
		integers=Arrays.asList(2,2,2);
		
		//Ci attendiamo che venga restituito null, poichè la lista di interi è più piccola rispetto a quelle delle domande
		assertNull(peer2.createAuserProfileKey(integers));
		
		peer3.registerNickname("Lucio");
		
		//Ci attendiamo che venga restituito null poichè peer3 passa al metodo createAuserProfileKey il valore null
		assertNull(peer3.createAuserProfileKey(null));
		
		List<Integer> integers_=Arrays.asList(1,1,1,1,1);
		
		//Ci attendiamo che la chiave restituita sia pari alla stringa 6
		assertEquals("6",peer3.createAuserProfileKey(integers_));
		
	}
	
	@Test
	void testCaseJoin()
	{
	
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null e nickname=null
		assertFalse(peer0.join(null, null));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota e nickname=null
		assertFalse(peer0.join("", null));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null e nickname=stringa vuota
		assertFalse(peer0.join(null, ""));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota e nickname=stringa vuota
		assertFalse(peer0.join("", ""));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=stringa vuota
		assertFalse(peer0.join("chiavesegreta", ""));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota
		assertFalse(peer0.join("", "Mario"));	
		//Ci attendiamo che venga restituito false poichè il nickname non corrisponde a quello del peer0.
		assertFalse(peer0.join("gruppo", "Mario"));
		
		peer0.registerNickname("Giancarlo");
		
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null e nickname=null
		assertFalse(peer0.join(null, null));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota e nickname=null
		assertFalse(peer0.join("", null));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=null
		assertFalse(peer0.join("gruppoprivato", null));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null
		assertFalse(peer0.join(null, "Marco"));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null e nickname=stringa vuota
		assertFalse(peer0.join(null, ""));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota e nickname=stringa vuota
		assertFalse(peer0.join("", ""));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=stringa vuota
		assertFalse(peer0.join("chiavesegreta", ""));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota
		assertFalse(peer0.join("", "Mario"));	
		//Ci attendiamo che venga restituito false poichè il nickname non corrisponde a quello del peer0.
		assertFalse(peer0.join("gruppo", "Mario"));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=stringa vuota
		assertFalse(peer0.join("", "Giancarlo"));
		
		//Ci attendiamo true visto che passiamo come profile key una stringa e come nickname il nickname usato per la registrazione
		assertTrue(peer0.join("grupposegreto","Giancarlo"));
		//Ci attendiamo che venga restituito false poichè peer0 appartiene già ad un gruppo.
		assertFalse(peer0.join("gruppononsegreto","Alice"));
		//Ci attendiamo che venga restituito false poichè peer0 appartiene già ad un gruppo anche se il nickname corrisponde a quello della registrazione.
		assertFalse(peer0.join("gruppononsegreto", "Giancarlo"));
		//Ci attendiamo che venga restituito false poichè il nickname non corrisponde a quello del peer1.
		assertFalse(peer1.join("gruppononsegreto",	"Alice"));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=stringa vuota
		assertFalse(peer1.join("gruppononsegreto",	""));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=null
		assertFalse(peer1.join("gruppononsegreto",	null));
		//Ci attendiamo che venga restituito false poichè passiamo profile key=null e nickname=null
		assertFalse(peer1.join(null, null));
		assertFalse(peer1.join("", null));
		assertFalse(peer1.join(null, ""));
		assertFalse(peer1.join("", ""));
		//Ci attendiamo che venga restituito false poichè passiamo nickname=stringa vuota
		assertFalse(peer1.join("chiavesegreta", ""));
		assertFalse(peer1.join("", "Andrea"));
		
		peer1.registerNickname("Roberto");
		assertFalse(peer1.join(null, null));
		assertFalse(peer1.join("", null));
		assertFalse(peer1.join(null, ""));
		assertFalse(peer1.join("", ""));
		assertFalse(peer1.join("chiavesegreta", ""));
		assertFalse(peer1.join("", "Andrea"));
		//Ci attendiamo che venga restituito false visto che il nickname passato non è quello di peer1
		assertFalse(peer1.join("grupposegreto","Giancarlo"));
		assertTrue(peer1.join("grupposegreto","Roberto"));
		
		assertFalse(peer2.join("prova",	"Alice"));
		assertFalse(peer2.join("prova",	""));
		assertFalse(peer2.join("prova",	null));
		assertFalse(peer2.join(null, null));
		assertFalse(peer2.join("", null));
		assertFalse(peer2.join(null, ""));
		assertFalse(peer2.join("", ""));
		assertFalse(peer2.join("chiavesegreta", ""));
		assertFalse(peer2.join("", "Antonio"));
		
		peer2.registerNickname("Ciccio");
		assertFalse(peer1.join(null, null));
		assertFalse(peer1.join("", null));
		assertFalse(peer1.join(null, ""));
		assertFalse(peer1.join("", ""));
		assertFalse(peer1.join("chiavesegreta", ""));
		assertFalse(peer1.join("", "Antonio"));
		assertTrue(peer2.join("grupposegreto", "Ciccio"));
		assertFalse(peer0.join("grupposegreto","Giancarlo"));
		
		peer3.registerNickname("Antonio");
		assertTrue(peer3.join("javaexpert", "Antonio"));
	}
	
	
	@Test
	void testCaseLeaveGroup()
	{
		List<Integer> integers=Arrays.asList(2,2,2,2,2);
		
		//Ci attendiamo che venga restituito false visto che peer0 non appartiene a nessun gruppo
		assertFalse(peer0.leaveGroup());
		
		peer0.registerNickname("Giancarlo");
		
		//Ci attendiamo che venga restituito false visto che peer0 non appartiene a nessun gruppo
		assertFalse(peer0.leaveGroup());
		String key=peer0.createAuserProfileKey(integers);
		
		//Ci attendiamo che venga restituito false visto che peer0 non appartiene a nessun gruppo
		assertFalse(peer0.leaveGroup());
		
		peer0.join(key,"Giancarlo");
		peer1.registerNickname("Nunzio");
		peer1.join(key, "Nunzio");
		
		//Ci attendiamo che venga restituito true visto che peer0 ha fatto la join precedentemente
		assertTrue(peer0.leaveGroup());
		//Ci attendiamo che venga restituito false visto che peer0 non appartiene a nessun gruppo
		assertFalse(peer0.leaveGroup());
		//Ci attendiamo che venga restituito true visto che peer0 ha fatto la join precedentemente
		assertTrue(peer1.leaveGroup());
		
	}
	
	@Test
	void testLeaveNetwork()
	{
		
		//Ci attendiamo che venga restituito sempre true ogni qualvolta viene chiamata la leaveNetwork().
		assertTrue(peer0.leaveNetwork());
		
		peer0.registerNickname("Giancarlo");
		
		assertTrue(peer0.leaveNetwork());
		
		List<Integer> integers=Arrays.asList(2,2,2,2,2);
		peer1.registerNickname("Gianmarco");
		String key_=peer1.createAuserProfileKey(integers);
		assertTrue(peer1.leaveNetwork());
		
		
		peer2.registerNickname("Armando");
		String key=peer2.createAuserProfileKey(integers);
		peer2.join(key,"Armando");
		assertTrue(peer2.leaveNetwork());
		peer0.registerNickname("Giancarlo");
		peer1.registerNickname("Alfonso");
		peer2.registerNickname("Nunzio");
		peer3.registerNickname("Michelangelo");
		assertTrue(peer3.leaveNetwork());
		assertTrue(peer2.leaveNetwork());
		assertTrue(peer1.leaveNetwork());
		assertTrue(peer0.leaveNetwork());
	
	}
	
	
	@Test
	void testCaseGetFriends()
	{
		
		List<Integer> integers=Arrays.asList(2,2,2,2,2);
		
		//Ci attendiamo che venga restituito null poichè il peer0 non appartiene a nessun gruppo
		assertNull(peer0.getFriends());
		
		peer0.registerNickname("Alice");
		List<String> friends=peer0.getFriends();
		//Ci attendiamo che venga restituito null poichè il peer0 non appartiene a nessun gruppo
		assertNull(friends);
		peer1.registerNickname("Ciro");
		
		String key=peer1.createAuserProfileKey(integers);
		peer1.join(key,"Ciro");
		//Ci attendiamo che venga restituito true poichè il peer1 è l'unico ad essere dentro al gruppo
		assertTrue(peer1.getFriends().isEmpty());
		
		peer2.registerNickname("Luigi");
		peer2.createAuserProfileKey(integers);
		peer2.join(key, "Luigi");
		
		//Ci attendiamo che peer2 veda nella lista degli amici l'utente "Ciro"
		assertEquals(Arrays.asList("Ciro"),peer2.getFriends());
		//Ci attendiamo che peer1 veda nella lista degli amici l'utente "Luigi"
		assertEquals(Arrays.asList("Luigi"),peer1.getFriends());
		
		List<Integer> integers_=Arrays.asList(1,1,1,1,1);
		peer3.registerNickname("Nicola");
		
		String key_=peer3.createAuserProfileKey(integers_);
		peer3.join(key_, "Luigi");
		
		/*
		 * Ci aspettiamo che venga restituito null, poichè la join non è andata a buon fine,
		 * visto che si è usato un nickname diverso da quello usato per la registrazione
		 */
		assertNull(peer3.getFriends());
		
		peer3.join(key_, "Nicola");
		assertTrue(peer3.getFriends().isEmpty());
	

	}
	
	
	@Test
	void testCaseSendMessageToGroup()
	{
		
		List<Integer> integers=Arrays.asList(1,1,1,1,1);
		peer0.registerNickname("Alice");
		peer1.registerNickname("Alberto");
		
		//Ci aspettiamo false poichè peer1 non appartiene a nessun gruppo
		assertFalse(peer1.sendMessageToGroup("ciao"));
		
		String key=peer1.createAuserProfileKey(integers);
		assertFalse(peer1.sendMessageToGroup("ciao"));
		peer1.join(key,"Alberto");
		
		//Ci aspettiamo false poichè viene dato null come parametro
		assertFalse(peer1.sendMessageToGroup(null));
		//Ci aspettiamo false poichè viene data una stringa vuota
		assertFalse(peer1.sendMessageToGroup(""));
		
		//Ci aspettiamo che il sendMessageToGroup restituisca true
		assertTrue(peer1.sendMessageToGroup("ciao"));
		
		peer2.registerNickname("Marco");
		assertFalse(peer2.sendMessageToGroup(null));
		assertFalse(peer2.sendMessageToGroup(""));
		
		//Ci aspettiamo false poichè peer2 non appartiene a nessun gruppo
		assertFalse(peer2.sendMessageToGroup("ciao"));
		
		peer2.createAuserProfileKey(integers);
		assertFalse(peer2.sendMessageToGroup(null));
		assertFalse(peer2.sendMessageToGroup(""));
		assertFalse(peer2.sendMessageToGroup("ciao"));
		peer2.join(key, "Marco");
		assertFalse(peer2.sendMessageToGroup(null));
		assertFalse(peer2.sendMessageToGroup(""));
		assertTrue(peer2.sendMessageToGroup("ciao"));
		
	}
	
	@Test
	void testCaseCreateGroup()
	{
		//Ci aspettiamo che venga restituito false visto che peer0 non ha effettuato ancora il registerNickname()
		assertFalse(peer0.createGroup("universitari"));
		
		assertFalse(peer0.createGroup(null));
		assertFalse(peer0.createGroup(""));
		peer0.registerNickname("Alice");
		assertTrue(peer0.createGroup("universitari"));
		assertFalse(peer0.createGroup(null));
		assertFalse(peer0.createGroup(""));
		assertFalse(peer0.createGroup("universitari"));
		assertFalse(peer0.createGroup("esperti"));
		peer1.registerNickname("Gianluigi");
		
		//Ci aspettiamo venga restituito false visto che il gruppo "universitari" è stato creato in precedenza
		assertFalse(peer1.createGroup("universitari"));		
		
		assertFalse(peer1.createGroup(null));
		assertFalse(peer1.createGroup(""));
		peer2.registerNickname("Marino");
		assertTrue(peer2.createGroup("gruppo predefinito"));

	}
	
	@Test
	void testCaseAddUserToGroup()
	{
		
		assertFalse(peer0.addUserToGroup(null));
		assertFalse(peer0.addUserToGroup(""));
		assertFalse(peer0.addUserToGroup("Guido"));
		peer0.registerNickname("Giulio");
		assertFalse(peer0.addUserToGroup(null));
		assertFalse(peer0.addUserToGroup(""));
		assertFalse(peer0.addUserToGroup("Guido"));
		peer0.createGroup("universitari");
		assertFalse(peer0.addUserToGroup(null));
		assertFalse(peer0.addUserToGroup(""));
		assertFalse(peer0.addUserToGroup("ciao"));
		
		//Ci aspettiamo che venga restituito false poichè non c'è nessun utente registrato col nome "Guido"
		assertFalse(peer0.addUserToGroup("Guido"));
		
		//Ci aspettiamo che venga restituito false poichè stiamo invitando noi stessi
		assertFalse(peer0.addUserToGroup("Giulio"));
		
		peer1.registerNickname("Natale");
		assertFalse(peer0.addUserToGroup(null));
		assertFalse(peer0.addUserToGroup(""));
		assertFalse(peer0.addUserToGroup("Guido"));
	    assertTrue(peer0.addUserToGroup("Natale"));
		peer2.registerNickname("Carlo");	
		assertFalse(peer0.addUserToGroup(null));
		assertFalse(peer0.addUserToGroup(""));
		assertFalse(peer0.addUserToGroup("Guido"));
		List<Integer> integers=Arrays.asList(2,2,2,2,2);
		String key=peer2.createAuserProfileKey(integers);
		peer2.join(key, "Carlo");
		
		//Ci aspettiamo che venga restituito false visto che "Carlo" già appartiene ad un gruppo
		assertFalse(peer0.addUserToGroup("Carlo"));
		
		peer3.registerNickname("Luca");
		assertTrue(peer2.addUserToGroup("Luca"));
		peer0.leaveGroup();
		
		//Ci aspettiamo che venga restituito true visto che peer0 aveva chiamato la leaveGroup
		assertTrue(peer1.addUserToGroup("Giulio"));
		//Ci aspettiamo che venga restituito false visto che "Giulio" già appartiene ad un gruppo
		assertFalse(peer2.addUserToGroup("Giulio"));
		
	}
	


}
