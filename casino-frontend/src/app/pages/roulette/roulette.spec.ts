import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Roulette } from './roulette';

describe('Roulette', () => {
  let component: Roulette;
  let fixture: ComponentFixture<Roulette>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Roulette],
    }).compileComponents();

    fixture = TestBed.createComponent(Roulette);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
