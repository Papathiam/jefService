import { Component, inject, input } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ITechnicien } from '../technicien.model';

@Component({
  selector: 'jhi-technicien-detail',
  templateUrl: './technicien-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TechnicienDetailComponent {
  technicien = input<ITechnicien | null>(null);
  private readonly sanitizer = inject(DomSanitizer);

  previousState(): void {
    window.history.back();
  }

  getMapEmbedUrl(latitude: number | null | undefined, longitude: number | null | undefined): SafeResourceUrl | null {
    if (latitude == null || longitude == null) return null;

    const delta = 0.01;
    const left = longitude - delta;
    const right = longitude + delta;
    const top = latitude + delta;
    const bottom = latitude - delta;

    const url =
      `https://www.openstreetmap.org/export/embed.html` +
      `?bbox=${left}%2C${bottom}%2C${right}%2C${top}` +
      `&layer=mapnik` +
      `&marker=${latitude}%2C${longitude}`;

    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  getMapExternalUrl(latitude: number | null | undefined, longitude: number | null | undefined): string | null {
    if (latitude == null || longitude == null) return null;
    return `https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=15/${latitude}/${longitude}`;
  }
}
