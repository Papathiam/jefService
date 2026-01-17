import { ITechnicien } from 'app/entities/technicien/technicien.model';

export interface IServiceJef {
  id: number;
  nom?: string | null;
  description?: string | null;
  actif?: boolean | null;
  techniciens?: Pick<ITechnicien, 'id'>[] | null;
}

export type NewServiceJef = Omit<IServiceJef, 'id'> & { id: null };
