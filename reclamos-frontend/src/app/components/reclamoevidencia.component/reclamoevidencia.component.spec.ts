import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReclamoevidenciaComponent } from './reclamoevidencia.component';

describe('ReclamoevidenciaComponent', () => {
  let component: ReclamoevidenciaComponent;
  let fixture: ComponentFixture<ReclamoevidenciaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReclamoevidenciaComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReclamoevidenciaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
