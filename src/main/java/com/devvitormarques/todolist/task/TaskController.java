package com.devvitormarques.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devvitormarques.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  
  @Autowired 
  private ITaskRepository taskRepository;

  @PostMapping("/create")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    
    var currentDate = LocalDateTime.now();
    if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(400).body("Starting/Ending datetime must be greater than current datetime");  
    }
     if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(400).body("Starting datetime must be less than ending datetime");  
    }

    var requestUserId = request.getAttribute("userId");
    taskModel.setUserId((UUID)requestUserId);

    var task = this.taskRepository.save(taskModel);
    return ResponseEntity.status(200).body(task);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var requestUserId = request.getAttribute("userId");
    var tasks = this.taskRepository.findByUserId((UUID)requestUserId);
  
    return tasks;
  }

  
  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel,  @PathVariable UUID id, HttpServletRequest request) {
    
    var requestUserId = request.getAttribute("userId");
    var task = this.taskRepository.findById(id).orElse(null);


    if(task == null) {    
      return ResponseEntity.status(400).body("Task not found");
    }


    if(!task.getUserId().equals(requestUserId)) {
      return ResponseEntity.status(400).body("This user is not allowed to change task data");  

    }  
    
      
    Utils.copyNonNullProperties(taskModel, task);
    taskModel.setUserId((UUID)requestUserId);
    // taskModel.setId(id);
    var updatedTask = this.taskRepository.save(task);
    return ResponseEntity.status(200).body(this.taskRepository.save(updatedTask));

  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(@PathVariable UUID id, HttpServletRequest request) {
    var requestUserId = request.getAttribute("userId");
    var task = taskRepository.findById(id).orElse(null);
   
    if(task == null) {    
      return ResponseEntity.status(400).body("Task not found");
    }


    if(!task.getUserId().equals(requestUserId)) {
      return ResponseEntity.status(400).body("This user is not allowed to change task data");  
    } else {
      taskRepository.deleteById((UUID)id);
      return ResponseEntity.status(202).body("Task successfully deleted!");
    }  
  } 

}

