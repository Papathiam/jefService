import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { IDocument } from '../document.model';
import { DocumentService, EntityArrayResponseType } from '../service/document.service';
import { DocumentDeleteDialogComponent } from '../delete/document-delete-dialog.component';

@Component({
  selector: 'jhi-document',
  templateUrl: './document.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class DocumentComponent implements OnInit {
  subscription: Subscription | null = null;
  documents = signal<IDocument[]>([]);
  isLoading = false;
  downloadingId = signal<number | null>(null);

  sortState = sortStateSignal({});

  public readonly router = inject(Router);
  protected readonly documentService = inject(DocumentService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IDocument): number => this.documentService.getDocumentIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(document: IDocument): void {
    const modalRef = this.modalService.open(DocumentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.document = document;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  preview(document: IDocument): void {
    this.openDocument(document, true);
  }

  download(document: IDocument): void {
    this.openDocument(document, false);
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.documents.set(this.refineData(dataFromBody));
  }

  protected refineData(data: IDocument[]): IDocument[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IDocument[] | null): IDocument[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.documentService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }

  private openDocument(document: IDocument, preview: boolean): void {
    this.downloadingId.set(document.id);
    this.documentService.downloadFile(document.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) {
          this.downloadingId.set(null);
          return;
        }

        const fileName = this.extractFilename(response, document.fileName ?? `document-${document.id}`);
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

        this.downloadingId.set(null);
      },
      error: () => this.downloadingId.set(null),
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
