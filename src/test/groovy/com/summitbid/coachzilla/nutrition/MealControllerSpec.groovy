package com.summitbid.coachzilla.nutrition

import grails.test.mixin.*
import spock.lang.*

@TestFor(MealController)
@Mock(Meal)
class MealControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.mealList
            model.mealCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.meal!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def meal = new Meal()
            meal.validate()
            controller.save(meal)

        then:"The create view is rendered again with the correct model"
            model.meal!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            meal = new Meal(params)

            controller.save(meal)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/meal/show/1'
            controller.flash.message != null
            Meal.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def meal = new Meal(params)
            controller.show(meal)

        then:"A model is populated containing the domain instance"
            model.meal == meal
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def meal = new Meal(params)
            controller.edit(meal)

        then:"A model is populated containing the domain instance"
            model.meal == meal
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/meal/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def meal = new Meal()
            meal.validate()
            controller.update(meal)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.meal == meal

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            meal = new Meal(params).save(flush: true)
            controller.update(meal)

        then:"A redirect is issued to the show action"
            meal != null
            response.redirectedUrl == "/meal/show/$meal.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/meal/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def meal = new Meal(params).save(flush: true)

        then:"It exists"
            Meal.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(meal)

        then:"The instance is deleted"
            Meal.count() == 0
            response.redirectedUrl == '/meal/index'
            flash.message != null
    }
}
