import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IServiceJef } from '../service-jef.model';
import { ServiceJefService } from '../service/service-jef.service';

@Component({
  templateUrl: './service-jef-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ServiceJefDeleteDialogComponent {
  serviceJef?: IServiceJef;

  protected serviceJefService = inject(ServiceJefService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.serviceJefService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
