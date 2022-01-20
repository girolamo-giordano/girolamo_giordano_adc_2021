package it.semanticharmony;

import java.util.List;

public interface SemanticHarmonySocialNetwork {
	
	
	/**
	 * Gets the social network users questions.
	 * @return a list of String that is the profile questions.
	 */
	public List<String> getUserProfileQuestions();
	
	/**
	 * Creates a new user profile key according the user answers.
	 * @param _answer a list of answers.
	 * @return a String, the obtained profile key.
	 */
	public String createAuserProfileKey(List<Integer> _answer);
	
	/**
	 * Joins in the Network. An automatic messages to each potential new friend is generated.
	 * @param _profile_key a String, the user profile key according the user answers
	 * @param _nick_name a String, the nickname of the user in the network.
	 * @return true if the join success, fail otherwise.
	 */
	public boolean join(String _profile_key,String _nick_name);
	
	/**
	 * Gets the nicknames of all automatically creates friendships. 
	 * @return a list of String.
	 */
	public List<String> getFriends();
	
	/**
	 * Add a user in your group
	 * @param nickname a String, the nickname of the user in the network
	 * @return true if the user is added to group, fail otherwise 
	 * 
	 */
	public boolean addUserToGroup(String nickname);
	
	/**
	 * Register the given nickname inside the Network
	 * @param nickname a String, the nickname used to register inside the Network
	 * @return true if the user is registered successfully with the given network, false otherwise
	 */
	public boolean registerNickname(String nickname);
	
	
	/**
	 * Send the message to all friends inside your group
	 * @param message a String, the message sent to the group
	 * @return true if the message is successfully delivered to the group, false otherwise
	 */
	public boolean sendMessageToGroup(String message);
	
	/**
	 * Leave the group
	 * @return true if your successfully leave the group, false otherwise
	 */
	public boolean leaveGroup();
	
	/**
	 * Create a group inside the Network
	 * @param key a String, the key used for the creation of the group
	 * @return true if the group is created successful, false otherwise
	 */
	public boolean createGroup(String key);
	
	/**
	 * Leave the Network
	 * @return true if the network is leaved successfully, false otherwise
	 */
	public boolean leaveNetwork();

}
