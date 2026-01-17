import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITechnicien } from '../technicien.model';
import { TechnicienService } from '../service/technicien.service';

const technicienResolve = (route: ActivatedRouteSnapshot): Observable<null | ITechnicien> => {
  const id = route.params.id;
  if (id) {
    return inject(TechnicienService)
      .find(id)
      .pipe(
        mergeMap((technicien: HttpResponse<ITechnicien>) => {
          if (technicien.body) {
            return of(technicien.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default technicienResolve;
