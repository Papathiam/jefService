import { IPaiement, NewPaiement } from './paiement.model';

export const sampleWithRequiredData: IPaiement = {
  id: 5155,
  montantTotal: 31009.05,
  commission: 28510.67,
  montantTechnicien: 17009.08,
};

export const sampleWithPartialData: IPaiement = {
  id: 11912,
  montantTotal: 12773.72,
  commission: 18080.17,
  montantTechnicien: 12823.68,
  reference: 'accentuer',
};

export const sampleWithFullData: IPaiement = {
  id: 8664,
  montantTotal: 4831.51,
  commission: 23201.37,
  montantTechnicien: 2137.2,
  moyenPaiement: 'FREE',
  statut: 'INITIE',
  reference: 'du moment que pour que en guise de',
};

export const sampleWithNewData: NewPaiement = {
  montantTotal: 30623.7,
  commission: 8053.77,
  montantTechnicien: 21918.88,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
