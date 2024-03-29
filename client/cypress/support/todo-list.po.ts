export class TodoListPage{
  navigateTo() {
    return cy.visit('/todos');
  }

  getTodoTitle() {
    return cy.get('.todo-list-title');
  }

  getTodoListItems() {
    return cy.get('.todo-nav-list .todo-list-item');
  }

  selectStatus(value: 'complete'| 'incomplete') {
    cy.get('[data-test=todoStatusSelect]').click();
    return cy.get(`mat-option[value="${value}"]`).click();
  }

  selectCategory(todoCategoryInputString: string) {
    cy.get('[data-test=todoCategoryInput]').as('input').click();
    return cy.get('@input').type(todoCategoryInputString);
  }
}
