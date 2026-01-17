import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PaiementResolve from './route/paiement-routing-resolve.service';

const paiementRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/paiement.component').then(m => m.PaiementComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/paiement-detail.component').then(m => m.PaiementDetailComponent),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/paiement-update.component').then(m => m.PaiementUpdateComponent),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/paiement-update.component').then(m => m.PaiementUpdateComponent),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default paiementRoute;
