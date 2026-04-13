import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkerViewComponent } from './worker-view.component';

describe('WorkerViewComponent', () => {
  let component: WorkerViewComponent;
  let fixture: ComponentFixture<WorkerViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkerViewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkerViewComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
