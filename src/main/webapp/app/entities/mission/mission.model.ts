import dayjs from 'dayjs/esm';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { IServiceJef } from 'app/entities/service-jef/service-jef.model';
import { IClient } from 'app/entities/client/client.model';
import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { StatutMission } from 'app/entities/enumerations/statut-mission.model';

export interface IMission {
  id: number;
  description?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  statut?: keyof typeof StatutMission | null;
  prixPropose?: number | null;
  dateCreation?: dayjs.Dayjs | null;
  paiement?: IPaiement | null;
  serviceJef?: IServiceJef | null;
  client?: IClient | null;
  technicien?: ITechnicien | null;
}

export type NewMission = Omit<IMission, 'id'> & { id: null };
