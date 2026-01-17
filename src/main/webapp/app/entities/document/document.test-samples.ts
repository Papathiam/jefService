import dayjs from 'dayjs/esm';

import { IDocument, NewDocument } from './document.model';

export const sampleWithRequiredData: IDocument = {
  id: 26644,
  type: 'PASSPORT',
  urlFichier: 'main-d’œuvre',
};

export const sampleWithPartialData: IDocument = {
  id: 26370,
  type: 'CNI',
  urlFichier: 'déjà',
  dateUpload: dayjs('2026-01-16T19:13'),
};

export const sampleWithFullData: IDocument = {
  id: 31599,
  type: 'CNI',
  urlFichier: 'police gigantesque triathlète',
  valide: false,
  dateUpload: dayjs('2026-01-16T15:28'),
};

export const sampleWithNewData: NewDocument = {
  type: 'PASSPORT',
  urlFichier: 'mélancolique coac coac parce que',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
