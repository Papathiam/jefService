import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MissionResolve from './route/mission-routing-resolve.service';

const missionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mission.component').then(m => m.MissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mission-detail.component').then(m => m.MissionDetailComponent),
    resolve: {
      mission: MissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mission-update.component').then(m => m.MissionUpdateComponent),
    resolve: {
      mission: MissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mission-update.component').then(m => m.MissionUpdateComponent),
    resolve: {
      mission: MissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default missionRoute;
