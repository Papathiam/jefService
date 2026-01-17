import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IServiceJef, NewServiceJef } from '../service-jef.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IServiceJef for edit and NewServiceJefFormGroupInput for create.
 */
type ServiceJefFormGroupInput = IServiceJef | PartialWithRequiredKeyOf<NewServiceJef>;

type ServiceJefFormDefaults = Pick<NewServiceJef, 'id' | 'actif' | 'techniciens'>;

type ServiceJefFormGroupContent = {
  id: FormControl<IServiceJef['id'] | NewServiceJef['id']>;
  nom: FormControl<IServiceJef['nom']>;
  description: FormControl<IServiceJef['description']>;
  actif: FormControl<IServiceJef['actif']>;
  techniciens: FormControl<IServiceJef['techniciens']>;
};

export type ServiceJefFormGroup = FormGroup<ServiceJefFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ServiceJefFormService {
  createServiceJefFormGroup(serviceJef: ServiceJefFormGroupInput = { id: null }): ServiceJefFormGroup {
    const serviceJefRawValue = {
      ...this.getFormDefaults(),
      ...serviceJef,
    };
    return new FormGroup<ServiceJefFormGroupContent>({
      id: new FormControl(
        { value: serviceJefRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(serviceJefRawValue.nom, {
        validators: [Validators.required],
      }),
      description: new FormControl(serviceJefRawValue.description),
      actif: new FormControl(serviceJefRawValue.actif),
      techniciens: new FormControl(serviceJefRawValue.techniciens ?? []),
    });
  }

  getServiceJef(form: ServiceJefFormGroup): IServiceJef | NewServiceJef {
    return form.getRawValue() as IServiceJef | NewServiceJef;
  }

  resetForm(form: ServiceJefFormGroup, serviceJef: ServiceJefFormGroupInput): void {
    const serviceJefRawValue = { ...this.getFormDefaults(), ...serviceJef };
    form.reset(
      {
        ...serviceJefRawValue,
        id: { value: serviceJefRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ServiceJefFormDefaults {
    return {
      id: null,
      actif: false,
      techniciens: [],
    };
  }
}
