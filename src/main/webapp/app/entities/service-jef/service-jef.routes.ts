import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ServiceJefResolve from './route/service-jef-routing-resolve.service';

const serviceJefRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/service-jef.component').then(m => m.ServiceJefComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/service-jef-detail.component').then(m => m.ServiceJefDetailComponent),
    resolve: {
      serviceJef: ServiceJefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/service-jef-update.component').then(m => m.ServiceJefUpdateComponent),
    resolve: {
      serviceJef: ServiceJefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/service-jef-update.component').then(m => m.ServiceJefUpdateComponent),
    resolve: {
      serviceJef: ServiceJefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default serviceJefRoute;
