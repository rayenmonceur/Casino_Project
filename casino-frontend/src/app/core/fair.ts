import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';

export interface FairInfo { serverSeedHash: string; clientSeed: string; nonce: number; }
export interface Reveal { serverSeed: string; serverSeedHash: string; clientSeed: string; lastNonce: number; }
export interface Verified { hashMatches: boolean; values: number[]; }

@Injectable({ providedIn: 'root' })
export class FairClient {
  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/fair`;

  current() { return this.http.get<FairInfo>(`${this.api}/current`); }
  setClientSeed(clientSeed: string) { return this.http.post<FairInfo>(`${this.api}/client-seed`, { clientSeed }); }
  rotate() { return this.http.post<Reveal>(`${this.api}/rotate`, {}); }
  verify(body: { serverSeed: string; serverSeedHash: string; clientSeed: string; nonce: number; bounds: number[] }) {
    return this.http.post<Verified>(`${this.api}/verify`, body);

  }
  verifyShuffle(body: { serverSeed: string; serverSeedHash: string; clientSeed: string; nonce: number }) {
  return this.http.post<{ hashMatches: boolean; deck: string[] }>(`${this.api}/verify-shuffle`, body);
}
}