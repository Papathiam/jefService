import { IServiceJef, NewServiceJef } from './service-jef.model';

export const sampleWithRequiredData: IServiceJef = {
  id: 18875,
  nom: 'sous couleur de',
};

export const sampleWithPartialData: IServiceJef = {
  id: 22419,
  nom: 'prou même au point que',
  description: 'sitôt',
  actif: true,
};

export const sampleWithFullData: IServiceJef = {
  id: 26274,
  nom: 'orange',
  description: 'lasser redescendre en vérité',
  actif: true,
};

export const sampleWithNewData: NewServiceJef = {
  nom: 'alors que vite aux environs de',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
