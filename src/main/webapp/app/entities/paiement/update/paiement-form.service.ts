import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPaiement, NewPaiement } from '../paiement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaiement for edit and NewPaiementFormGroupInput for create.
 */
type PaiementFormGroupInput = IPaiement | PartialWithRequiredKeyOf<NewPaiement>;

type PaiementFormDefaults = Pick<NewPaiement, 'id'>;

type PaiementFormGroupContent = {
  id: FormControl<IPaiement['id'] | NewPaiement['id']>;
  montantTotal: FormControl<IPaiement['montantTotal']>;
  commission: FormControl<IPaiement['commission']>;
  montantTechnicien: FormControl<IPaiement['montantTechnicien']>;
  moyenPaiement: FormControl<IPaiement['moyenPaiement']>;
  statut: FormControl<IPaiement['statut']>;
  reference: FormControl<IPaiement['reference']>;
};

export type PaiementFormGroup = FormGroup<PaiementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaiementFormService {
  createPaiementFormGroup(paiement: PaiementFormGroupInput = { id: null }): PaiementFormGroup {
    const paiementRawValue = {
      ...this.getFormDefaults(),
      ...paiement,
    };
    return new FormGroup<PaiementFormGroupContent>({
      id: new FormControl(
        { value: paiementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      montantTotal: new FormControl(paiementRawValue.montantTotal, {
        validators: [Validators.required],
      }),
      commission: new FormControl(paiementRawValue.commission, {
        validators: [Validators.required],
      }),
      montantTechnicien: new FormControl(paiementRawValue.montantTechnicien, {
        validators: [Validators.required],
      }),
      moyenPaiement: new FormControl(paiementRawValue.moyenPaiement),
      statut: new FormControl(paiementRawValue.statut),
      reference: new FormControl(paiementRawValue.reference),
    });
  }

  getPaiement(form: PaiementFormGroup): IPaiement | NewPaiement {
    return form.getRawValue() as IPaiement | NewPaiement;
  }

  resetForm(form: PaiementFormGroup, paiement: PaiementFormGroupInput): void {
    const paiementRawValue = { ...this.getFormDefaults(), ...paiement };
    form.reset(
      {
        ...paiementRawValue,
        id: { value: paiementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PaiementFormDefaults {
    return {
      id: null,
    };
  }
}
