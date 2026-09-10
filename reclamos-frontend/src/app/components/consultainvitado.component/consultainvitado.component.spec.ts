import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConsultainvitadoComponent } from './consultainvitado.component';

describe('ConsultainvitadoComponent', () => {
  let component: ConsultainvitadoComponent;
  let fixture: ComponentFixture<ConsultainvitadoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultainvitadoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultainvitadoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
