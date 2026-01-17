import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITechnicien, NewTechnicien } from '../technicien.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITechnicien for edit and NewTechnicienFormGroupInput for create.
 */
type TechnicienFormGroupInput = ITechnicien | PartialWithRequiredKeyOf<NewTechnicien>;

type TechnicienFormDefaults = Pick<NewTechnicien, 'id' | 'disponible' | 'serviceJefs'>;

type TechnicienFormGroupContent = {
  id: FormControl<ITechnicien['id'] | NewTechnicien['id']>;
  nom: FormControl<ITechnicien['nom']>;
  prenom: FormControl<ITechnicien['prenom']>;
  tarifMin: FormControl<ITechnicien['tarifMin']>;
  tarifMax: FormControl<ITechnicien['tarifMax']>;
  latitude: FormControl<ITechnicien['latitude']>;
  longitude: FormControl<ITechnicien['longitude']>;
  disponible: FormControl<ITechnicien['disponible']>;
  statutValidation: FormControl<ITechnicien['statutValidation']>;
  noteMoyenne: FormControl<ITechnicien['noteMoyenne']>;
  user: FormControl<ITechnicien['user']>;
  serviceJefs: FormControl<ITechnicien['serviceJefs']>;
};

export type TechnicienFormGroup = FormGroup<TechnicienFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TechnicienFormService {
  createTechnicienFormGroup(technicien: TechnicienFormGroupInput = { id: null }): TechnicienFormGroup {
    const technicienRawValue = {
      ...this.getFormDefaults(),
      ...technicien,
    };
    return new FormGroup<TechnicienFormGroupContent>({
      id: new FormControl(
        { value: technicienRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(technicienRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(technicienRawValue.prenom, {
        validators: [Validators.required],
      }),
      tarifMin: new FormControl(technicienRawValue.tarifMin, {
        validators: [Validators.required],
      }),
      tarifMax: new FormControl(technicienRawValue.tarifMax, {
        validators: [Validators.required],
      }),
      latitude: new FormControl(technicienRawValue.latitude),
      longitude: new FormControl(technicienRawValue.longitude),
      disponible: new FormControl(technicienRawValue.disponible),
      statutValidation: new FormControl(technicienRawValue.statutValidation),
      noteMoyenne: new FormControl(technicienRawValue.noteMoyenne),
      user: new FormControl(technicienRawValue.user),
      serviceJefs: new FormControl(technicienRawValue.serviceJefs ?? []),
    });
  }

  getTechnicien(form: TechnicienFormGroup): ITechnicien | NewTechnicien {
    return form.getRawValue() as ITechnicien | NewTechnicien;
  }

  resetForm(form: TechnicienFormGroup, technicien: TechnicienFormGroupInput): void {
    const technicienRawValue = { ...this.getFormDefaults(), ...technicien };
    form.reset(
      {
        ...technicienRawValue,
        id: { value: technicienRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TechnicienFormDefaults {
    return {
      id: null,
      disponible: false,
      serviceJefs: [],
    };
  }
}
