import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMission } from '../mission.model';
import { MissionService } from '../service/mission.service';

@Component({
  templateUrl: './mission-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MissionDeleteDialogComponent {
  mission?: IMission;

  protected missionService = inject(MissionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.missionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
