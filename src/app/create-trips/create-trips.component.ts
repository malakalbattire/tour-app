import { Component, OnInit } from '@angular/core';
import { Trips } from '../trips/trips';
import { TripsService } from '../trips/trips.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-create-trips',
  templateUrl: './create-trips.component.html',
  styleUrls: ['./create-trips.component.css']
})
export class CreateTripsComponent implements OnInit {
trips: Trips= new Trips;

  constructor(
    private tripsService: TripsService,
    private router: ActivatedRoute,
    private route1: Router,
  ) { }


  ngOnInit():void {}
  
  onSubmit(){

    console.log(this.trips);
    this.saveTrips();

  }
 

  goToTripsList(){
    this.route1.navigate(['/trips']);
    
      }
      saveTrips(){
        this.tripsService.createTrips(this.trips).subscribe(data =>{
          console.log('response',data);
          this.goToTripsList();
        },
        error => console.log(error)
        );
      }
      createTrips(){
        this.tripsService.createTrips(this.trips).subscribe(data =>{
          console.log('response',data);
        },
        error => console.log(error)
        );
      }

}


