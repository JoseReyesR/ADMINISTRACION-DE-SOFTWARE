import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetallecasoComponent } from './detallecaso.component';

describe('DetallecasoComponent', () => {
  let component: DetallecasoComponent;
  let fixture: ComponentFixture<DetallecasoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetallecasoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DetallecasoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
