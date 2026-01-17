import dayjs from 'dayjs/esm';

import { IMission, NewMission } from './mission.model';

export const sampleWithRequiredData: IMission = {
  id: 12380,
};

export const sampleWithPartialData: IMission = {
  id: 30965,
  latitude: 16058.9,
  prixPropose: 2014.76,
};

export const sampleWithFullData: IMission = {
  id: 17181,
  description: 'chef ensemble',
  latitude: 21608.8,
  longitude: 17821.51,
  statut: 'EN_ATTENTE',
  prixPropose: 32069.44,
  dateCreation: dayjs('2026-01-17T14:21'),
};

export const sampleWithNewData: NewMission = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
