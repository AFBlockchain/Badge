package hk.edu.polyu.af.bc.badge.flows.assertion

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.AbstractParty
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import java.util.*
@StartableByRPC
@StartableByService
class ReadAssertionByID (
    private var uuid: UUID
    ): FlowLogic<Assertion>() {
    @Suspendable
    override fun call(): Assertion {
        val queryCriteria= QueryCriteria.LinearStateQueryCriteria(null, listOf(uuid),null, Vault.StateStatus.UNCONSUMED,null)
        val assertionRef =serviceHub.vaultService.queryBy<Assertion>(queryCriteria).states.get(0)
        val assertion=assertionRef.state.data
        return assertion
    }
}
@StartableByRPC
@StartableByService
class ReadAssertionByRecipient (
        private var recipient:AbstractParty
): FlowLogic<List<StateAndRef<Assertion>>>() {
    @Suspendable
    override fun call(): List<StateAndRef<Assertion>> {
        val allAssertion= serviceHub.vaultService.queryBy(Assertion::class.java).states
        val result = allAssertion.filter { it.state.data.participants.contains(recipient) }
        return result
    }
}
@StartableByRPC
@StartableByService
class ReadAssertionByBadgeClass (
        private var badgeClass: BadgeClass
): FlowLogic<List<StateAndRef<Assertion>>>() {
    @Suspendable
    override fun call(): List<StateAndRef<Assertion>> {
        val allAssertion= serviceHub.vaultService.queryBy(Assertion::class.java).states
        val result = allAssertion.filter { it.state.data.badgeClassPointer==badgeClass.toPointer(BadgeClass::class.java) }
        return result
    }
}
