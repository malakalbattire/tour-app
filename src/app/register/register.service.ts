import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from './user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
baseUrl="http://localhost:8080/api/admin/users";
  constructor(private httpclient: HttpClient ) { }

  registeruser(user: User): Observable<object>{
console.log(user);
return this.httpclient.post(`${this.baseUrl}`,user);
  }
}
