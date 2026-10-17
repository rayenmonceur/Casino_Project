import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Slots } from './slots';

describe('Slots', () => {
  let component: Slots;
  let fixture: ComponentFixture<Slots>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Slots],
    }).compileComponents();

    fixture = TestBed.createComponent(Slots);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
