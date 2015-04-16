package com.summitbid.coachzilla.fitness

import grails.test.mixin.*
import spock.lang.*

@TestFor(WorkoutController)
@Mock(Workout)
class WorkoutControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.workoutList
            model.workoutCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.workout!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def workout = new Workout()
            workout.validate()
            controller.save(workout)

        then:"The create view is rendered again with the correct model"
            model.workout!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            workout = new Workout(params)

            controller.save(workout)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/workout/show/1'
            controller.flash.message != null
            Workout.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def workout = new Workout(params)
            controller.show(workout)

        then:"A model is populated containing the domain instance"
            model.workout == workout
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def workout = new Workout(params)
            controller.edit(workout)

        then:"A model is populated containing the domain instance"
            model.workout == workout
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/workout/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def workout = new Workout()
            workout.validate()
            controller.update(workout)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.workout == workout

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            workout = new Workout(params).save(flush: true)
            controller.update(workout)

        then:"A redirect is issued to the show action"
            workout != null
            response.redirectedUrl == "/workout/show/$workout.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/workout/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def workout = new Workout(params).save(flush: true)

        then:"It exists"
            Workout.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(workout)

        then:"The instance is deleted"
            Workout.count() == 0
            response.redirectedUrl == '/workout/index'
            flash.message != null
    }
}
