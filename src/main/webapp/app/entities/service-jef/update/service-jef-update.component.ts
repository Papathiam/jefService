import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { IServiceJef } from '../service-jef.model';
import { ServiceJefService } from '../service/service-jef.service';
import { ServiceJefFormGroup, ServiceJefFormService } from './service-jef-form.service';

@Component({
  selector: 'jhi-service-jef-update',
  templateUrl: './service-jef-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ServiceJefUpdateComponent implements OnInit {
  isSaving = false;
  serviceJef: IServiceJef | null = null;

  techniciensSharedCollection: ITechnicien[] = [];

  protected serviceJefService = inject(ServiceJefService);
  protected serviceJefFormService = inject(ServiceJefFormService);
  protected technicienService = inject(TechnicienService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ServiceJefFormGroup = this.serviceJefFormService.createServiceJefFormGroup();

  compareTechnicien = (o1: ITechnicien | null, o2: ITechnicien | null): boolean => this.technicienService.compareTechnicien(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serviceJef }) => {
      this.serviceJef = serviceJef;
      if (serviceJef) {
        this.updateForm(serviceJef);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const serviceJef = this.serviceJefFormService.getServiceJef(this.editForm);
    if (serviceJef.id !== null) {
      this.subscribeToSaveResponse(this.serviceJefService.update(serviceJef));
    } else {
      this.subscribeToSaveResponse(this.serviceJefService.create(serviceJef));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServiceJef>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(serviceJef: IServiceJef): void {
    this.serviceJef = serviceJef;
    this.serviceJefFormService.resetForm(this.editForm, serviceJef);

    this.techniciensSharedCollection = this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(
      this.techniciensSharedCollection,
      ...(serviceJef.techniciens ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.technicienService
      .query()
      .pipe(map((res: HttpResponse<ITechnicien[]>) => res.body ?? []))
      .pipe(
        map((techniciens: ITechnicien[]) =>
          this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(techniciens, ...(this.serviceJef?.techniciens ?? [])),
        ),
      )
      .subscribe((techniciens: ITechnicien[]) => (this.techniciensSharedCollection = techniciens));
  }
}
