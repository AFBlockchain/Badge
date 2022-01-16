package hk.edu.polyu.af.bc.badge.flows.assertion

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.SignedTransaction
import java.util.*

/**
 * Issue an [Assertion].
 *
 * The calling node is the **issuer** of this [Assertion], which must be (enforced by contract) the **issuer** of the
 * underlying [BadgeClass]. The consistency should also be checked by this flow.
 *
 * @property badgeClassPointer a pointer the the [BadgeClass]. This can be obtained by calling `toPointer` of a [BadgeClass]
 * which may in turn be obtained from a vault query; or can be constructed directly if the linearId is known.
 * @property recipient recipient of this [Assertion]
 */
@StartableByService
@InitiatingFlow
@StartableByRPC
class IssueAssertion (
        private var badgeClassPointer: TokenPointer<BadgeClass>,
        private var recipient: AbstractParty
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        // TODO: check that we are the `issue` of the BadgeClass
        val assertion= Assertion(badgeClassPointer,
                ourIdentity,
                recipient,
                Date(),  // current time
                false,
                UniqueIdentifier())

        return subFlow(IssueTokens(listOf(assertion)))
    }
}