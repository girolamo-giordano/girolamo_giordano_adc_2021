package it.semanticharmony;

import java.util.ArrayList;
import java.util.List;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {
	
	@Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
	private static String master;

	@Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
	private static int id;
	
	
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		Main example = new Main();
		MessageListenerImpl mli=new MessageListenerImpl(id);
		final CmdLineParser parser = new CmdLineParser(example);  
		try  
		{  
			parser.parseArgument(args);  
			TextIO textIO = TextIoFactory.getTextIO();
			TextTerminal terminal = textIO.getTextTerminal();
			SemanticHarmonySocialNetworkImpl peer = 
					new SemanticHarmonySocialNetworkImpl(id, master, mli);
			
			terminal.printf("\nStaring peer id: %d on master node: %s\n",
					id, master);
			
			terminal.printf("Register with nickname\n");
			String nickname=textIO.newStringInputReader().read("Nickname");
			while(!peer.registerNickname(nickname))
			{
				nickname=textIO.newStringInputReader().read("Nickname");
			}
			terminal.printf("Registrazione avvenuta con successo\n");
			
			while(true) {
				printMenu(terminal);
				
				int option = textIO.newIntInputReader()
						.withMaxVal(7)
						.withMinVal(1)
						.read("Option");
				switch (option) {
				case 1:
					terminal.printf("\nGET USER PROFILE QUESTIONS AND JOIN GROUP\n");
					
					List<String> questions= peer.getUserProfileQuestions();
					List<Integer> answers = new ArrayList<Integer>();
					
					for(String question: questions)
					{
						int choice = textIO.newIntInputReader()
								.withMaxVal(3)
								.withMinVal(1)
								.withDefaultValue(1)
								.read(question);
						terminal.printf("Hai votato con "+choice+"\n");
						answers.add(choice);
						
					}
					String key= peer.createAuserProfileKey(answers);
					if(!peer.join(key, nickname))
						terminal.printf("Appartieni già ad un gruppo\n");
					break;
				
				case 2:
					terminal.printf("GET LIST FRIENDS\n");
					List<String> myfriends= peer.getFriends();
					if(myfriends == null)
					{
						terminal.printf("Non sei ancora entrato in un gruppo\n");
					}
					else if(myfriends.isEmpty())
					{
						terminal.printf("Sei solo nel gruppo\n");
					}
					else
					{
						for(String friend:myfriends)
							terminal.printf(friend+"\n");
					}break;
				case 3:
					terminal.printf("SEND MESSAGE TO GROUP\n");
					String message= textIO.newStringInputReader().read("Message:");
					if(! peer.sendMessageToGroup(message))
						terminal.printf("errore nell'invio del messaggio\n");
					break;
				case 4:
					terminal.printf("ADD USER TO YOUR GROUP\n");
					if(peer.getFriends() != null)
					{
						String nick= textIO.newStringInputReader().read("Nickname:");
						if(peer.addUserToGroup(nick))
							terminal.printf("Hai aggiunto "+nick+" con successo nel tuo gruppo");
						else
							terminal.printf("Utente non registrato o già appartenente ad un gruppo");
					}
					else
					{
						terminal.printf("Non appartieni a nessun gruppo");
					}
					break;
				case 5:
					terminal.printf("LEAVE GROUP\n");
					if(peer.leaveGroup())
						terminal.printf("Sei uscito dal gruppo con successo\n");
					else
						terminal.printf("Non appartieni a nessun gruppo\n");
					break;
				case 6:
					terminal.printf("CREATE GROUP\n");
					String keygroup= textIO.newStringInputReader().read("Key group:");
					if(peer.createGroup(keygroup))
						terminal.printf("Gruppo creato con successo\n");
					else
						terminal.printf("Errore nella creazione del gruppo\n");
					break;
				case 7:
					terminal.printf("EXIT\n");
					if(peer.leaveNetwork())
					{
						System.exit(0);
					}
					break;
				default:
					break;
				}
			}



		}  
		catch (CmdLineException clEx)  
		{  
			System.err.println("ERROR: Unable to parse command-line options: " + clEx);  
		}  


	}
	
	@SuppressWarnings("rawtypes")
	public static void printMenu(TextTerminal terminal) {
		terminal.printf("\n1 - GET QUESTIONS AND JOIN GROUP\n");
		terminal.printf("\n2 - GET LIST FRIENDS\n");
		terminal.printf("\n3 - SEND MESSAGE TO GROUP\n");
		terminal.printf("\n4 - ADD USER TO YOUR GROUP\n");
		terminal.printf("\n5 - LEAVE GROUP\n");
		terminal.printf("\n6 - CREATE A NEW GROUP\n");
		terminal.printf("\n7 - EXIT\n");

	}

}
