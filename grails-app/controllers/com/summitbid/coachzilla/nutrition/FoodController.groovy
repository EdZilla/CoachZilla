package com.summitbid.coachzilla.nutrition

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FoodController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Food.list(params), model:[foodCount: Food.count()]
    }

    def show(Food food) {
        respond food
    }

    def create() {
        respond new Food(params)
    }

    @Transactional
    def save(Food food) {
        if (food == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (food.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond food.errors, view:'create'
            return
        }

        food.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'food.label', default: 'Food'), food.id])
                redirect food
            }
            '*' { respond food, [status: CREATED] }
        }
    }

    def edit(Food food) {
        respond food
    }

    @Transactional
    def update(Food food) {
        if (food == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (food.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond food.errors, view:'edit'
            return
        }

        food.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'food.label', default: 'Food'), food.id])
                redirect food
            }
            '*'{ respond food, [status: OK] }
        }
    }

    @Transactional
    def delete(Food food) {

        if (food == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        food.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'food.label', default: 'Food'), food.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'food.label', default: 'Food'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
