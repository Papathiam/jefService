import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IServiceJef } from '../service-jef.model';

@Component({
  selector: 'jhi-service-jef-detail',
  templateUrl: './service-jef-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ServiceJefDetailComponent {
  serviceJef = input<IServiceJef | null>(null);

  previousState(): void {
    window.history.back();
  }
}
