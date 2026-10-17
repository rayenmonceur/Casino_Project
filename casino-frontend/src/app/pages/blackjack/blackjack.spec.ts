import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Blackjack } from './blackjack';

describe('Blackjack', () => {
  let component: Blackjack;
  let fixture: ComponentFixture<Blackjack>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Blackjack],
    }).compileComponents();

    fixture = TestBed.createComponent(Blackjack);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
