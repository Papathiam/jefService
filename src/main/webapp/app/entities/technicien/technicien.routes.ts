import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TechnicienResolve from './route/technicien-routing-resolve.service';

const technicienRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/technicien.component').then(m => m.TechnicienComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/technicien-detail.component').then(m => m.TechnicienDetailComponent),
    resolve: {
      technicien: TechnicienResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/technicien-update.component').then(m => m.TechnicienUpdateComponent),
    resolve: {
      technicien: TechnicienResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/technicien-update.component').then(m => m.TechnicienUpdateComponent),
    resolve: {
      technicien: TechnicienResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default technicienRoute;
