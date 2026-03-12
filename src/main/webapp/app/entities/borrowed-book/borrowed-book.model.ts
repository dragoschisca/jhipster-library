import dayjs from 'dayjs/esm';
import { IBook } from 'app/entities/book/book.model';
import { IClient } from 'app/entities/client/client.model';

export interface IBorrowedBook {
  id: number;
  bookIsbn?: string | null;
  borrowDate?: dayjs.Dayjs | null;
  book?: Pick<IBook, 'id' | 'isbn'> | null;
  client?: Pick<IClient, 'id' | 'firstName'> | null;
}

export type NewBorrowedBook = Omit<IBorrowedBook, 'id'> & { id: null };
