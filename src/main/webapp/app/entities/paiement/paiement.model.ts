import { MoyenPaiement } from 'app/entities/enumerations/moyen-paiement.model';
import { StatutPaiement } from 'app/entities/enumerations/statut-paiement.model';

export interface IPaiement {
  id: number;
  montantTotal?: number | null;
  commission?: number | null;
  montantTechnicien?: number | null;
  moyenPaiement?: keyof typeof MoyenPaiement | null;
  statut?: keyof typeof StatutPaiement | null;
  reference?: string | null;
}

export type NewPaiement = Omit<IPaiement, 'id'> & { id: null };
