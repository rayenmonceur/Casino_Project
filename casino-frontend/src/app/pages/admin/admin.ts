import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { AdminService, SecEvent, Stats, Tx } from '../../core/admin';

@Component({
  selector: 'app-admin',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './admin.html',
  styleUrl: './admin.scss',
})
export class Admin {
  private api = inject(AdminService);

  stats = signal<Stats | null>(null);
  txs = signal<Tx[]>([]);
  events = signal<SecEvent[]>([]);
  tab = signal<'tx' | 'sec'>('tx');
  error = signal('');

  constructor() {
    this.load();
  }

  load() {
    this.error.set('');
    this.api.stats().subscribe({ next: (s) => this.stats.set(s), error: () => this.fail() });
    this.api.transactions().subscribe({ next: (t) => this.txs.set(t), error: () => this.fail() });
    this.api.events().subscribe({ next: (e) => this.events.set(e), error: () => this.fail() });
  }

  private fail() {
    this.error.set('Impossible de charger les données admin');
  }
}