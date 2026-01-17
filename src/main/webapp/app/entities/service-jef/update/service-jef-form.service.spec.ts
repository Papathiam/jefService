import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../service-jef.test-samples';

import { ServiceJefFormService } from './service-jef-form.service';

describe('ServiceJef Form Service', () => {
  let service: ServiceJefFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceJefFormService);
  });

  describe('Service methods', () => {
    describe('createServiceJefFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createServiceJefFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
            techniciens: expect.any(Object),
          }),
        );
      });

      it('passing IServiceJef should create a new form with FormGroup', () => {
        const formGroup = service.createServiceJefFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
            techniciens: expect.any(Object),
          }),
        );
      });
    });

    describe('getServiceJef', () => {
      it('should return NewServiceJef for default ServiceJef initial value', () => {
        const formGroup = service.createServiceJefFormGroup(sampleWithNewData);

        const serviceJef = service.getServiceJef(formGroup) as any;

        expect(serviceJef).toMatchObject(sampleWithNewData);
      });

      it('should return NewServiceJef for empty ServiceJef initial value', () => {
        const formGroup = service.createServiceJefFormGroup();

        const serviceJef = service.getServiceJef(formGroup) as any;

        expect(serviceJef).toMatchObject({});
      });

      it('should return IServiceJef', () => {
        const formGroup = service.createServiceJefFormGroup(sampleWithRequiredData);

        const serviceJef = service.getServiceJef(formGroup) as any;

        expect(serviceJef).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IServiceJef should not enable id FormControl', () => {
        const formGroup = service.createServiceJefFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewServiceJef should disable id FormControl', () => {
        const formGroup = service.createServiceJefFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
