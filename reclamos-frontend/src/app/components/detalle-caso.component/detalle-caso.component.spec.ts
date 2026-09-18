import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetalleCasoComponent } from './detalle-caso.component';

describe('DetalleCasoComponent', () => {
  let component: DetalleCasoComponent;
  let fixture: ComponentFixture<DetalleCasoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleCasoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetalleCasoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
