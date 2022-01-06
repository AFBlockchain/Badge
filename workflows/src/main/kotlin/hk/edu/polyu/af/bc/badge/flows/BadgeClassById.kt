package hk.edu.polyu.af.bc.badge.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.LinearPointer
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService

/**
 * Query a [BadgeClass] by ID.
 *
 *
 * @property linearId the linearId of a badgeClass
 */

@StartableByRPC
@StartableByService
class AccountInfoByUUID(private val linearId: UniqueIdentifier) : FlowLogic<StateAndRef<BadgeClass>>() {
    @Suspendable
    override fun call(): StateAndRef<BadgeClass> {
        return LinearPointer(
            linearId,
            BadgeClass::class.java,
            false
        ).resolve(serviceHub)
    }
}