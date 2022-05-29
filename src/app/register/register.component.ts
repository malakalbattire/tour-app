import { Component, OnInit } from '@angular/core';
import { RegisterService } from './register.service';
import { User } from './user';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
user:User =new User();
  constructor(private registerService:RegisterService) { }

  ngOnInit(): void {
  }

  userRegister(){
    console.log(this.user);
    this.registerService.registeruser(this.user).subscribe (data=>{
      alert("successfully user is register")
    },error=>alert("sory user not regester"));
  }
}
