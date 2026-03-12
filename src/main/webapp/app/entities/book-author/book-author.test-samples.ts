import { IBookAuthor, NewBookAuthor } from './book-author.model';

export const sampleWithRequiredData: IBookAuthor = {
  id: 1781,
  bookIsbn: 'hence um haul',
  authorId: 21459,
};

export const sampleWithPartialData: IBookAuthor = {
  id: 22832,
  bookIsbn: 'colligate sav',
  authorId: 23377,
};

export const sampleWithFullData: IBookAuthor = {
  id: 18826,
  bookIsbn: 'shout sneaky',
  authorId: 12568,
};

export const sampleWithNewData: NewBookAuthor = {
  bookIsbn: 'not',
  authorId: 16875,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
