package edu.studyapp.repository;

import edu.studyapp.model.Message;
import edu.studyapp.model.MessageStatus;
import edu.studyapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.recipient.username = :recipient " +
            "AND m.sender.username = :sender " +
            "OR (m.sender.username =:recipient " +
            "AND m.recipient.username =:sender)")
    List<Message> getMessagesByUsername (@Param("sender") String sender,
            @Param("recipient") String recipient);
    @Query("SELECT m FROM Message m WHERE m.recipient.username = :sender " +
            "AND m.status = :status AND m.sender.username = :recipient")
    List<Message> getMessagesByStatus (@Param("sender") String sender,
            @Param("recipient") String recipient,
                                     @Param("status") MessageStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.status = 'READ' WHERE m.recipient.username = :sender" +
            " AND m.status = 'UNREAD' " +
            "AND m.sender.username =:recipient")
    void markAllAsRead(@Param("sender") String sender,
            @Param("recipient") String recipient);

    @Query("SELECT m FROM Message m WHERE m.recipient.username = :recipient " +
            "AND m.text = :text" +
            " AND m.sender.username = :sender")
    List<Message> getMessage(@Param("sender") String sender,
                       @Param("recipient") String recipient,
                       @Param("text") String text);
    @Query("SELECT DISTINCT r.recipient FROM Message r WHERE r.sender.username = :sender")
    List<User> getMyChats(@Param("sender") String sender);



}
