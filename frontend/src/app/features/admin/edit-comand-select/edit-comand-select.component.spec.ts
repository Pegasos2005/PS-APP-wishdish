import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditComandSelectComponent } from './edit-comand-select.component';

describe('EditComandSelectComponent', () => {
  let component: EditComandSelectComponent;
  let fixture: ComponentFixture<EditComandSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditComandSelectComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EditComandSelectComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
