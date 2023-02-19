import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { filter } from 'rxjs';

import { Todo } from './todo';
import { TodoService } from './todo.service';

describe('TodoService', () => {
  //small collection of test todos

  const testTodos: Todo[] = [
    {
      _id: 'alwin_id',
      owner: 'Alwin',
      status: false,
      body: 'Revvin up your engine listen to her howlin roar',
      category: 'software design',
    },
    {
      _id: 'harry_id',
      owner:'Harry',
      status: true,
      body: 'Metal under tension beggin you to touch and go',
      category: 'homework',
    },
    {
      _id: 'kk_id',
      owner: 'KK',
      status: true,
      body: 'Highway to the Danger Zone ride into the Danger Zone',
      category: 'video games',
    },
    {
      _id: 'nic_id',
      owner: 'Nic',
      status: false,
      body: 'Headin into twilight spreadin out her wings tonight',
      category: 'groceries',
    },
  ];
  let todoService: TodoService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
  });
  httpClient = TestBed.inject(HttpClient);
  httpTestingController = TestBed.inject(HttpTestingController);
  //create an instance of the server with the mock HTTP client
  todoService = new TodoService(httpClient);
  });

  afterEach(() => {
    //after each test, assert that there are no more pending requests
    httpTestingController.verify();
  });

  //testing for filtering on the database
  describe('getTodos()', () => {
    it('should be created', () => {
      expect(todoService).toBeTruthy();
    });
  });

  describe('filterTodos()', () => {
    it('filters by body', () => {
      const todoBody = 'Revvin up your engine listen to her howlin roar';
      const filteredTodos = todoService.filterTodos(testTodos, {body: todoBody});
      expect(filteredTodos.length).toBe(1);
      filteredTodos.forEach(todo => {
        expect(todo.body.indexOf(todoBody)).toBeGreaterThanOrEqual(0);
      });
    });
    it('filters by category',()=>{
      const todoCategory = 'video games';
      const filteredTodos = todoService.filterTodos(testTodos,{category:todoCategory});
      expect(filteredTodos.length).toBe(1);
      filteredTodos.forEach(todo => {
        expect(todo.category.indexOf(todoCategory)).toBeGreaterThanOrEqual(0);
      });
    });
    it('filters by owner', () => {
      const todoOwner = 'Nic';
      const filteredTodos = todoService.filterTodos(testTodos, {owner: todoOwner});
      expect(filteredTodos.length).toBe(1);
      filteredTodos.forEach(todo => {
        expect(todo.owner.indexOf(todoOwner)).toBeGreaterThanOrEqual(0);
      });
    });
  });



});