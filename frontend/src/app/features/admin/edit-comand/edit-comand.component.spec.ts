import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditComandComponent } from './edit-comand.component';

describe('EditComandComponent', () => {
  let component: EditComandComponent;
  let fixture: ComponentFixture<EditComandComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditComandComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EditComandComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
