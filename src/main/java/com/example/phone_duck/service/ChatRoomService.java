package com.example.phone_duck.service;
import com.example.phone_duck.Model.ChatRoom;
import com.example.phone_duck.repo.ChatRoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepo chatRoomRepo;
    public Optional<ChatRoom> getChatRoom(Long id){
        return chatRoomRepo.findById(id);
    }

    public ChatRoom getChatRoom(String name){
        return chatRoomRepo.findByName(name);
    }
    public void saveChatRoom(ChatRoom chatRoom){
        chatRoomRepo.save(chatRoom);
    }
    public List<ChatRoom> readAllActiveChatRoom(){
        return chatRoomRepo.findAllByIsOnlineTrue();
    }
    public List<ChatRoom> readAll(){
        return chatRoomRepo.findAll();
    }
    public void create(ChatRoom chatRoom){
        chatRoomRepo.save(chatRoom);
    }
    public void delete(Long id){
        chatRoomRepo.deleteById(id);
    }
}
