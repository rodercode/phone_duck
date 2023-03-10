package com.example.phone_duck.rest;

import com.example.phone_duck.Model.ChatRoom;
import com.example.phone_duck.exception.ListEmptyException;
import com.example.phone_duck.exception.ResourceNotFoundException;
import com.example.phone_duck.exception.UniqueValidationException;
import com.example.phone_duck.service.ChatRoomService;
import com.example.phone_duck.websocket.ChatRoomSocketHandler;
import com.example.phone_duck.websocket.MainChatRoomSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("api/channels")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MainChatRoomSocketHandler mainChatRoomSocketHandler;

    @Autowired
    private ChatRoomSocketHandler chatRoomSocketHandler;

    @GetMapping
    private ResponseEntity<List<ChatRoom>> showAllChatRoom() throws IOException {
        if (chatRoomService.readAll().isEmpty())
            throw new ListEmptyException("List is empty");
        else {
            for (ChatRoom chatRoom : chatRoomService.readAllActiveChatRoom()) {
                mainChatRoomSocketHandler.broadcast("Active: " + chatRoom.getName());
            }
            return new ResponseEntity<>(chatRoomService.readAll(), HttpStatus.OK);
        }
    }

    @PostMapping
    private ResponseEntity<String> createChatRoom(@RequestBody ChatRoom chatRoom) throws IOException {
        if (chatRoomService.getChatRoom(chatRoom.getName()) != null)
            throw new UniqueValidationException("There already exist a Chat Room with this name");
        else {
            chatRoomService.create(chatRoom);
            mainChatRoomSocketHandler.broadcast(chatRoom.getName() + " has been created");
            return new ResponseEntity<>(chatRoom.getName() + " has been created", HttpStatus.CREATED);
        }
    }

    @PatchMapping("/{status}/{id}")
    private ResponseEntity<String> activateChatRoom(@PathVariable String status, @PathVariable Long id) throws IOException {
        if (chatRoomService.getChatRoom(id).isEmpty())
            throw new ResourceNotFoundException("Could not update chat room because it doesn't exist");
        else {
            Optional<ChatRoom> chatRoom = chatRoomService.getChatRoom(id);
            switch (status) {
                case "online" -> chatRoom.get().setIsOnline(true);
                case "offline" -> chatRoom.get().setIsOnline(false);
                default -> throw new IllegalStateException(status + "was not defined");
            }
            chatRoomService.saveChatRoom(chatRoom.get());
            mainChatRoomSocketHandler.broadcast(chatRoomService.getChatRoom(id).get().getName() +" is " + status);
            return new ResponseEntity<>(chatRoomService.getChatRoom(id).get().getName() + " is " + status, HttpStatus.OK);
        }
    }
    @DeleteMapping("/{id}")
    private ResponseEntity<String> deleteChatRoom(@PathVariable("id") Long id) throws IOException {
        if (chatRoomService.getChatRoom(id).isEmpty())
            throw new ResourceNotFoundException("Chat Room you were trying to delete does not exist");
        else {
            String name = chatRoomService.getChatRoom(id).get().getName();
            chatRoomService.delete(id);
            mainChatRoomSocketHandler.broadcast( name + " has been deleted");
            return new ResponseEntity<>(name + " has been deleted", HttpStatus.OK);
        }
    }
}


