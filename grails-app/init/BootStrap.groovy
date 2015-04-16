
import com.summitbid.coachzilla.users.User
import com.summitbid.coachzilla.nutrition.Food
import com.summitbid.coachzilla.fitness.Exercise

class BootStrap {

    def init = { servletContext ->
		createUsers()
		createFoods()
		createExercise()
    }
	
    def destroy = {
    }
	
	def createFoods() {
		def object
		def idx = 0
		5.times {
			object = new Food(name:"TestFood-${idx}")
			object.validateAndSave()
			idx++
		}
	}
	
	def createExercise() {
		def object
		def idx = 0
		5.times {
			object = new Exercise(name:"TestExercise-${idx}")
			object.validateAndSave()
			idx++
		}
	}
	
	def createUsers(){
		def object
		def idx = 0
		5.times {
			object = new User(name:"TestUser-${idx}")
			object.validateAndSave()
			idx++
		}
		
		def objects = User.findAll()
		objects.each { newUser->  println newUser.name }
	}
}
