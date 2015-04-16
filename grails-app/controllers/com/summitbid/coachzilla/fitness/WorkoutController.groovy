package com.summitbid.coachzilla.fitness

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class WorkoutController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Workout.list(params), model:[workoutCount: Workout.count()]
    }

    def show(Workout workout) {
        respond workout
    }

    def create() {
        respond new Workout(params)
    }

    @Transactional
    def save(Workout workout) {
        if (workout == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (workout.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond workout.errors, view:'create'
            return
        }

        workout.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'workout.label', default: 'Workout'), workout.id])
                redirect workout
            }
            '*' { respond workout, [status: CREATED] }
        }
    }

    def edit(Workout workout) {
        respond workout
    }

    @Transactional
    def update(Workout workout) {
        if (workout == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (workout.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond workout.errors, view:'edit'
            return
        }

        workout.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'workout.label', default: 'Workout'), workout.id])
                redirect workout
            }
            '*'{ respond workout, [status: OK] }
        }
    }

    @Transactional
    def delete(Workout workout) {

        if (workout == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        workout.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'workout.label', default: 'Workout'), workout.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'workout.label', default: 'Workout'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
