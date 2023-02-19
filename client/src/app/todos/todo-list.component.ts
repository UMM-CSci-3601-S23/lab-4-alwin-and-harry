import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Todo, TodoCategory } from './todo';
import { TodoService } from './todo.service';

@Component({
  selector: 'app-todo-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.scss'],
  providers: []
})
export class TodoListComponent implements OnInit{
  public serverFilteredTodo: Todo[];
  public filteredTodos: Todo[];

  public todoOwner: string;
  public todoStatus: boolean;
  public todoBody: string;
  public todoCategory: TodoCategory;
  public viewType: 'card' | 'list' = 'card';

  constructor(private todoService: TodoService, private snackBar: MatSnackBar) {
  }
  getTodosFromServer(){
    //will add to once we figure out the database
  }

  public updateFilter(){
    this.filteredTodos = this.todoService.filterTodos(
      this.serverFilteredTodo, { body: this.todoBody, category: this.todoCategory, owner: this.todoOwner }
    );
  }
  ngOnInit(): void {
    this.getTodosFromServer();
  }
}
