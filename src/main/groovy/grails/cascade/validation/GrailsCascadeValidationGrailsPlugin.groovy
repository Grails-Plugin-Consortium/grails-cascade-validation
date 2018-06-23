package grails.cascade.validation

import com.cscinfo.platform.constraint.CascadeValidationConstraint
import grails.plugins.Plugin
import org.grails.datastore.gorm.validation.constraints.factory.DefaultConstraintFactory
import org.grails.datastore.gorm.validation.constraints.eval.ConstraintsEvaluator
import org.grails.datastore.gorm.validation.constraints.eval.DefaultConstraintEvaluator
import org.grails.datastore.gorm.validation.constraints.registry.ConstraintRegistry

class GrailsCascadeValidationGrailsPlugin extends Plugin {

    def grailsVersion = "3.3.0 > *"
    def title = "Cascade Validation Plugin"
    def author = "Russell Morrisey"
    def authorEmail = "rmorrise@cscinfo.com"
    def description = '''\
Establishes a 'cascade' constraint property for validateable objects. If "cascade:true" is set
 on a nested object, the nested object's validate() method will be invoked and the results will
 be reported as part of the parent object's validation.

Based on a blog post by Eric Kelm:
 http://asoftwareguy.com/2013/07/01/grails-cascade-validation-for-pogos/
Used with permission.
'''
    def documentation = "https://github.com/rmorrise/grails-cascade-validation/wiki/How-to-use-cascade-validation"
    def license = "APACHE"
    def organization = [name: "CSC", url: "http://www.cscglobal.com/"]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/rmorrise/grails-cascade-validation/issues']
    def scm = [url: 'https://github.com/rmorrise/grails-cascade-validation']

    def developers = [ [ name: "Soeren Glasius", email: "soeren@glasius.dk" ], [ name: "Russell Morrisey", email: "russell.morrisey@cscglobal.com" ]]

    //Class<? extends Constraint> constraintClass, MessageSource messageSource, List<Class> targetTypes = [Object]
    Closure doWithSpring() {{ ->
        cascadeValidationConstraintFactory(DefaultConstraintFactory, CascadeValidationConstraint, null)
    }}

    void doWithApplicationContext() {
        //This method for registering constraints came from longwa
        List<ConstraintRegistry> registries = []
        DefaultConstraintEvaluator evaluator = applicationContext.getBean(ConstraintsEvaluator) as DefaultConstraintEvaluator

        // Register with both the default constraint as well as the gorm registry (it's stupid that it needs both)
        // Also the ConstraintsEvaluator evaluator constructs a new internal registry and doesn't seem to expose it
        // so we are forced to invade it's privacy if we want custom constraints for Validateable instances.
        registries << evaluator.constraintRegistry
        registries << applicationContext.getBean("gormValidatorRegistry", ConstraintRegistry)

        registries.each { ConstraintRegistry registry ->
            registry.addConstraint(CascadeValidationConstraint)
        }
    }

}
