package com.summitbid.coachzilla

import java.util.SortedSet;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Abstract class used for storing general tracking information about classes.
 * Taps in to some default "magic" built into Grails for tracking the date
 * something is created and the date it was last updated.  Defines two event
 * handlers for updating the User who created the object and the User who
 * last modified the object.  If more detailed auditing is necessary then
 * that functionality should be added to the event handlers below (beforeInsert
 * and beforeUpdate).
 *
 * @since 0.4
 * @author Daniel Glauser
 */
abstract class BaseDomain implements Comparable ,Serializable { //Implements Serializable for WebFlow
	public static final long serialVersionUid = 1l;
	String uuid
	String name
	Date dateCreated
	Date lastUpdated
	
	def final static transientFields = ['createdBy', 'modifiedBy', 'lastUpdated', 'dateCreated']
	def final static collectionClasses = [	SortedSet.class.getName(), 
										TreeSet.class.getName(), 
										Set.class.getName()]
	
	static constraints = {
		uuid(nullable:true, size:36..72)
		name(nullable:true, size:1..255)
		dateCreated(nullable:true)
		lastUpdated(nullable:true)
	}
	
	
	static mapping = {
		tablePerHierarchy false
	}
	
	transient def beforeInsert = {
		if(! this.uuid) {
			uuid = java.util.UUID.randomUUID().toString()
		}
	}
	
	transient def beforeUpdate = {
		
	}
	
	transient def beforeDelete = {

	}
	
	String toString() {
		name
	}
	
	int compareTo(obj) {
		def thisId = this.id ?: 0
		def objId  = obj?.id ?: 0
		
		thisId - objId
	}
	
	/**
	 * Validates and saves the supplied domain object.
	 * Issues are printed to standard out.
	 * @author Ed Young
	 * @author Daniel Glauser
	 * @since 0.5
	 */
	def validateAndSave = { flushStatus ->
		def savedObj
		def valid = this.validate()
		
		if (valid) {
			savedObj = this.save(flush:flushStatus)
		}
		
		if(!valid || !savedObj) {
			log.error("Error saving " + this)
			log.error this.errors
		}
		
		savedObj
	}
	
	
	/**
	 * Default validateAndSave method without Boolean flash status 
	 * @return : Boolean
	 * 
	 * @author subrata
	 */
	def validateAndSave() {
		this.validateAndSave(true)
	}
	
	
	/**
	 * Validates and Saves domain instances with tacking the changes 
	 * as History instances.
	 * @param oldInstance : domain instance with previous values
	 * @param flushStatus : Boolean (Hibernate session flush parameter)
	 * @return : Boolean (true if saved else false)
	 * 
	 * @author subrata
	 */
	def Boolean validateAndSave(def oldInstance, def flushStatus) {
		
		Boolean isValidateAndSaved = false
		def valid = this.validate()
		
		if (valid) {
			compareObjectState(oldInstance)
			isValidateAndSaved = this.save(flush:flushStatus)
		}
		
		if(!valid || !isValidateAndSaved) {
			log.error("Error saving " + this)
			log.error this.errors
		}
		isValidateAndSaved
	}
	
	/**
	 * Validates and Saves domain instances with tacking the changes
	 * as History instances.
	 * @param fieldName 		: String (name of the changed field)
	 * @param oldValue 			: String (old value of that field)
	 * @param newValue 			: String (new value of that field)
	 * @return : Boolean (true if saved else false)
	 *
	 * @author Retheeshkumar
	 */
	def Boolean validateAndSave(String fieldName, String oldValue,String newValue, def user) {
		
		
		Boolean isValidateAndSaved = false
		def valid = this.validate()
		if (valid) {
			isValidateAndSaved = this.save(flush:true)
			if(isValidateAndSaved){
				def description="Modified a " +this.getClass()+" named "+this?.name
			}
		}
		
		if(!valid || !isValidateAndSaved) {
			log.error("Error saving " + this)
			log.error this.errors
		}
		isValidateAndSaved
	}
	
	/**
	* Validates and Saves domain instances with old value and new value.
	* This is the over-ride method of validateAndSave(baseDomain domain, Boolean flushStatus)
	* to reduce the flow. This method can be called when program knows which all changes are
	* going to be made. Like, Reservation-Auto-Status-Update thread: here only status is
	* going to change, so in this scenario we can use this method to avoid lots of checking.
	*  
	* @param fieldName 			: String (name of the changed field)
	* @param oldValue 			: String (old value of that field)
	* @param newValue 			: String (new value of that field)
	* @param customizedVersion 	: String (Math.random().toString() to make group)
	* @param flushStatus 		: Boolean (Hibernate session flush parameter)
	* @return 					: Boolean (true if saved else false)
	*
	* @author subrata
	*/
	def Boolean validateAndSave(String fieldName, String oldValue, String newValue, String customizedVersion, def flushStatus) {
		
		Boolean isValidateAndSaved = false
		def valid = this.validate()
		
		if (valid) {
			isValidateAndSaved = this.save(flush:flushStatus)
		}
		
		if(!valid || !isValidateAndSaved) {
			log.error("Error saving " + this)
			log.error this.errors
		}
		isValidateAndSaved
	}
		
	
	/**
	 * To compare whether the values of two collection
	 * types are same or not. Such as for any domain instance
	 * whether old and new collection are remain same or changed.
	 * 
	 * @param property		: Instance of DefaultGrailsDomainClassProperty
	 * @param oldInstance	: Instance of Domain
	 * @return 				: Boolean (true if values changed, else false)
	 * 
	 * @author subrata
	 */
	protected def isCollectionChanged = { property, oldInstance -> 
		def isChanged = false
		def oldValues = oldInstance.(property.getName())?.id
		def newValues = this.(property.getName())?.id
		
		Integer numberOfOldRecords = oldValues?.size() ?: 0
		if(newValues) oldValues?.retainAll(newValues)
		
		if(numberOfOldRecords != (oldValues?.size() ?: 0) || numberOfOldRecords != (newValues?.size() ?: 0)) isChanged = true
		
		isChanged
	}

	
}


