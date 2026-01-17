import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'jefServiceApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'client',
    data: { pageTitle: 'Client' },
    loadChildren: () => import('./client/client.routes'),
  },
  {
    path: 'document',
    data: { pageTitle: 'Documents' },
    loadChildren: () => import('./document/document.routes'),
  },
  {
    path: 'paiement',
    data: { pageTitle: 'Paiement' },
    loadChildren: () => import('./paiement/paiement.routes'),
  },
  {
    path: 'mission',
    data: { pageTitle: 'Mission' },
    loadChildren: () => import('./mission/mission.routes'),
  },
  {
    path: 'technicien',
    data: { pageTitle: 'Technicien' },
    loadChildren: () => import('./technicien/technicien.routes'),
  },
  {
    path: 'service-jef',
    data: { pageTitle: 'Services' },
    loadChildren: () => import('./service-jef/service-jef.routes'),
  },

  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
