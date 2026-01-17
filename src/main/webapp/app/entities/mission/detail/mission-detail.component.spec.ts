import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MissionDetailComponent } from './mission-detail.component';

describe('Mission Management Detail Component', () => {
  let comp: MissionDetailComponent;
  let fixture: ComponentFixture<MissionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mission-detail.component').then(m => m.MissionDetailComponent),
              resolve: { mission: () => of({ id: 27989 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MissionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MissionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load mission on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MissionDetailComponent);

      // THEN
      expect(instance.mission()).toEqual(expect.objectContaining({ id: 27989 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
