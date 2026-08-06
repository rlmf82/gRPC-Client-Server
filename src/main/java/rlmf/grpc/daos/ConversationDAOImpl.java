package rlmf.grpc.daos;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import rlmf.grpc.entities.Conversation;

/**
 * This is a fake storage. The data is saved in memory while the application is running.
 */
public class ConversationDAOImpl implements ConversationDAO{

	private static List<Conversation> conversations = new LinkedList<>();

	@Override
	public void save(Conversation conversation) {
		System.out.println("New Conversation saved:" + conversation);
		conversations.add(conversation);
	}

	@Override
	public List<Conversation> retrieveMyConversations(String receiverName) {
		System.out.println("Getting all conversations");
		return conversations.stream()
				.filter(c -> c.getDestinatary().equalsIgnoreCase(receiverName) || c.getSender().equalsIgnoreCase(receiverName))
				.toList();
	}

	@Override
	public Optional<Conversation> getById(UUID id) {
		System.out.println("Getting by id");
		return conversations.stream().filter(c -> c.getId().equals(id)).findFirst();
	}

	@Override
	public void update(Conversation updatedConversation) {
		System.out.println("Updating conversation:" + updatedConversation);
		Optional<Conversation> conversation = conversations.stream().filter(c -> c.getId().equals(updatedConversation.getId())).findFirst();
		conversation.get().setMessage(updatedConversation.getMessage());
	}
}
