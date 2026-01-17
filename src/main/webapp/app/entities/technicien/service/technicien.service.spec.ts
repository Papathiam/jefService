import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITechnicien } from '../technicien.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../technicien.test-samples';

import { TechnicienService } from './technicien.service';

const requireRestSample: ITechnicien = {
  ...sampleWithRequiredData,
};

describe('Technicien Service', () => {
  let service: TechnicienService;
  let httpMock: HttpTestingController;
  let expectedResult: ITechnicien | ITechnicien[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TechnicienService);
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

    it('should create a Technicien', () => {
      const technicien = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(technicien).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Technicien', () => {
      const technicien = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(technicien).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Technicien', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Technicien', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Technicien', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTechnicienToCollectionIfMissing', () => {
      it('should add a Technicien to an empty array', () => {
        const technicien: ITechnicien = sampleWithRequiredData;
        expectedResult = service.addTechnicienToCollectionIfMissing([], technicien);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technicien);
      });

      it('should not add a Technicien to an array that contains it', () => {
        const technicien: ITechnicien = sampleWithRequiredData;
        const technicienCollection: ITechnicien[] = [
          {
            ...technicien,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTechnicienToCollectionIfMissing(technicienCollection, technicien);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Technicien to an array that doesn't contain it", () => {
        const technicien: ITechnicien = sampleWithRequiredData;
        const technicienCollection: ITechnicien[] = [sampleWithPartialData];
        expectedResult = service.addTechnicienToCollectionIfMissing(technicienCollection, technicien);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technicien);
      });

      it('should add only unique Technicien to an array', () => {
        const technicienArray: ITechnicien[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const technicienCollection: ITechnicien[] = [sampleWithRequiredData];
        expectedResult = service.addTechnicienToCollectionIfMissing(technicienCollection, ...technicienArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const technicien: ITechnicien = sampleWithRequiredData;
        const technicien2: ITechnicien = sampleWithPartialData;
        expectedResult = service.addTechnicienToCollectionIfMissing([], technicien, technicien2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technicien);
        expect(expectedResult).toContain(technicien2);
      });

      it('should accept null and undefined values', () => {
        const technicien: ITechnicien = sampleWithRequiredData;
        expectedResult = service.addTechnicienToCollectionIfMissing([], null, technicien, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technicien);
      });

      it('should return initial array if no Technicien is added', () => {
        const technicienCollection: ITechnicien[] = [sampleWithRequiredData];
        expectedResult = service.addTechnicienToCollectionIfMissing(technicienCollection, undefined, null);
        expect(expectedResult).toEqual(technicienCollection);
      });
    });

    describe('compareTechnicien', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTechnicien(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12055 };
        const entity2 = null;

        const compareResult1 = service.compareTechnicien(entity1, entity2);
        const compareResult2 = service.compareTechnicien(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12055 };
        const entity2 = { id: 22318 };

        const compareResult1 = service.compareTechnicien(entity1, entity2);
        const compareResult2 = service.compareTechnicien(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12055 };
        const entity2 = { id: 12055 };

        const compareResult1 = service.compareTechnicien(entity1, entity2);
        const compareResult2 = service.compareTechnicien(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
