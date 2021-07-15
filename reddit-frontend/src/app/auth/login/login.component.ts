import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../shared/auth.service';
import { LoginRequestPayload } from './login.request.payloads';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginRequestPayload : LoginRequestPayload
  loginForm : FormGroup;
  registerSuccessMessage: String
  isError: boolean

  constructor(private authService: AuthService, private activatedRoute : ActivatedRoute, private router: Router, private toastr: ToastrService) {
    this.loginRequestPayload = {
      username: "",
      password: ""
    }
   }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    })

    this.activatedRoute.queryParams.subscribe(params => {
      if(params.registered !== undefined && params.registered === true){
        this.toastr.success('signup suceess')
        this.registerSuccessMessage = "remember to activate your account"
      }
    })
  }

  login(){
    this.loginRequestPayload.username = this.loginForm.get('username')?.value
    this.loginRequestPayload.password = this.loginForm.get('password')?.value
    this.authService.login(this.loginRequestPayload).subscribe(data => {
      if(data) {
        this.isError = false
        this.router.navigateByUrl('/')
        this.toastr.success('login success')
      }else{
        this.isError = true
        
      }
    })
  }

}
