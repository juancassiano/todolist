package br.com.juancassiano.todolist.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController()
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;
  
  @PostMapping("/")
  public ResponseEntity<UserModel> create(@RequestBody UserModel userModel){
    Optional<UserModel> user = this.userRepository.findByUsername(userModel.getUsername());

    if(user.isPresent()){
      return ResponseEntity.badRequest().build();
    }

    String hashedPassword = BCrypt.withDefaults()
    .hashToString(12, userModel.getPassword().toCharArray());

    userModel.setPassword(hashedPassword);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userRepository.save(userModel));
  }
}
