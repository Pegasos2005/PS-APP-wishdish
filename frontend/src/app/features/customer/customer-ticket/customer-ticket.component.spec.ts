import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerTicketComponent } from './customer-ticket.component';

describe('CustomerTicketComponent', () => {
  let component: CustomerTicketComponent;
  let fixture: ComponentFixture<CustomerTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerTicketComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerTicketComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
