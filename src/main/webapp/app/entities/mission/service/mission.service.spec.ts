import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMission } from '../mission.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mission.test-samples';

import { MissionService, RestMission } from './mission.service';

const requireRestSample: RestMission = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
};

describe('Mission Service', () => {
  let service: MissionService;
  let httpMock: HttpTestingController;
  let expectedResult: IMission | IMission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MissionService);
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

    it('should create a Mission', () => {
      const mission = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(mission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mission', () => {
      const mission = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(mission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Mission', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mission', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Mission', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMissionToCollectionIfMissing', () => {
      it('should add a Mission to an empty array', () => {
        const mission: IMission = sampleWithRequiredData;
        expectedResult = service.addMissionToCollectionIfMissing([], mission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mission);
      });

      it('should not add a Mission to an array that contains it', () => {
        const mission: IMission = sampleWithRequiredData;
        const missionCollection: IMission[] = [
          {
            ...mission,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, mission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mission to an array that doesn't contain it", () => {
        const mission: IMission = sampleWithRequiredData;
        const missionCollection: IMission[] = [sampleWithPartialData];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, mission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mission);
      });

      it('should add only unique Mission to an array', () => {
        const missionArray: IMission[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const missionCollection: IMission[] = [sampleWithRequiredData];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, ...missionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mission: IMission = sampleWithRequiredData;
        const mission2: IMission = sampleWithPartialData;
        expectedResult = service.addMissionToCollectionIfMissing([], mission, mission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mission);
        expect(expectedResult).toContain(mission2);
      });

      it('should accept null and undefined values', () => {
        const mission: IMission = sampleWithRequiredData;
        expectedResult = service.addMissionToCollectionIfMissing([], null, mission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mission);
      });

      it('should return initial array if no Mission is added', () => {
        const missionCollection: IMission[] = [sampleWithRequiredData];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, undefined, null);
        expect(expectedResult).toEqual(missionCollection);
      });
    });

    describe('compareMission', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMission(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27989 };
        const entity2 = null;

        const compareResult1 = service.compareMission(entity1, entity2);
        const compareResult2 = service.compareMission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27989 };
        const entity2 = { id: 25543 };

        const compareResult1 = service.compareMission(entity1, entity2);
        const compareResult2 = service.compareMission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27989 };
        const entity2 = { id: 27989 };

        const compareResult1 = service.compareMission(entity1, entity2);
        const compareResult2 = service.compareMission(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
