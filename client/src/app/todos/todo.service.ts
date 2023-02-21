import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Todo, TodoCategory } from './todo';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  readonly todoUrl: string = environment.apiUrl + 'todos';

  constructor(private httpClient: HttpClient) {
  }
  //Filters here

  //Filtering using the database


  //Filtering using Angular
  //Body, owner, and, category are being filtered through Angular

  filterTodos(todos: Todo[], filters?: { body?: string; category?: string; owner?: string }): Todo[] {
    let filteredTodos = todos;

    if (filters.body){
      filters.body = filters.body.toLowerCase();
      filteredTodos = filteredTodos.filter(todo => todo.body.toLowerCase().indexOf(filters.body) !== -1);
    }

    if (filters.category){
      filters.category = filters.category.toLowerCase();
      filteredTodos = filteredTodos.filter(todo => todo.category.toLowerCase().indexOf(filters.category) !== -1);
    }
    if (filters.owner){
      filters.owner = filters.owner.toLowerCase();
      filteredTodos = filteredTodos.filter(todo => todo.owner.toLowerCase().indexOf(filters.owner) !== -1);
    }

    return filteredTodos;

}
}
