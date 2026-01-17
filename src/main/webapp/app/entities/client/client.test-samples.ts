import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 16289,
};

export const sampleWithPartialData: IClient = {
  id: 8997,
  latitude: 1127.35,
  longitude: 12862.31,
  adresse: 'au-dessus de devant expliquer',
};

export const sampleWithFullData: IClient = {
  id: 31496,
  latitude: 32615.3,
  longitude: 2635.77,
  adresse: 'vérifier cependant',
};

export const sampleWithNewData: NewClient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
