import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ServiceJefDetailComponent } from './service-jef-detail.component';

describe('ServiceJef Management Detail Component', () => {
  let comp: ServiceJefDetailComponent;
  let fixture: ComponentFixture<ServiceJefDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiceJefDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./service-jef-detail.component').then(m => m.ServiceJefDetailComponent),
              resolve: { serviceJef: () => of({ id: 18411 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ServiceJefDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceJefDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load serviceJef on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ServiceJefDetailComponent);

      // THEN
      expect(instance.serviceJef()).toEqual(expect.objectContaining({ id: 18411 }));
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
