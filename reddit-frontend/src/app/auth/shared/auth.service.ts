import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { SignupRequestPayload } from '../signup/signup-request.payload';
import { Observable } from 'rxjs';
import { LoginRequestPayload } from '../login/login.request.payloads';
import { LocalStorageService } from 'ngx-webstorage';
import { map, tap } from 'rxjs/operators';
import { LoginResponse } from '../login/login-response.payload';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  refreshTokenPayload = {
    refreshToken: this.getRefreshToken(),
    username: this.getUserName()
  }

  constructor(private httpClient: HttpClient, private localStorage: LocalStorageService, private toastr : ToastrService) { }

  signup(signupRequestPayload: SignupRequestPayload) : Observable<any>{
    return this.httpClient.post("http://127.0.0.1:8080/api/auth/signup", signupRequestPayload, { responseType : 'text'})
  }

  login(loginRequestPayload : LoginRequestPayload) : Observable<boolean>{
    return this.httpClient.post<LoginResponse>("http://127.0.0.1:8080/api/auth/login", loginRequestPayload)
.pipe(map(data => {
        this.localStorage.store('authenticationToken', data.authenticationToken);
        this.localStorage.store('username', data.username);
        this.localStorage.store('refreshToken', data.refreshToken);
        this.localStorage.store('expiredAt', data.expiresAt);
        return true;
      }))
  }

  getJwtToken(){
    return this.localStorage.retrieve("authenticationToken")
  }
  getRefreshToken() {
    return this.localStorage.retrieve('refreshToken');
  }
  getUserName() {
    return this.localStorage.retrieve('username');
  }

  refreshToken() {
    return this.httpClient.post<LoginResponse>('http://localhost:8080/api/auth/refresh/token',
      this.refreshTokenPayload)
      .pipe(tap(response => {
        this.localStorage.clear('authenticationToken');
        this.localStorage.clear('expiresAt');

        this.localStorage.store('authenticationToken',
          response.authenticationToken);
        this.localStorage.store('expiresAt', response.expiresAt);
      }));
  }
}
