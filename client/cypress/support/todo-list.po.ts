import { TodoCategory } from 'src/app/todos/todo';

export class TodoListPage{
  navigateTo() {
    return cy.visit('/todos');
  }

  getTodoTitle() {
    return cy.get('.todo-list-title');
  }

  getTodoListItems() {
    return cy.get('todo-nav-list .todo-list-item');
  }

  selectStatus(value: 'complete'| 'incomplete') {
    cy.get('[data-test=userStatusSelect]').click();
    return cy.get(`mat-option[value="${value}"]`).click();
  }
}
