package com.devvitormarques.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;

/*
 * Modifiers
 * public
 * private
 * protected
 *  */

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;  
  private UserDTO userDto;
  /*
 can be;
 String
 Int
 Double(0.0)
 Float
 chat(A C B)
 Date
 void (no return) */
 @PostMapping("/create")
  public ResponseEntity create(@RequestBody UserModel userModel) {
    var user = this.userRepository.findByUsername(userModel.getUsername());
    if(user != null) {
      System.out.println("This user already exists");
      return ResponseEntity.status(400).body("This user already exists.");
    }

    var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

    userModel.setPassword(hashedPassword);
    var usedCreated = this.userRepository.save(userModel);
    return ResponseEntity.status(201).body(usedCreated);
  }

  @PostMapping("/login") 
  public ResponseEntity login(@RequestBody UserModel userModel, HttpServletRequest request) {
    
    var user = this.userRepository.findByUsername(userModel.getUsername());
    if(user == null) {
      return ResponseEntity.status(400).body("This user doesn't exists.");
    }

    var newDto = new UserDTO();

    var verifyPw = BCrypt.verifyer().verify(userModel.getPassword().toCharArray(), user.getPassword());

    newDto.setId(user.getId());
    newDto.setName(user.getName());
    newDto.setUsername(user.getUsername());

    if(verifyPw.verified) { 
      request.setAttribute("userId", user.getId());
      return ResponseEntity.status(401).body(newDto);
    } else {
      return ResponseEntity.status(401).body("Wrong credentials.");
      
    }


    
  }

}
