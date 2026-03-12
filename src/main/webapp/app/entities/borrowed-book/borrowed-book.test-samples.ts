import dayjs from 'dayjs/esm';

import { IBorrowedBook, NewBorrowedBook } from './borrowed-book.model';

export const sampleWithRequiredData: IBorrowedBook = {
  id: 27089,
  bookIsbn: 'excluding by',
};

export const sampleWithPartialData: IBorrowedBook = {
  id: 6307,
  bookIsbn: 'although',
};

export const sampleWithFullData: IBorrowedBook = {
  id: 29509,
  bookIsbn: 'oof',
  borrowDate: dayjs('2026-03-12'),
};

export const sampleWithNewData: NewBorrowedBook = {
  bookIsbn: 'wombat',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
