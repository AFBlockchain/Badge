package hk.edu.polyu.af.bc.badge.flows.badgeclass

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.LinearPointer
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService


/**
 * Read all [BadgeClass].
 *
 */
@StartableByRPC
@StartableByService
class ReadAllBadgeClass : FlowLogic<List<StateAndRef<BadgeClass>>>() {
    @Suspendable
    override fun call(): List<StateAndRef<BadgeClass>> {
        return serviceHub.vaultService.queryBy(BadgeClass::class.java).states
    }
}

/**
 * Read a [BadgeClass] by ID.
 *
 * @property linearId the linearId of a badgeClass
 */
@StartableByRPC
@StartableByService
class ReadBadgeClassById(private val linearId: UniqueIdentifier) : FlowLogic<StateAndRef<BadgeClass>>() {
    @Suspendable
    override fun call(): StateAndRef<BadgeClass> {
        return LinearPointer(
            linearId,
            BadgeClass::class.java,
            false
        ).resolve(serviceHub)
    }
}

/**
 * Read a [BadgeClass] by name.
 *
 * @property name the name of a badgeClass
 */
@StartableByRPC
@StartableByService
class ReadBadgeClassByName(private val name: String) : FlowLogic<StateAndRef<BadgeClass>>() {
    @Suspendable
    override fun call(): StateAndRef<BadgeClass> {
        val allBadgeClass = subFlow(ReadAllBadgeClass())
        val result = allBadgeClass.filter { it.state.data.name == name }
        if(result.isEmpty()) {
            throw FlowException("The $name is not exist")
        }
        return result[0]
    }
}