import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IMission } from '../mission.model';

@Component({
  selector: 'jhi-mission-detail',
  templateUrl: './mission-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MissionDetailComponent {
  mission = input<IMission | null>(null);

  previousState(): void {
    window.history.back();
  }
}
