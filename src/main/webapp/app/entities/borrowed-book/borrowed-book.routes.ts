import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BorrowedBookResolve from './route/borrowed-book-routing-resolve.service';

const borrowedBookRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/borrowed-book.component').then(m => m.BorrowedBookComponent),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: ['ROLE_ADMIN', 'ROLE_LIBRARIAN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/borrowed-book-detail.component').then(m => m.BorrowedBookDetailComponent),
    resolve: {
      borrowedBook: BorrowedBookResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN', 'ROLE_LIBRARIAN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/borrowed-book-update.component').then(m => m.BorrowedBookUpdateComponent),
    resolve: {
      borrowedBook: BorrowedBookResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/borrowed-book-update.component').then(m => m.BorrowedBookUpdateComponent),
    resolve: {
      borrowedBook: BorrowedBookResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default borrowedBookRoute;
