import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPaiement } from 'app/entities/paiement/paiement.model';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { ServiceJefService } from 'app/entities/service-jef/service/service-jef.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';
import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { IMission } from '../mission.model';
import { MissionService } from '../service/mission.service';
import { MissionFormService } from './mission-form.service';

import { MissionUpdateComponent } from './mission-update.component';

describe('Mission Management Update Component', () => {
  let comp: MissionUpdateComponent;
  let fixture: ComponentFixture<MissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let missionFormService: MissionFormService;
  let missionService: MissionService;
  let paiementService: PaiementService;
  let serviceJefService: ServiceJefService;
  let clientService: ClientService;
  let technicienService: TechnicienService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MissionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    missionFormService = TestBed.inject(MissionFormService);
    missionService = TestBed.inject(MissionService);
    paiementService = TestBed.inject(PaiementService);
    serviceJefService = TestBed.inject(ServiceJefService);
    clientService = TestBed.inject(ClientService);
    technicienService = TestBed.inject(TechnicienService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call paiement query and add missing value', () => {
      const mission: IMission = { id: 25543 };
      const paiement: IPaiement = { id: 25893 };
      mission.paiement = paiement;

      const paiementCollection: IPaiement[] = [{ id: 25893 }];
      jest.spyOn(paiementService, 'query').mockReturnValue(of(new HttpResponse({ body: paiementCollection })));
      const expectedCollection: IPaiement[] = [paiement, ...paiementCollection];
      jest.spyOn(paiementService, 'addPaiementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(paiementService.query).toHaveBeenCalled();
      expect(paiementService.addPaiementToCollectionIfMissing).toHaveBeenCalledWith(paiementCollection, paiement);
      expect(comp.paiementsCollection).toEqual(expectedCollection);
    });

    it('should call ServiceJef query and add missing value', () => {
      const mission: IMission = { id: 25543 };
      const serviceJef: IServiceJef = { id: 18411 };
      mission.serviceJef = serviceJef;

      const serviceJefCollection: IServiceJef[] = [{ id: 18411 }];
      jest.spyOn(serviceJefService, 'query').mockReturnValue(of(new HttpResponse({ body: serviceJefCollection })));
      const additionalServiceJefs = [serviceJef];
      const expectedCollection: IServiceJef[] = [...additionalServiceJefs, ...serviceJefCollection];
      jest.spyOn(serviceJefService, 'addServiceJefToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(serviceJefService.query).toHaveBeenCalled();
      expect(serviceJefService.addServiceJefToCollectionIfMissing).toHaveBeenCalledWith(
        serviceJefCollection,
        ...additionalServiceJefs.map(expect.objectContaining),
      );
      expect(comp.serviceJefsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Client query and add missing value', () => {
      const mission: IMission = { id: 25543 };
      const client: IClient = { id: 26282 };
      mission.client = client;

      const clientCollection: IClient[] = [{ id: 26282 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining),
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Technicien query and add missing value', () => {
      const mission: IMission = { id: 25543 };
      const technicien: ITechnicien = { id: 12055 };
      mission.technicien = technicien;

      const technicienCollection: ITechnicien[] = [{ id: 12055 }];
      jest.spyOn(technicienService, 'query').mockReturnValue(of(new HttpResponse({ body: technicienCollection })));
      const additionalTechniciens = [technicien];
      const expectedCollection: ITechnicien[] = [...additionalTechniciens, ...technicienCollection];
      jest.spyOn(technicienService, 'addTechnicienToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(technicienService.query).toHaveBeenCalled();
      expect(technicienService.addTechnicienToCollectionIfMissing).toHaveBeenCalledWith(
        technicienCollection,
        ...additionalTechniciens.map(expect.objectContaining),
      );
      expect(comp.techniciensSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const mission: IMission = { id: 25543 };
      const paiement: IPaiement = { id: 25893 };
      mission.paiement = paiement;
      const serviceJef: IServiceJef = { id: 18411 };
      mission.serviceJef = serviceJef;
      const client: IClient = { id: 26282 };
      mission.client = client;
      const technicien: ITechnicien = { id: 12055 };
      mission.technicien = technicien;

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(comp.paiementsCollection).toContainEqual(paiement);
      expect(comp.serviceJefsSharedCollection).toContainEqual(serviceJef);
      expect(comp.clientsSharedCollection).toContainEqual(client);
      expect(comp.techniciensSharedCollection).toContainEqual(technicien);
      expect(comp.mission).toEqual(mission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionFormService, 'getMission').mockReturnValue(mission);
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(missionFormService.getMission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(missionService.update).toHaveBeenCalledWith(expect.objectContaining(mission));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionFormService, 'getMission').mockReturnValue({ id: null });
      jest.spyOn(missionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(missionFormService.getMission).toHaveBeenCalled();
      expect(missionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMission>>();
      const mission = { id: 27989 };
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(missionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePaiement', () => {
      it('should forward to paiementService', () => {
        const entity = { id: 25893 };
        const entity2 = { id: 17769 };
        jest.spyOn(paiementService, 'comparePaiement');
        comp.comparePaiement(entity, entity2);
        expect(paiementService.comparePaiement).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareServiceJef', () => {
      it('should forward to serviceJefService', () => {
        const entity = { id: 18411 };
        const entity2 = { id: 16854 };
        jest.spyOn(serviceJefService, 'compareServiceJef');
        comp.compareServiceJef(entity, entity2);
        expect(serviceJefService.compareServiceJef).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClient', () => {
      it('should forward to clientService', () => {
        const entity = { id: 26282 };
        const entity2 = { id: 16836 };
        jest.spyOn(clientService, 'compareClient');
        comp.compareClient(entity, entity2);
        expect(clientService.compareClient).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTechnicien', () => {
      it('should forward to technicienService', () => {
        const entity = { id: 12055 };
        const entity2 = { id: 22318 };
        jest.spyOn(technicienService, 'compareTechnicien');
        comp.compareTechnicien(entity, entity2);
        expect(technicienService.compareTechnicien).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
