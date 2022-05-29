import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Trips } from './trips';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TripsService {
  private tripsUrl!: string;
 
 

  constructor(private http: HttpClient ) {
    this.tripsUrl='http://localhost:8080/api/trips';
   }


   public save(trips: Trips): Observable<object>{
    return this.http.post<Trips>(this.tripsUrl, trips);
  }
  public createTrips(trips: Trips) : Observable< Object> {
    return this.http.post(`${this.tripsUrl}`, trips);
  }

  


}
