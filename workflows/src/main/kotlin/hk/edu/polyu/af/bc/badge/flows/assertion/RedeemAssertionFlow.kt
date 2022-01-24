package hk.edu.polyu.af.bc.badge.flows.assertion


import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokens
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import java.util.*

/**
 * Revoke an assertion.
 *
 * This flow is used to revoke an existed assertion by uuid
 *
 * @property  uuid the uuid of the assertion
 */
@StartableByRPC
@StartableByService
class RedeemAssertionFlow (
        private var uuid:UUID
        ): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val oldAssertion = subFlow(GetAssertionByID(uuid))
        return (subFlow(RedeemNonFungibleTokens(oldAssertion.tokenType,ourIdentity)))
    }
}
