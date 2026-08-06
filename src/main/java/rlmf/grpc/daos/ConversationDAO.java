package rlmf.grpc.daos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import rlmf.grpc.entities.Conversation;

public interface ConversationDAO {

	public void save(Conversation conversation);
	
	public List<Conversation> retrieveMyConversations(String receiverName);
	
	public Optional<Conversation> getById(UUID id);
	
	public void update(Conversation conversation);
	
}
