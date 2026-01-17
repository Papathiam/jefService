import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPaiement } from 'app/entities/paiement/paiement.model';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { ServiceJefService } from 'app/entities/service-jef/service/service-jef.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { StatutMission } from 'app/entities/enumerations/statut-mission.model';
import { MissionService } from '../service/mission.service';
import { IMission } from '../mission.model';
import { MissionFormGroup, MissionFormService } from './mission-form.service';

@Component({
  selector: 'jhi-mission-update',
  templateUrl: './mission-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MissionUpdateComponent implements OnInit {
  isSaving = false;
  mission: IMission | null = null;
  statutMissionValues = Object.keys(StatutMission);

  paiementsCollection: IPaiement[] = [];
  serviceJefsSharedCollection: IServiceJef[] = [];
  clientsSharedCollection: IClient[] = [];
  techniciensSharedCollection: ITechnicien[] = [];

  protected missionService = inject(MissionService);
  protected missionFormService = inject(MissionFormService);
  protected paiementService = inject(PaiementService);
  protected serviceJefService = inject(ServiceJefService);
  protected clientService = inject(ClientService);
  protected technicienService = inject(TechnicienService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MissionFormGroup = this.missionFormService.createMissionFormGroup();

  comparePaiement = (o1: IPaiement | null, o2: IPaiement | null): boolean => this.paiementService.comparePaiement(o1, o2);

  compareServiceJef = (o1: IServiceJef | null, o2: IServiceJef | null): boolean => this.serviceJefService.compareServiceJef(o1, o2);

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  compareTechnicien = (o1: ITechnicien | null, o2: ITechnicien | null): boolean => this.technicienService.compareTechnicien(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mission }) => {
      this.mission = mission;
      if (mission) {
        this.updateForm(mission);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mission = this.missionFormService.getMission(this.editForm);
    if (mission.id !== null) {
      this.subscribeToSaveResponse(this.missionService.update(mission));
    } else {
      this.subscribeToSaveResponse(this.missionService.create(mission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMission>>): void {
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

  protected updateForm(mission: IMission): void {
    this.mission = mission;
    this.missionFormService.resetForm(this.editForm, mission);

    this.paiementsCollection = this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(this.paiementsCollection, mission.paiement);
    this.serviceJefsSharedCollection = this.serviceJefService.addServiceJefToCollectionIfMissing<IServiceJef>(
      this.serviceJefsSharedCollection,
      mission.serviceJef,
    );
    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(this.clientsSharedCollection, mission.client);
    this.techniciensSharedCollection = this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(
      this.techniciensSharedCollection,
      mission.technicien,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.paiementService
      .query({ 'missionId.specified': 'false' })
      .pipe(map((res: HttpResponse<IPaiement[]>) => res.body ?? []))
      .pipe(
        map((paiements: IPaiement[]) =>
          this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(paiements, this.mission?.paiement),
        ),
      )
      .subscribe((paiements: IPaiement[]) => (this.paiementsCollection = paiements));

    this.serviceJefService
      .query()
      .pipe(map((res: HttpResponse<IServiceJef[]>) => res.body ?? []))
      .pipe(
        map((serviceJefs: IServiceJef[]) =>
          this.serviceJefService.addServiceJefToCollectionIfMissing<IServiceJef>(serviceJefs, this.mission?.serviceJef),
        ),
      )
      .subscribe((serviceJefs: IServiceJef[]) => (this.serviceJefsSharedCollection = serviceJefs));

    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.mission?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));

    this.technicienService
      .query()
      .pipe(map((res: HttpResponse<ITechnicien[]>) => res.body ?? []))
      .pipe(
        map((techniciens: ITechnicien[]) =>
          this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(techniciens, this.mission?.technicien),
        ),
      )
      .subscribe((techniciens: ITechnicien[]) => (this.techniciensSharedCollection = techniciens));
  }
}
