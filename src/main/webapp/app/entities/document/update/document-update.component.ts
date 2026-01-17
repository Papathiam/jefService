import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TechnicienService } from 'app/entities/technicien/service/technicien.service';
import { TypeDocument } from 'app/entities/enumerations/type-document.model';
import { DocumentService } from '../service/document.service';
import { IDocument } from '../document.model';
import { DocumentFormGroup, DocumentFormService } from './document-form.service';

@Component({
  selector: 'jhi-document-update',
  templateUrl: './document-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DocumentUpdateComponent implements OnInit {
  isSaving = false;
  isValidating = false;
  document: IDocument | null = null;
  typeDocumentValues = Object.keys(TypeDocument);

  techniciensSharedCollection: ITechnicien[] = [];

  protected documentService = inject(DocumentService);
  protected documentFormService = inject(DocumentFormService);
  protected technicienService = inject(TechnicienService);
  protected activatedRoute = inject(ActivatedRoute);
  fileUrl?: string;
  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DocumentFormGroup = this.documentFormService.createDocumentFormGroup();

  compareTechnicien = (o1: ITechnicien | null, o2: ITechnicien | null): boolean => this.technicienService.compareTechnicien(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ document }) => {
      this.document = document;
      if (document) {
        this.updateForm(document);
      }

      if (document.fileData && document.contentType) {
        const blob = this.base64ToBlob(document.fileData, document.contentType);
        this.fileUrl = URL.createObjectURL(blob);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const document = this.documentFormService.getDocument(this.editForm);
    if (document.id !== null) {
      this.subscribeToSaveResponse(this.documentService.partialUpdate(document));
    } else {
      this.subscribeToSaveResponse(this.documentService.create(document));
    }
  }

  get urlFichier(): string | null {
    return this.editForm.get('urlFichier')?.value ?? null;
  }

  isImage(url: string | null): boolean {
    return !!url && /\.(png|jpe?g|gif|webp)$/i.test(url);
  }

  isPDF(url: string | null): boolean {
    return !!url && /\.pdf$/i.test(url);
  }

  base64ToBlob(base64: string, contentType: string): Blob {
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    return new Blob([byteArray], { type: contentType });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocument>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  validateCurrentDocument(): void {
    const documentId = this.document?.id;
    if (!documentId) return;

    this.isValidating = true;
    this.documentService
      .validateDocument(documentId)
      .pipe(finalize(() => (this.isValidating = false)))
      .subscribe({
        next: () => {
          this.editForm.patchValue({ valide: true });
          if (this.document?.technicien) {
            this.document.technicien.statutValidation = 'VALIDE';
          }
        },
      });
  }

  get selectedTechnicienLabel(): string {
    const technicien = this.editForm.get('technicien')?.value as ITechnicien | null;
    return this.formatTechnicienLabel(technicien);
  }

  formatTechnicienLabel(technicien: ITechnicien | null | undefined): string {
    if (!technicien) return 'Aucun technicien sélectionné';
    const fullName = [technicien.prenom, technicien.nom].filter(Boolean).join(' ').trim();
    return fullName ? `${fullName} (ID ${technicien.id})` : `Technicien #${technicien.id}`;
  }

  protected updateForm(document: IDocument): void {
    this.document = document;
    this.documentFormService.resetForm(this.editForm, document);

    this.techniciensSharedCollection = this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(
      this.techniciensSharedCollection,
      document.technicien,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.technicienService
      .query()
      .pipe(map((res: HttpResponse<ITechnicien[]>) => res.body ?? []))
      .pipe(
        map((techniciens: ITechnicien[]) =>
          this.technicienService.addTechnicienToCollectionIfMissing<ITechnicien>(techniciens, this.document?.technicien),
        ),
      )
      .subscribe((techniciens: ITechnicien[]) => (this.techniciensSharedCollection = techniciens));
  }
}
