import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDocument, NewDocument } from '../document.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDocument for edit and NewDocumentFormGroupInput for create.
 */
type DocumentFormGroupInput = IDocument | PartialWithRequiredKeyOf<NewDocument>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDocument | NewDocument> = Omit<T, 'dateUpload'> & {
  dateUpload?: string | null;
};

type DocumentFormRawValue = FormValueOf<IDocument>;

type NewDocumentFormRawValue = FormValueOf<NewDocument>;

type DocumentFormDefaults = Pick<NewDocument, 'id' | 'valide' | 'dateUpload'>;

type DocumentFormGroupContent = {
  id: FormControl<DocumentFormRawValue['id'] | NewDocument['id']>;
  type: FormControl<DocumentFormRawValue['type']>;
  valide: FormControl<DocumentFormRawValue['valide']>;
  dateUpload: FormControl<DocumentFormRawValue['dateUpload']>;
  technicien: FormControl<DocumentFormRawValue['technicien']>;
};

export type DocumentFormGroup = FormGroup<DocumentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DocumentFormService {
  createDocumentFormGroup(document: DocumentFormGroupInput = { id: null }): DocumentFormGroup {
    const documentRawValue = this.convertDocumentToDocumentRawValue({
      ...this.getFormDefaults(),
      ...document,
    });
    return new FormGroup<DocumentFormGroupContent>({
      id: new FormControl(
        { value: documentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(documentRawValue.type, {
        validators: [Validators.required],
      }),
      valide: new FormControl(documentRawValue.valide),
      dateUpload: new FormControl(documentRawValue.dateUpload),
      technicien: new FormControl(documentRawValue.technicien),
    });
  }

  getDocument(form: DocumentFormGroup): IDocument | NewDocument {
    return this.convertDocumentRawValueToDocument(form.getRawValue() as DocumentFormRawValue | NewDocumentFormRawValue);
  }

  resetForm(form: DocumentFormGroup, document: DocumentFormGroupInput): void {
    const documentRawValue = this.convertDocumentToDocumentRawValue({ ...this.getFormDefaults(), ...document });
    form.reset(
      {
        ...documentRawValue,
        id: { value: documentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DocumentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      valide: false,
      dateUpload: currentTime,
    };
  }

  private convertDocumentRawValueToDocument(rawDocument: DocumentFormRawValue | NewDocumentFormRawValue): IDocument | NewDocument {
    return {
      ...rawDocument,
      dateUpload: dayjs(rawDocument.dateUpload, DATE_TIME_FORMAT),
    };
  }

  private convertDocumentToDocumentRawValue(
    document: IDocument | (Partial<NewDocument> & DocumentFormDefaults),
  ): DocumentFormRawValue | PartialWithRequiredKeyOf<NewDocumentFormRawValue> {
    return {
      ...document,
      dateUpload: document.dateUpload ? document.dateUpload.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
