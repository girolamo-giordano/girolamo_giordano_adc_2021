package it.semanticharmony;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

public class MessageListenerImpl implements MessageListener {

	int peerid;
	//private JFrame frame;
	//private JTextArea textArea;
	//private JScrollPane scrollPane;
	
	public MessageListenerImpl(int peerid)
	{
		this.peerid=peerid;
		/*
		frame = new JFrame("Chat Group");
		
		frame.setSize(700,500);
		textArea = new JTextArea(5, 20);
		scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false); 
		frame.add(textArea);
		*/
		//frame.add(scrollPane);
	}
	
	@SuppressWarnings("rawtypes")
	public Object parseMessage(Object obj) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	    Date date = new Date();  
		TextIO textIO = TextIoFactory.getTextIO();
		TextTerminal terminal = textIO.getTextTerminal();
		terminal.printf(formatter.format(date)+" "+obj.toString()+"\n");
		//JOptionPane.showMessageDialog(null, "\n["+peerid+"] (Direct Message Received) "+obj+"\n\n", "InfoBox: " + "p2p", JOptionPane.INFORMATION_MESSAGE);
		//frame.setVisible(true);
		//textArea.append(formatter.format(date)+" "+obj.toString());
		return "success";
	}
	
}
