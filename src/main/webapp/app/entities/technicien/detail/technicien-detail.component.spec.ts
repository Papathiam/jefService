import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TechnicienDetailComponent } from './technicien-detail.component';

describe('Technicien Management Detail Component', () => {
  let comp: TechnicienDetailComponent;
  let fixture: ComponentFixture<TechnicienDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TechnicienDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./technicien-detail.component').then(m => m.TechnicienDetailComponent),
              resolve: { technicien: () => of({ id: 12055 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TechnicienDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicienDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load technicien on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TechnicienDetailComponent);

      // THEN
      expect(instance.technicien()).toEqual(expect.objectContaining({ id: 12055 }));
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
