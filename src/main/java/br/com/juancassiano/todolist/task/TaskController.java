package br.com.juancassiano.todolist.task;

import br.com.juancassiano.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private TaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
    taskModel.setIdUser((UUID) request.getAttribute("idUser"));
    LocalDateTime currentDate = LocalDateTime.now();
    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término deve ser maior que a data atual");
    }
    if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término deve ser maior que a data atual");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(this.taskRepository.save(taskModel));
  }

  @GetMapping("/")
  public ResponseEntity<List<TaskModel>> list(HttpServletRequest request){
    List<TaskModel> userTasks = this.taskRepository.findByIdUser((UUID) request.getAttribute( "idUser"));

    return ResponseEntity.ok(userTasks);
  }


  @PutMapping("/{id}")
  public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request){
    Object idUser = request.getAttribute( "idUser");

//    TaskModel task = this.taskRepository.findById(id).orElseThrow(
//            () -> new RuntimeException("Task not found")
//    );
    TaskModel task = this.taskRepository.findById(id).orElse(null); // Use .orElse(null) para tratar o caso de task ser null

    if (task == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              "Tarefa não encontrada"
      );
    }

    if(!task.getIdUser().equals(idUser)){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              "Usuário sem permissão para alterar essa tarefa"
      );
    }

//    this.taskRepository.findByIdAndByIdUser(task.getId(), (UUID) idUser);
    Utils.copyNonNullProperties(taskModel, task);

    return ResponseEntity.ok().body(this.taskRepository.save(task));

  }


  
}
