import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMission } from '../mission.model';
import { MissionService } from '../service/mission.service';

const missionResolve = (route: ActivatedRouteSnapshot): Observable<null | IMission> => {
  const id = route.params.id;
  if (id) {
    return inject(MissionService)
      .find(id)
      .pipe(
        mergeMap((mission: HttpResponse<IMission>) => {
          if (mission.body) {
            return of(mission.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default missionResolve;
