import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IServiceJef } from '../service-jef.model';
import { ServiceJefService } from '../service/service-jef.service';

const serviceJefResolve = (route: ActivatedRouteSnapshot): Observable<null | IServiceJef> => {
  const id = route.params.id;
  if (id) {
    return inject(ServiceJefService)
      .find(id)
      .pipe(
        mergeMap((serviceJef: HttpResponse<IServiceJef>) => {
          if (serviceJef.body) {
            return of(serviceJef.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default serviceJefResolve;
