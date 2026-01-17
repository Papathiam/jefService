import { ITechnicien, NewTechnicien } from './technicien.model';

export const sampleWithRequiredData: ITechnicien = {
  id: 31649,
  nom: 'de peur que concernant',
  prenom: 'quand multiplier infiniment',
  tarifMin: 346.09,
  tarifMax: 8784.31,
};

export const sampleWithPartialData: ITechnicien = {
  id: 4632,
  nom: 'moins ah',
  prenom: 'porte-parole tchou tchouu avant que',
  tarifMin: 28865.44,
  tarifMax: 18011.65,
};

export const sampleWithFullData: ITechnicien = {
  id: 27789,
  nom: 'fourbe',
  prenom: 'vétuste efficace par rapport à',
  tarifMin: 4353.72,
  tarifMax: 744.97,
  latitude: 11041.96,
  longitude: 2488.62,
  disponible: true,
  statutValidation: 'VALIDE',
  noteMoyenne: 6002.7,
};

export const sampleWithNewData: NewTechnicien = {
  nom: 'miam vorace touriste',
  prenom: 'témoigner au cas où',
  tarifMin: 6716.98,
  tarifMax: 24412.49,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
