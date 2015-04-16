package com.summitbid.coachzilla.fitness

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ActivityController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Activity.list(params), model:[activityCount: Activity.count()]
    }

    def show(Activity activity) {
        respond activity
    }

    def create() {
        respond new Activity(params)
    }

    @Transactional
    def save(Activity activity) {
        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (activity.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond activity.errors, view:'create'
            return
        }

        activity.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect activity
            }
            '*' { respond activity, [status: CREATED] }
        }
    }

    def edit(Activity activity) {
        respond activity
    }

    @Transactional
    def update(Activity activity) {
        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (activity.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond activity.errors, view:'edit'
            return
        }

        activity.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect activity
            }
            '*'{ respond activity, [status: OK] }
        }
    }

    @Transactional
    def delete(Activity activity) {

        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        activity.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'activity.label', default: 'Activity'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
