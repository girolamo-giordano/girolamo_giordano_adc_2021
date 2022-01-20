# SEMANTICHARMONYSOCIALNETWORK

Progetto per l'esame di Architetture Distribuite per il Cloud.

## Prerequisiti del progetto

- Java 8 o superiore.
- Apache Maven.


## Descrizione del progetto

Il progetto mira a sviluppare un social network basato sugli interessi degli utenti, nella soluzione sviluppata vengono poste delle domande riguardanti la conoscenza di java ed a seconda dello score ottenuto nel rispondere a tutte le domande viene assegnata una key, con tale key è possibile accedere al gruppo di persone che hanno ottenuto lo stesso score. La soluzione realizzata permette di:

- Registrare il proprio nickname.
- Calcolare il rating e far accedere l'utente al gruppo una volta restituite le domande.
- Vedere la lista degli amici online
- Mandare un messaggio all'interno del gruppo
- Lasciare il gruppo corrente
- Creare un proprio gruppo
- Invitare persone al gruppo corrente

## Tecnologie e librerie utilizzate

Per lo sviluppo della soluzione sono stati utilizzati:

- Java: Linguaggio di programmazione utilizzato per lo sviluppo del progetto.
- [TomP2P](https://tomp2p.net/): Libreria che permette la gestione di dht all'interno della rete.
- [Maven](https://maven.apache.org/): Software project management utilizzato per la gestione del progetto.
- JUnit: Libreria utilizzata per poter effettuare testing.
- [Docker](https://www.docker.com/): Software utilizzato per la creazione di container.

## Struttura del progetto

Lo sviluppo della soluzione parte dall'implementazione delle [API base](https://github.com/spagnuolocarmine/distributedsystems-unisa/blob/master/homework/SemanticHarmonySocialNetwork.java).

Nel package ```it.semanticharmony``` troviamo:

- **Main** classe che utilizza l'implementazione SemanticHarmonySocialNetwork
- **MessageListener**  un'interfaccia per il listener di messaggi ricevuti da un peer.
- **MessageListenerImpl** classe che implementa l'interfaccia MessageListener.
- **PeerUser** oggetto che salva le informazioni degli utenti all'interno della rete. Tiene traccia di:
	- **peeradd** viene salvato il riferimento PeerAddress dell'utente
	- **busy** tiene traccia se l'utente si trova all'interno di un gruppo
  	- **score** tiene traccia dello score dell'utente
 	- **nickname** tiene traccia del nickname utilizzato dall'utente all'interno del network
	- **key** tiene traccia della key del gruppo di appartenenza dell'utente
	
- **SemanticHarmonySocialNetwork** interfaccia che definisce le operazioni da implementare per lo sviluppo del social network.
- **SemanticHarmonySocialNetworkImpl** un'implementazione dell'interfaccia. SemanticHarmonySocialNetwork che sfrutta la libreria TomP2P.

### Descrizione dei metodi implementati
All'interno della classe SemanticHarmonySocialNetwork troviamo l'implementazione dei metodi descritti nell'interfaccia SemanticHarmonySocialNetworkImpl, tra cui troviamo:

- **registerNickname:** Metodo che permette di registrare il proprio nickname all'interno della rete.
- **getUserProfileQuestions:** Metodo che restituisce le domande a cui l'utente dovrà rispondere per ottenere uno score.
- **createAuserProfileKey:** Metodo che prende in input una lista di score per ogni domanda risposta, viene sommato lo score totale delle risposte e viene restituita la key sottoforma di stringa.
- **join:** Metodo che permette di effettuare la join all'interno del gruppo, dando in input la chiave ed il proprio nickname.
- **getFriends:** Restituisce la lista degli amici che si trovano all'interno del gruppo.
- **createGroup:** Metodo che permette di creare un nuovo gruppo all'interno del social network.
- **sendMessageToGroup:** Permette di inviare un messaggio all'interno del gruppo, il messaggio verrà ricevuto da tutti gli utenti che si trovano attualmente in quel gruppo.
- **leaveGroup:** Metodo che permette di lasciare il gruppo attuale.
- **leaveNetwork:** Metodo che permette di uscire dalla rete.

## Dipendenze
Le dipendenze necessarie sono definite all'interno del file pom.xml.

```
<dependencies>

		<dependency>
			<groupId>net.tomp2p</groupId>
			<artifactId>tomp2p-all</artifactId>
			<version>5.0-Beta8</version>
		</dependency>

		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter-engine</artifactId>
			<version>5.5.2</version>
		</dependency>
		<dependency>
			<groupId>args4j</groupId>
			<artifactId>args4j</artifactId>
			<version>2.33</version>
		</dependency>

		<dependency>
			<groupId>org.beryx</groupId>
			<artifactId>text-io</artifactId>
			<version>3.3.0</version>
		</dependency>
	</dependencies>
```

## TEST CASE
Per il testing è stato utilizzato il framework JUnit e sono stati implementati all'interno della classe **TestSemanticHarmonySocialNetwork**, classe che si può trovare nel percorso ```src/test/java/it/semanticharmony```. Prima che ogni test venga chiamato, viene eseguito il metodo **setBefore()** con il tag @BeforeEach, così da eseguirlo prima di ogni test.

```
	@BeforeEach
	 void set() throws Exception
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
```

I test case implementati sono:

- **testCaseRegisterNickname:**

```
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
```

- **testCaseGetUserProfileQuestions:**

```
	@Test
	void testCaseGetUserProfileQuestions()
	{
		peer0.registerNickname("Alice");
		//Il peer0 chiama la getUserProfileQuestions dove gli verrà restituita una lista con le domande
		assertTrue(!peer0.getUserProfileQuestions().isEmpty());
	
	}
		
	}
```

- **testCaseCreateAUserProfileKey:**

```
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
```
- **testCaseJoin:**

```
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
```

- **testCaseLeaveNetwork:**

```
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
```


- **testCaseLeaveGroup:**

```
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
```

- **testCaseGetFriends:**

```
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
```


- **testCaseSendMessageToGroup:**

```
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
```

- **testCaseCreateGroup:**

```
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
```

- ** testCaseAddUserToGroup:**

```
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
```

Con @AfterEach andiamo a chiamare dopo ogni test il metodo setAfter(), che fa la leaveNetwork di tutti i peer e chiama il metodo resetdht in modo da effettuare lo shutdown delle dht dei vari peer, così da non avere conflitti tra un test e l'altro:

```
	@AfterEach
	void setAfter() throws InterruptedException
	{
		peer0.leaveNetwork();
		peer1.leaveNetwork();
		peer2.leaveNetwork();
		peer3.leaveNetwork();
		SemanticHarmonySocialNetworkImpl.resetDht();
	}
```


## Docker

Qui di seguito viene mostrato il Dockerfile

```
FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY src ./src
COPY pom.xml ./
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV java = /usr/bin/java
COPY --from=0 /app/target/semanticharmonysocialnetwork-1.0-jar-with-dependencies.jar ./

ENTRYPOINT ["java", "-jar", "semanticharmonysocialnetwork-1.0-jar-with-dependencies.jar"] 
CMD ["-m","-id"]

```

- Per eseguire l'applicativo utilizzando Docker, prima di tutto bisogna costruire il container:

	```
	docker build --no-cache -t semantic-harmony .
	```

- Dopo aver fatto la build, bisogna far eseguire il master peer.

	```
	docker run -i --name MASTER-PEER semantic-harmony -m 127.0.0.1 -id 0
	```
	
	**NB**: Dopo il primo run è possibile avviare il master peer inserendo il seguente comando:
		```docker start -i MASTER-PEER```
	
- Successivamente è possibile far eseguire dei peer generici:
	
	- Una volta avviato il master peer, bisogna controllare l'indirizzo ip del container:
		- Si verifica l'id del container in esecuzione:
		
			```
			docker ps
			```
		- Successivamente si verifica l'indirizzo ip del container:
		
			```
			docker inspect <container ID>
			```
	
	- Una volta verificato l'indirizzo ip del container è possibile avviare il peer generico:
	
		```
		docker run -i --name PEER-1 semantic-harmony -m 172.17.0.2 -id 1
		```
	**NB**: Dopo il primo run è possibile avviare il master peer inserendo il seguente comando:
		```docker start -i PEER-1```	
		

