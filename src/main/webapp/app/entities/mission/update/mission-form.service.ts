import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMission, NewMission } from '../mission.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMission for edit and NewMissionFormGroupInput for create.
 */
type MissionFormGroupInput = IMission | PartialWithRequiredKeyOf<NewMission>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMission | NewMission> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

type MissionFormRawValue = FormValueOf<IMission>;

type NewMissionFormRawValue = FormValueOf<NewMission>;

type MissionFormDefaults = Pick<NewMission, 'id' | 'dateCreation'>;

type MissionFormGroupContent = {
  id: FormControl<MissionFormRawValue['id'] | NewMission['id']>;
  description: FormControl<MissionFormRawValue['description']>;
  latitude: FormControl<MissionFormRawValue['latitude']>;
  longitude: FormControl<MissionFormRawValue['longitude']>;
  statut: FormControl<MissionFormRawValue['statut']>;
  prixPropose: FormControl<MissionFormRawValue['prixPropose']>;
  dateCreation: FormControl<MissionFormRawValue['dateCreation']>;
  paiement: FormControl<MissionFormRawValue['paiement']>;
  serviceJef: FormControl<MissionFormRawValue['serviceJef']>;
  client: FormControl<MissionFormRawValue['client']>;
  technicien: FormControl<MissionFormRawValue['technicien']>;
};

export type MissionFormGroup = FormGroup<MissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MissionFormService {
  createMissionFormGroup(mission: MissionFormGroupInput = { id: null }): MissionFormGroup {
    const missionRawValue = this.convertMissionToMissionRawValue({
      ...this.getFormDefaults(),
      ...mission,
    });
    return new FormGroup<MissionFormGroupContent>({
      id: new FormControl(
        { value: missionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(missionRawValue.description),
      latitude: new FormControl(missionRawValue.latitude),
      longitude: new FormControl(missionRawValue.longitude),
      statut: new FormControl(missionRawValue.statut),
      prixPropose: new FormControl(missionRawValue.prixPropose),
      dateCreation: new FormControl(missionRawValue.dateCreation),
      paiement: new FormControl(missionRawValue.paiement),
      serviceJef: new FormControl(missionRawValue.serviceJef),
      client: new FormControl(missionRawValue.client),
      technicien: new FormControl(missionRawValue.technicien),
    });
  }

  getMission(form: MissionFormGroup): IMission | NewMission {
    return this.convertMissionRawValueToMission(form.getRawValue() as MissionFormRawValue | NewMissionFormRawValue);
  }

  resetForm(form: MissionFormGroup, mission: MissionFormGroupInput): void {
    const missionRawValue = this.convertMissionToMissionRawValue({ ...this.getFormDefaults(), ...mission });
    form.reset(
      {
        ...missionRawValue,
        id: { value: missionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MissionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
    };
  }

  private convertMissionRawValueToMission(rawMission: MissionFormRawValue | NewMissionFormRawValue): IMission | NewMission {
    return {
      ...rawMission,
      dateCreation: dayjs(rawMission.dateCreation, DATE_TIME_FORMAT),
    };
  }

  private convertMissionToMissionRawValue(
    mission: IMission | (Partial<NewMission> & MissionFormDefaults),
  ): MissionFormRawValue | PartialWithRequiredKeyOf<NewMissionFormRawValue> {
    return {
      ...mission,
      dateCreation: mission.dateCreation ? mission.dateCreation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
