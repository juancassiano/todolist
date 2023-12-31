package br.com.juancassiano.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {

  List<TaskModel> findByIdUser(UUID idUser);

//  TaskModel findByIdAndByIdUser(UUID id, UUID idUser);
}
