package com.summitbid.coachzilla.fitness

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ExerciseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Exercise.list(params), model:[exerciseCount: Exercise.count()]
    }

    def show(Exercise exercise) {
        respond exercise
    }

    def create() {
        respond new Exercise(params)
    }

    @Transactional
    def save(Exercise exercise) {
        if (exercise == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (exercise.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond exercise.errors, view:'create'
            return
        }

        exercise.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'exercise.label', default: 'Exercise'), exercise.id])
                redirect exercise
            }
            '*' { respond exercise, [status: CREATED] }
        }
    }

    def edit(Exercise exercise) {
        respond exercise
    }

    @Transactional
    def update(Exercise exercise) {
        if (exercise == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (exercise.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond exercise.errors, view:'edit'
            return
        }

        exercise.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'exercise.label', default: 'Exercise'), exercise.id])
                redirect exercise
            }
            '*'{ respond exercise, [status: OK] }
        }
    }

    @Transactional
    def delete(Exercise exercise) {

        if (exercise == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        exercise.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'exercise.label', default: 'Exercise'), exercise.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'exercise.label', default: 'Exercise'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
