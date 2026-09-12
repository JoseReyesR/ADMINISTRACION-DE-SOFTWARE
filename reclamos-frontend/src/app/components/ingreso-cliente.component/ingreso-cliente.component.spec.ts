import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IngresoClienteComponent } from './ingreso-cliente.component';

describe('IngresoClienteComponent', () => {
  let component: IngresoClienteComponent;
  let fixture: ComponentFixture<IngresoClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IngresoClienteComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(IngresoClienteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
