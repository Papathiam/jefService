import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { DocumentService } from '../service/document.service';
import { IDocument } from '../document.model';
import { DocumentFormService } from './document-form.service';

import { DocumentUpdateComponent } from './document-update.component';

describe('Document Management Update Component', () => {
  let comp: DocumentUpdateComponent;
  let fixture: ComponentFixture<DocumentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let documentFormService: DocumentFormService;
  let documentService: DocumentService;
  let technicienService: TechnicienService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DocumentUpdateComponent],
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
      .overrideTemplate(DocumentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocumentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    documentFormService = TestBed.inject(DocumentFormService);
    documentService = TestBed.inject(DocumentService);
    technicienService = TestBed.inject(TechnicienService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Technicien query and add missing value', () => {
      const document: IDocument = { id: 4007 };
      const technicien: ITechnicien = { id: 12055 };
      document.technicien = technicien;

      const technicienCollection: ITechnicien[] = [{ id: 12055 }];
      jest.spyOn(technicienService, 'query').mockReturnValue(of(new HttpResponse({ body: technicienCollection })));
      const additionalTechniciens = [technicien];
      const expectedCollection: ITechnicien[] = [...additionalTechniciens, ...technicienCollection];
      jest.spyOn(technicienService, 'addTechnicienToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ document });
      comp.ngOnInit();

      expect(technicienService.query).toHaveBeenCalled();
      expect(technicienService.addTechnicienToCollectionIfMissing).toHaveBeenCalledWith(
        technicienCollection,
        ...additionalTechniciens.map(expect.objectContaining),
      );
      expect(comp.techniciensSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const document: IDocument = { id: 4007 };
      const technicien: ITechnicien = { id: 12055 };
      document.technicien = technicien;

      activatedRoute.data = of({ document });
      comp.ngOnInit();

      expect(comp.techniciensSharedCollection).toContainEqual(technicien);
      expect(comp.document).toEqual(document);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 24703 };
      jest.spyOn(documentFormService, 'getDocument').mockReturnValue(document);
      jest.spyOn(documentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: document }));
      saveSubject.complete();

      // THEN
      expect(documentFormService.getDocument).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(documentService.update).toHaveBeenCalledWith(expect.objectContaining(document));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 24703 };
      jest.spyOn(documentFormService, 'getDocument').mockReturnValue({ id: null });
      jest.spyOn(documentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: document }));
      saveSubject.complete();

      // THEN
      expect(documentFormService.getDocument).toHaveBeenCalled();
      expect(documentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 24703 };
      jest.spyOn(documentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(documentService.update).toHaveBeenCalled();
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
