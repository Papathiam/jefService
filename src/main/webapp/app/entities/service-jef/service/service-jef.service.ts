import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IServiceJef, NewServiceJef } from '../service-jef.model';

export type PartialUpdateServiceJef = Partial<IServiceJef> & Pick<IServiceJef, 'id'>;

export type EntityResponseType = HttpResponse<IServiceJef>;
export type EntityArrayResponseType = HttpResponse<IServiceJef[]>;

@Injectable({ providedIn: 'root' })
export class ServiceJefService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/service-jefs');

  create(serviceJef: NewServiceJef): Observable<EntityResponseType> {
    return this.http.post<IServiceJef>(this.resourceUrl, serviceJef, { observe: 'response' });
  }

  update(serviceJef: IServiceJef): Observable<EntityResponseType> {
    return this.http.put<IServiceJef>(`${this.resourceUrl}/${this.getServiceJefIdentifier(serviceJef)}`, serviceJef, {
      observe: 'response',
    });
  }

  partialUpdate(serviceJef: PartialUpdateServiceJef): Observable<EntityResponseType> {
    return this.http.patch<IServiceJef>(`${this.resourceUrl}/${this.getServiceJefIdentifier(serviceJef)}`, serviceJef, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IServiceJef>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IServiceJef[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getServiceJefIdentifier(serviceJef: Pick<IServiceJef, 'id'>): number {
    return serviceJef.id;
  }

  compareServiceJef(o1: Pick<IServiceJef, 'id'> | null, o2: Pick<IServiceJef, 'id'> | null): boolean {
    return o1 && o2 ? this.getServiceJefIdentifier(o1) === this.getServiceJefIdentifier(o2) : o1 === o2;
  }

  addServiceJefToCollectionIfMissing<Type extends Pick<IServiceJef, 'id'>>(
    serviceJefCollection: Type[],
    ...serviceJefsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const serviceJefs: Type[] = serviceJefsToCheck.filter(isPresent);
    if (serviceJefs.length > 0) {
      const serviceJefCollectionIdentifiers = serviceJefCollection.map(serviceJefItem => this.getServiceJefIdentifier(serviceJefItem));
      const serviceJefsToAdd = serviceJefs.filter(serviceJefItem => {
        const serviceJefIdentifier = this.getServiceJefIdentifier(serviceJefItem);
        if (serviceJefCollectionIdentifiers.includes(serviceJefIdentifier)) {
          return false;
        }
        serviceJefCollectionIdentifiers.push(serviceJefIdentifier);
        return true;
      });
      return [...serviceJefsToAdd, ...serviceJefCollection];
    }
    return serviceJefCollection;
  }
}
