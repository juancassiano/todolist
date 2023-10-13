package br.com.juancassiano.todolist.task.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.juancassiano.todolist.user.UserModel;
import br.com.juancassiano.todolist.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{
    @Autowired
    private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        if(request.getServletPath().equals("/tasks/")){
          String authorization = request.getHeader("Authorization");

          String authEncoded = authorization.substring("Basic".length()).trim();

          byte[] authDecode =Base64.getDecoder().decode(authEncoded);
          String authString = new String(authDecode);
          String[] credentials = authString.split(":");
          String username = credentials[0];
          String password = credentials[1];

          Optional<UserModel> user = this.userRepository.findByUsername(username);
          if(user.isEmpty()) {
            response.sendError(401);
          }else{

          Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.get().getPassword());

          if(passwordVerify.verified){
              request.setAttribute("idUser", user.get().getId());
              filterChain.doFilter(request, response);
          }else{
            response.sendError(401);
          }
        }
      }else{
        filterChain.doFilter(request, response);
      }
  }

}
