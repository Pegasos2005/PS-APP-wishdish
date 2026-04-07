import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VistaCamarero } from './vista-camarero';

describe('VistaCamarero', () => {
  let component: VistaCamarero;
  let fixture: ComponentFixture<VistaCamarero>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaCamarero],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaCamarero);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
