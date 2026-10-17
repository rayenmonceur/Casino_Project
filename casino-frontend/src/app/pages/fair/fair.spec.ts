import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Fair } from './fair';

describe('Fair', () => {
  let component: Fair;
  let fixture: ComponentFixture<Fair>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Fair],
    }).compileComponents();

    fixture = TestBed.createComponent(Fair);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
