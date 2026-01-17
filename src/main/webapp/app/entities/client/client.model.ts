import { IUser } from 'app/entities/user/user.model';

type MinimalUser = Pick<IUser, 'id' | 'login'> & { firstName?: string | null; lastName?: string | null };

export interface IClient {
  id: number;
  latitude?: number | null;
  longitude?: number | null;
  adresse?: string | null;
  user?: MinimalUser | null;
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
