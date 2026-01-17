import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../technicien.test-samples';

import { TechnicienFormService } from './technicien-form.service';

describe('Technicien Form Service', () => {
  let service: TechnicienFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TechnicienFormService);
  });

  describe('Service methods', () => {
    describe('createTechnicienFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTechnicienFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            tarifMin: expect.any(Object),
            tarifMax: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            disponible: expect.any(Object),
            statutValidation: expect.any(Object),
            noteMoyenne: expect.any(Object),
            user: expect.any(Object),
            serviceJefs: expect.any(Object),
          }),
        );
      });

      it('passing ITechnicien should create a new form with FormGroup', () => {
        const formGroup = service.createTechnicienFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            tarifMin: expect.any(Object),
            tarifMax: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            disponible: expect.any(Object),
            statutValidation: expect.any(Object),
            noteMoyenne: expect.any(Object),
            user: expect.any(Object),
            serviceJefs: expect.any(Object),
          }),
        );
      });
    });

    describe('getTechnicien', () => {
      it('should return NewTechnicien for default Technicien initial value', () => {
        const formGroup = service.createTechnicienFormGroup(sampleWithNewData);

        const technicien = service.getTechnicien(formGroup) as any;

        expect(technicien).toMatchObject(sampleWithNewData);
      });

      it('should return NewTechnicien for empty Technicien initial value', () => {
        const formGroup = service.createTechnicienFormGroup();

        const technicien = service.getTechnicien(formGroup) as any;

        expect(technicien).toMatchObject({});
      });

      it('should return ITechnicien', () => {
        const formGroup = service.createTechnicienFormGroup(sampleWithRequiredData);

        const technicien = service.getTechnicien(formGroup) as any;

        expect(technicien).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITechnicien should not enable id FormControl', () => {
        const formGroup = service.createTechnicienFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTechnicien should disable id FormControl', () => {
        const formGroup = service.createTechnicienFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
