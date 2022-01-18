package hk.edu.polyu.af.bc.badge.flows.assertion

import hk.edu.polyu.af.bc.badge.states.Assertion
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

import java.util.*

class ReadAssertionFlow (
    private var uuid: UUID
    ): FlowLogic<Assertion>() {
    override fun call(): Assertion {
        val queryCriteria= QueryCriteria.LinearStateQueryCriteria(null, listOf(uuid),null, Vault.StateStatus.UNCONSUMED,null)
        val assertionRef =serviceHub.vaultService.queryBy<Assertion>(queryCriteria).states.get(0)
        val assertion=assertionRef.state.data
        return assertion
    }
}
