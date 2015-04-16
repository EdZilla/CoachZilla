package com.summitbid.coachzilla.fitness

import com.summitbid.coachzilla.BaseDomain

class Workout extends BaseDomain {
      String duration 
    static constraints = {
		duration(nullable:true)
    }
}
