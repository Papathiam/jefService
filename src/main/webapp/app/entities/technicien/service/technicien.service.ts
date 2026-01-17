import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITechnicien, NewTechnicien } from '../technicien.model';

export type PartialUpdateTechnicien = Partial<ITechnicien> & Pick<ITechnicien, 'id'>;

export type EntityResponseType = HttpResponse<ITechnicien>;
export type EntityArrayResponseType = HttpResponse<ITechnicien[]>;

@Injectable({ providedIn: 'root' })
export class TechnicienService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/techniciens');

  create(technicien: NewTechnicien): Observable<EntityResponseType> {
    return this.http.post<ITechnicien>(this.resourceUrl, technicien, { observe: 'response' });
  }

  update(technicien: ITechnicien): Observable<EntityResponseType> {
    return this.http.put<ITechnicien>(`${this.resourceUrl}/${this.getTechnicienIdentifier(technicien)}`, technicien, {
      observe: 'response',
    });
  }

  partialUpdate(technicien: PartialUpdateTechnicien): Observable<EntityResponseType> {
    return this.http.patch<ITechnicien>(`${this.resourceUrl}/${this.getTechnicienIdentifier(technicien)}`, technicien, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITechnicien>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITechnicien[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTechnicienIdentifier(technicien: Pick<ITechnicien, 'id'>): number {
    return technicien.id;
  }

  compareTechnicien(o1: Pick<ITechnicien, 'id'> | null, o2: Pick<ITechnicien, 'id'> | null): boolean {
    return o1 && o2 ? this.getTechnicienIdentifier(o1) === this.getTechnicienIdentifier(o2) : o1 === o2;
  }

  addTechnicienToCollectionIfMissing<Type extends Pick<ITechnicien, 'id'>>(
    technicienCollection: Type[],
    ...techniciensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const techniciens: Type[] = techniciensToCheck.filter(isPresent);
    if (techniciens.length > 0) {
      const technicienCollectionIdentifiers = technicienCollection.map(technicienItem => this.getTechnicienIdentifier(technicienItem));
      const techniciensToAdd = techniciens.filter(technicienItem => {
        const technicienIdentifier = this.getTechnicienIdentifier(technicienItem);
        if (technicienCollectionIdentifiers.includes(technicienIdentifier)) {
          return false;
        }
        technicienCollectionIdentifiers.push(technicienIdentifier);
        return true;
      });
      return [...techniciensToAdd, ...technicienCollection];
    }
    return technicienCollection;
  }
}
