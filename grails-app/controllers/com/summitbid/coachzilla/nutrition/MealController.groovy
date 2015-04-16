package com.summitbid.coachzilla.nutrition

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class MealController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Meal.list(params), model:[mealCount: Meal.count()]
    }

    def show(Meal meal) {
        respond meal
    }

    def create() {
        respond new Meal(params)
    }

    @Transactional
    def save(Meal meal) {
        if (meal == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (meal.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond meal.errors, view:'create'
            return
        }

        meal.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'meal.label', default: 'Meal'), meal.id])
                redirect meal
            }
            '*' { respond meal, [status: CREATED] }
        }
    }

    def edit(Meal meal) {
        respond meal
    }

    @Transactional
    def update(Meal meal) {
        if (meal == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (meal.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond meal.errors, view:'edit'
            return
        }

        meal.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'meal.label', default: 'Meal'), meal.id])
                redirect meal
            }
            '*'{ respond meal, [status: OK] }
        }
    }

    @Transactional
    def delete(Meal meal) {

        if (meal == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        meal.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'meal.label', default: 'Meal'), meal.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'meal.label', default: 'Meal'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
