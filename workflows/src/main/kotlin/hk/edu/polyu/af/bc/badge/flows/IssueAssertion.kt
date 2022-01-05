package hk.edu.polyu.af.bc.badge.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import java.time.Instant
import java.time.format.DateTimeFormatter

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
        var revoked:Boolean=true
        val issuerofBadgeclass:Party=badgeClassPointer.pointer.resolve(serviceHub).state.data.maintainers[0]
        val issuer: Party = ourIdentity
        if(!issuerofBadgeclass.equals(issuer)){
            revoked=false
        }

        val assertion= Assertion(badgeClassPointer, issuer, recipient, DateTimeFormatter.ISO_INSTANT.format(Instant.now()),revoked,UniqueIdentifier())
        val tokens = listOf(assertion)

        return subFlow(IssueTokens(tokens))
    }
}