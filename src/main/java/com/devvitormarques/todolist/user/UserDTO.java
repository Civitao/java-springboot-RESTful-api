package com.devvitormarques.todolist.user;

import java.util.UUID;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDTO{
    
  @Id
  private UUID id;

  private String username;
  private String name;

}
