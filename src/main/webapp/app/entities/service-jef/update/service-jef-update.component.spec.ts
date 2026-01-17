import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { ServiceJefService } from '../service/service-jef.service';
import { IServiceJef } from '../service-jef.model';
import { ServiceJefFormService } from './service-jef-form.service';

import { ServiceJefUpdateComponent } from './service-jef-update.component';

describe('ServiceJef Management Update Component', () => {
  let comp: ServiceJefUpdateComponent;
  let fixture: ComponentFixture<ServiceJefUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let serviceJefFormService: ServiceJefFormService;
  let serviceJefService: ServiceJefService;
  let technicienService: TechnicienService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ServiceJefUpdateComponent],
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
      .overrideTemplate(ServiceJefUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServiceJefUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    serviceJefFormService = TestBed.inject(ServiceJefFormService);
    serviceJefService = TestBed.inject(ServiceJefService);
    technicienService = TestBed.inject(TechnicienService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Technicien query and add missing value', () => {
      const serviceJef: IServiceJef = { id: 16854 };
      const techniciens: ITechnicien[] = [{ id: 12055 }];
      serviceJef.techniciens = techniciens;

      const technicienCollection: ITechnicien[] = [{ id: 12055 }];
      jest.spyOn(technicienService, 'query').mockReturnValue(of(new HttpResponse({ body: technicienCollection })));
      const additionalTechniciens = [...techniciens];
      const expectedCollection: ITechnicien[] = [...additionalTechniciens, ...technicienCollection];
      jest.spyOn(technicienService, 'addTechnicienToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ serviceJef });
      comp.ngOnInit();

      expect(technicienService.query).toHaveBeenCalled();
      expect(technicienService.addTechnicienToCollectionIfMissing).toHaveBeenCalledWith(
        technicienCollection,
        ...additionalTechniciens.map(expect.objectContaining),
      );
      expect(comp.techniciensSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const serviceJef: IServiceJef = { id: 16854 };
      const techniciens: ITechnicien = { id: 12055 };
      serviceJef.techniciens = [techniciens];

      activatedRoute.data = of({ serviceJef });
      comp.ngOnInit();

      expect(comp.techniciensSharedCollection).toContainEqual(techniciens);
      expect(comp.serviceJef).toEqual(serviceJef);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceJef>>();
      const serviceJef = { id: 18411 };
      jest.spyOn(serviceJefFormService, 'getServiceJef').mockReturnValue(serviceJef);
      jest.spyOn(serviceJefService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceJef });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serviceJef }));
      saveSubject.complete();

      // THEN
      expect(serviceJefFormService.getServiceJef).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(serviceJefService.update).toHaveBeenCalledWith(expect.objectContaining(serviceJef));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceJef>>();
      const serviceJef = { id: 18411 };
      jest.spyOn(serviceJefFormService, 'getServiceJef').mockReturnValue({ id: null });
      jest.spyOn(serviceJefService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceJef: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serviceJef }));
      saveSubject.complete();

      // THEN
      expect(serviceJefFormService.getServiceJef).toHaveBeenCalled();
      expect(serviceJefService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceJef>>();
      const serviceJef = { id: 18411 };
      jest.spyOn(serviceJefService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceJef });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(serviceJefService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
