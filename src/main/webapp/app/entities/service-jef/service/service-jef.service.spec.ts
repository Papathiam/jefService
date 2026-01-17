import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IServiceJef } from '../service-jef.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../service-jef.test-samples';

import { ServiceJefService } from './service-jef.service';

const requireRestSample: IServiceJef = {
  ...sampleWithRequiredData,
};

describe('ServiceJef Service', () => {
  let service: ServiceJefService;
  let httpMock: HttpTestingController;
  let expectedResult: IServiceJef | IServiceJef[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ServiceJefService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ServiceJef', () => {
      const serviceJef = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(serviceJef).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ServiceJef', () => {
      const serviceJef = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(serviceJef).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ServiceJef', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ServiceJef', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ServiceJef', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addServiceJefToCollectionIfMissing', () => {
      it('should add a ServiceJef to an empty array', () => {
        const serviceJef: IServiceJef = sampleWithRequiredData;
        expectedResult = service.addServiceJefToCollectionIfMissing([], serviceJef);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serviceJef);
      });

      it('should not add a ServiceJef to an array that contains it', () => {
        const serviceJef: IServiceJef = sampleWithRequiredData;
        const serviceJefCollection: IServiceJef[] = [
          {
            ...serviceJef,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addServiceJefToCollectionIfMissing(serviceJefCollection, serviceJef);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ServiceJef to an array that doesn't contain it", () => {
        const serviceJef: IServiceJef = sampleWithRequiredData;
        const serviceJefCollection: IServiceJef[] = [sampleWithPartialData];
        expectedResult = service.addServiceJefToCollectionIfMissing(serviceJefCollection, serviceJef);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serviceJef);
      });

      it('should add only unique ServiceJef to an array', () => {
        const serviceJefArray: IServiceJef[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const serviceJefCollection: IServiceJef[] = [sampleWithRequiredData];
        expectedResult = service.addServiceJefToCollectionIfMissing(serviceJefCollection, ...serviceJefArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const serviceJef: IServiceJef = sampleWithRequiredData;
        const serviceJef2: IServiceJef = sampleWithPartialData;
        expectedResult = service.addServiceJefToCollectionIfMissing([], serviceJef, serviceJef2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serviceJef);
        expect(expectedResult).toContain(serviceJef2);
      });

      it('should accept null and undefined values', () => {
        const serviceJef: IServiceJef = sampleWithRequiredData;
        expectedResult = service.addServiceJefToCollectionIfMissing([], null, serviceJef, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serviceJef);
      });

      it('should return initial array if no ServiceJef is added', () => {
        const serviceJefCollection: IServiceJef[] = [sampleWithRequiredData];
        expectedResult = service.addServiceJefToCollectionIfMissing(serviceJefCollection, undefined, null);
        expect(expectedResult).toEqual(serviceJefCollection);
      });
    });

    describe('compareServiceJef', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareServiceJef(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18411 };
        const entity2 = null;

        const compareResult1 = service.compareServiceJef(entity1, entity2);
        const compareResult2 = service.compareServiceJef(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18411 };
        const entity2 = { id: 16854 };

        const compareResult1 = service.compareServiceJef(entity1, entity2);
        const compareResult2 = service.compareServiceJef(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18411 };
        const entity2 = { id: 18411 };

        const compareResult1 = service.compareServiceJef(entity1, entity2);
        const compareResult2 = service.compareServiceJef(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
