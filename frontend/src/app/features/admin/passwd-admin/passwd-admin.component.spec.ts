import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswdAdminComponent } from './passwd-admin.component';

describe('PasswdAdminComponent', () => {
  let component: PasswdAdminComponent;
  let fixture: ComponentFixture<PasswdAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswdAdminComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PasswdAdminComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
