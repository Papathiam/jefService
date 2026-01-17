import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { ServiceJefService } from 'app/entities/service-jef/service/service-jef.service';
import { StatutValidation } from 'app/entities/enumerations/statut-validation.model';
import { TechnicienService } from '../service/technicien.service';
import { ITechnicien } from '../technicien.model';
import { TechnicienFormGroup, TechnicienFormService } from './technicien-form.service';

@Component({
  selector: 'jhi-technicien-update',
  templateUrl: './technicien-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TechnicienUpdateComponent implements OnInit {
  isSaving = false;
  technicien: ITechnicien | null = null;
  statutValidationValues = Object.keys(StatutValidation);

  usersSharedCollection: IUser[] = [];
  serviceJefsSharedCollection: IServiceJef[] = [];

  protected technicienService = inject(TechnicienService);
  protected technicienFormService = inject(TechnicienFormService);
  protected userService = inject(UserService);
  protected serviceJefService = inject(ServiceJefService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TechnicienFormGroup = this.technicienFormService.createTechnicienFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareServiceJef = (o1: IServiceJef | null, o2: IServiceJef | null): boolean => this.serviceJefService.compareServiceJef(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ technicien }) => {
      this.technicien = technicien;
      if (technicien) {
        this.updateForm(technicien);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const technicien = this.technicienFormService.getTechnicien(this.editForm);
    if (technicien.id !== null) {
      this.subscribeToSaveResponse(this.technicienService.update(technicien));
    } else {
      this.subscribeToSaveResponse(this.technicienService.create(technicien));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITechnicien>>): void {
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

  protected updateForm(technicien: ITechnicien): void {
    this.technicien = technicien;
    this.technicienFormService.resetForm(this.editForm, technicien);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, technicien.user);
    this.serviceJefsSharedCollection = this.serviceJefService.addServiceJefToCollectionIfMissing<IServiceJef>(
      this.serviceJefsSharedCollection,
      ...(technicien.serviceJefs ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.technicien?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.serviceJefService
      .query()
      .pipe(map((res: HttpResponse<IServiceJef[]>) => res.body ?? []))
      .pipe(
        map((serviceJefs: IServiceJef[]) =>
          this.serviceJefService.addServiceJefToCollectionIfMissing<IServiceJef>(serviceJefs, ...(this.technicien?.serviceJefs ?? [])),
        ),
      )
      .subscribe((serviceJefs: IServiceJef[]) => (this.serviceJefsSharedCollection = serviceJefs));
  }
}
