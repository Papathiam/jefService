import { IUser } from 'app/entities/user/user.model';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { StatutValidation } from 'app/entities/enumerations/statut-validation.model';

export interface ITechnicien {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  tarifMin?: number | null;
  tarifMax?: number | null;
  latitude?: number | null;
  longitude?: number | null;
  disponible?: boolean | null;
  statutValidation?: keyof typeof StatutValidation | null;
  noteMoyenne?: number | null;
  user?: Pick<IUser, 'id'> | null;
  serviceJefs?: Pick<IServiceJef, 'id'>[] | null;
}

export type NewTechnicien = Omit<ITechnicien, 'id'> & { id: null };
