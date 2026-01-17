import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITechnicien } from '../technicien.model';
import { TechnicienService } from '../service/technicien.service';

@Component({
  templateUrl: './technicien-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TechnicienDeleteDialogComponent {
  technicien?: ITechnicien;

  protected technicienService = inject(TechnicienService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.technicienService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
