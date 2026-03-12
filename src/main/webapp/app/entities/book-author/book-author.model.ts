import { IBook } from 'app/entities/book/book.model';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBookAuthor {
  id: number;
  bookIsbn?: string | null;
  authorId?: number | null;
  book?: Pick<IBook, 'id' | 'isbn'> | null;
  author?: Pick<IAuthor, 'id' | 'firstName'> | null;
}

export type NewBookAuthor = Omit<IBookAuthor, 'id'> & { id: null };
