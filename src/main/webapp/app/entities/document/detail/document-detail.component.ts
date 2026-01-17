import { HttpResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDocument } from '../document.model';
import { DocumentService } from '../service/document.service';

@Component({
  selector: 'jhi-document-detail',
  templateUrl: './document-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class DocumentDetailComponent {
  document = input<IDocument | null>(null);
  downloading = signal(false);
  private readonly documentService = inject(DocumentService);

  previousState(): void {
    window.history.back();
  }

  preview(): void {
    this.openDocument(true);
  }

  download(): void {
    this.openDocument(false);
  }

  private openDocument(preview: boolean): void {
    const documentRef = this.document();
    if (!documentRef?.id) return;

    this.downloading.set(true);
    this.documentService.downloadFile(documentRef.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) {
          this.downloading.set(false);
          return;
        }

        const fileName = this.extractFilename(response, documentRef.fileName ?? `document-${documentRef.id}`);
        const url = URL.createObjectURL(blob);

        if (preview && this.canPreview(blob.type, fileName)) {
          window.open(url, '_blank', 'noopener,noreferrer');
          setTimeout(() => URL.revokeObjectURL(url), 60_000);
        } else {
          const link = window.document.createElement('a');
          link.href = url;
          link.download = fileName;
          link.click();
          URL.revokeObjectURL(url);
        }

        this.downloading.set(false);
      },
      error: () => this.downloading.set(false),
    });
  }

  private extractFilename(response: HttpResponse<Blob>, fallback: string): string {
    const contentDisposition = response.headers.get('content-disposition') ?? '';
    const utfNameMatch = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i);
    if (utfNameMatch?.[1]) {
      return decodeURIComponent(utfNameMatch[1]);
    }

    const nameMatch = contentDisposition.match(/filename=\"?([^\";]+)\"?/i);
    return nameMatch?.[1] ?? fallback;
  }

  private canPreview(contentType: string, fileName: string): boolean {
    if (contentType.startsWith('image/') || contentType === 'application/pdf') {
      return true;
    }
    return /\.(pdf|png|jpe?g|gif|webp)$/i.test(fileName);
  }
}
