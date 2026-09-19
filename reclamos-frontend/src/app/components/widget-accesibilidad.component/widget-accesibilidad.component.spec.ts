import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WidgetAccesibilidadComponent } from './widget-accesibilidad.component';

describe('WidgetAccesibilidadComponent', () => {
  let component: WidgetAccesibilidadComponent;
  let fixture: ComponentFixture<WidgetAccesibilidadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WidgetAccesibilidadComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WidgetAccesibilidadComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
