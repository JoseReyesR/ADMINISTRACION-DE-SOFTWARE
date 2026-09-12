import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MiscasosComponent } from './miscasos.component';

describe('MiscasosComponent', () => {
  let component: MiscasosComponent;
  let fixture: ComponentFixture<MiscasosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiscasosComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MiscasosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
