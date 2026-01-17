import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPaiement } from '../paiement.model';

@Component({
  selector: 'jhi-paiement-detail',
  templateUrl: './paiement-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PaiementDetailComponent {
  paiement = input<IPaiement | null>(null);

  previousState(): void {
    window.history.back();
  }
}
