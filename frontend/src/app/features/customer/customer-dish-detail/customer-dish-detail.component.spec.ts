import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerDishDetailComponent } from './customer-dish-detail.component';

describe('CustomerDishDetailComponent', () => {
  let component: CustomerDishDetailComponent;
  let fixture: ComponentFixture<CustomerDishDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerDishDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerDishDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
