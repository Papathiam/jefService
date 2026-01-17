import { Component, inject, input } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IClient } from '../client.model';

@Component({
  selector: 'jhi-client-detail',
  templateUrl: './client-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ClientDetailComponent {
  client = input<IClient | null>(null);
  private readonly sanitizer = inject(DomSanitizer);

  previousState(): void {
    window.history.back();
  }

  hasCoordinates(client: IClient | null | undefined): boolean {
    return client?.latitude !== null && client?.latitude !== undefined && client?.longitude !== null && client?.longitude !== undefined;
  }

  displayUser(user: IClient['user'] | null | undefined): string | null {
    if (!user) {
      return null;
    }

    const name = [user.firstName, user.lastName]
      .filter(part => !!part && part.trim().length > 0)
      .join(' ')
      .trim();

    if (name.length > 0) {
      return name;
    }

    return user.login ?? null;
  }

  getMapEmbedUrl(client: IClient | null): SafeResourceUrl | null {
    if (!this.hasCoordinates(client)) {
      return null;
    }

    const lat = client!.latitude!;
    const lng = client!.longitude!;
    const delta = 0.01;
    const left = lng - delta;
    const right = lng + delta;
    const top = lat + delta;
    const bottom = lat - delta;

    const url =
      `https://www.openstreetmap.org/export/embed.html` +
      `?bbox=${left}%2C${bottom}%2C${right}%2C${top}` +
      `&layer=mapnik` +
      `&marker=${lat}%2C${lng}`;

    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  getMapExternalUrl(client: IClient | null): string | null {
    if (!this.hasCoordinates(client)) {
      return null;
    }
    const lat = client!.latitude;
    const lng = client!.longitude;
    return `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=15/${lat}/${lng}`;
  }
}
