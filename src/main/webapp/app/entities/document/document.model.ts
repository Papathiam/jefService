import dayjs from 'dayjs/esm';
import { ITechnicien } from 'app/entities/technicien/technicien.model';
import { TypeDocument } from 'app/entities/enumerations/type-document.model';

export interface IDocument {
  id: number;
  type?: keyof typeof TypeDocument | null;
  valide?: boolean | null;
  dateUpload?: dayjs.Dayjs | null;
  technicien?: ITechnicien | null;
  fileData?: string | null;
  contentType?: string | null;
  fileName?: string | null;
}

export type NewDocument = Omit<IDocument, 'id'> & { id: null };
