import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { ServiceJefService } from 'app/entities/service-jef/service/service-jef.service';
import { ITechnicien } from '../technicien.model';
import { TechnicienService } from '../service/technicien.service';
import { TechnicienFormService } from './technicien-form.service';

import { TechnicienUpdateComponent } from './technicien-update.component';

describe('Technicien Management Update Component', () => {
  let comp: TechnicienUpdateComponent;
  let fixture: ComponentFixture<TechnicienUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let technicienFormService: TechnicienFormService;
  let technicienService: TechnicienService;
  let userService: UserService;
  let serviceJefService: ServiceJefService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TechnicienUpdateComponent],
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
      .overrideTemplate(TechnicienUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TechnicienUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    technicienFormService = TestBed.inject(TechnicienFormService);
    technicienService = TestBed.inject(TechnicienService);
    userService = TestBed.inject(UserService);
    serviceJefService = TestBed.inject(ServiceJefService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const technicien: ITechnicien = { id: 22318 };
      const user: IUser = { id: 3944 };
      technicien.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ technicien });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call ServiceJef query and add missing value', () => {
      const technicien: ITechnicien = { id: 22318 };
      const serviceJefs: IServiceJef[] = [{ id: 18411 }];
      technicien.serviceJefs = serviceJefs;

      const serviceJefCollection: IServiceJef[] = [{ id: 18411 }];
      jest.spyOn(serviceJefService, 'query').mockReturnValue(of(new HttpResponse({ body: serviceJefCollection })));
      const additionalServiceJefs = [...serviceJefs];
      const expectedCollection: IServiceJef[] = [...additionalServiceJefs, ...serviceJefCollection];
      jest.spyOn(serviceJefService, 'addServiceJefToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ technicien });
      comp.ngOnInit();

      expect(serviceJefService.query).toHaveBeenCalled();
      expect(serviceJefService.addServiceJefToCollectionIfMissing).toHaveBeenCalledWith(
        serviceJefCollection,
        ...additionalServiceJefs.map(expect.objectContaining),
      );
      expect(comp.serviceJefsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const technicien: ITechnicien = { id: 22318 };
      const user: IUser = { id: 3944 };
      technicien.user = user;
      const serviceJefs: IServiceJef = { id: 18411 };
      technicien.serviceJefs = [serviceJefs];

      activatedRoute.data = of({ technicien });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.serviceJefsSharedCollection).toContainEqual(serviceJefs);
      expect(comp.technicien).toEqual(technicien);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITechnicien>>();
      const technicien = { id: 12055 };
      jest.spyOn(technicienFormService, 'getTechnicien').mockReturnValue(technicien);
      jest.spyOn(technicienService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technicien });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: technicien }));
      saveSubject.complete();

      // THEN
      expect(technicienFormService.getTechnicien).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(technicienService.update).toHaveBeenCalledWith(expect.objectContaining(technicien));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITechnicien>>();
      const technicien = { id: 12055 };
      jest.spyOn(technicienFormService, 'getTechnicien').mockReturnValue({ id: null });
      jest.spyOn(technicienService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technicien: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: technicien }));
      saveSubject.complete();

      // THEN
      expect(technicienFormService.getTechnicien).toHaveBeenCalled();
      expect(technicienService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITechnicien>>();
      const technicien = { id: 12055 };
      jest.spyOn(technicienService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technicien });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(technicienService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
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
  });
});
