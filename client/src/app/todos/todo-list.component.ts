import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';
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

  private ngUnsubscribe = new Subject<void>();

  constructor(private todoService: TodoService, private snackBar: MatSnackBar) {
  }
  getTodosFromServer(){
    this.todoService.getTodos({
      status: this.todoStatus
    }).pipe(
      takeUntil(this.ngUnsubscribe)
    ).subscribe({

      next: (returnedTodos) => {
        this.serverFilteredTodo = returnedTodos;
        this.updateFilter();
      },

      error: (e) => {
        this.snackBar.open(
          'Problem contacting the server – try again',
          'OK',
          // The message will disappear after 3 seconds.
          { duration: 3000 });
        console.error('We couldn\'t get the list of users; the server might be down');
      },
    });
  }

  public updateFilter(){
    this.filteredTodos = this.todoService.filterTodos(
      this.serverFilteredTodo, { body: this.todoBody, category: this.todoCategory, owner: this.todoOwner }
    );
  }
  ngOnInit(): void {
    this.getTodosFromServer();
  }
  /*
  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }*/
}
