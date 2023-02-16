export interface Todo {
  _id: string;
  owner: string;
  status: boolean;
  body: string;
  category: TodoCategory;
};

export type TodoCategory = 'homework' | 'groceries' | 'software design' | 'video games' ;

