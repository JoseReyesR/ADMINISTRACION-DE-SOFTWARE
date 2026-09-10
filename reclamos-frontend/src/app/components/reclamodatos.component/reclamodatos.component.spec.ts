import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReclamodatosComponent } from './reclamodatos.component';

describe('ReclamodatosComponent', () => {
  let component: ReclamodatosComponent;
  let fixture: ComponentFixture<ReclamodatosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReclamodatosComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReclamodatosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
