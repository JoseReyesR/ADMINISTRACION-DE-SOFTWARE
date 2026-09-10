import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReclamoconfirmacionComponent } from './reclamoconfirmacion.component';

describe('ReclamoconfirmacionComponent', () => {
  let component: ReclamoconfirmacionComponent;
  let fixture: ComponentFixture<ReclamoconfirmacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReclamoconfirmacionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReclamoconfirmacionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
