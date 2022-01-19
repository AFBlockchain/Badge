package hk.edu.polyu.af.bc.badge.flows.assertion

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokens
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.LinearPointer
import net.corda.core.contracts.StatePointer
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
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
        val issuerofBadgeclass: Party =badgeClassPointer.pointer.resolve(serviceHub).state.data.maintainers.get(0)
        val issuer: Party = ourIdentity
        if(issuerofBadgeclass != issuer){
            throw FlowException("the issuer of the BadgeClass is not the issuer of the assertion")
        }
        val assertion= Assertion(badgeClassPointer,
                ourIdentity,
                recipient,
                Date(),  // current time
                false,
                UniqueIdentifier())

        return subFlow(IssueTokens(listOf(assertion)))
    }
}
@StartableByRPC
@StartableByService
class IssueAssertionByID(
        private var uuid: UUID,
        private var recipient: AbstractParty
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val queryCriteria= QueryCriteria.LinearStateQueryCriteria(null, listOf(uuid),null, Vault.StateStatus.UNCONSUMED,null)
        val BadgeClassRef =serviceHub.vaultService.queryBy<BadgeClass>(queryCriteria).states.get(0)
        val badgeClass=BadgeClassRef.state.data
        val badgeClassPointer=badgeClass.toPointer<BadgeClass>()
        val assertion= Assertion(badgeClassPointer,
                ourIdentity,
                recipient,
                Date(),  // current time
                false,
                UniqueIdentifier())
        return subFlow(IssueTokens(listOf(assertion)))

    }
}
@StartableByRPC
@StartableByService
class IssueAssertionByName(
        private var name: String,
        private var recipient: AbstractParty
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val allBadgeClass = serviceHub.vaultService.queryBy(BadgeClass::class.java).states
        val result = allBadgeClass.filter { it.state.data.name == name }
        if(result.isEmpty()) {
            throw FlowException("The $name is not exist")
        }
        val badgeClass=result.get(0)
        val badgeClassPointer=badgeClass.state.data.toPointer<BadgeClass>()
        val assertion= Assertion(badgeClassPointer,
                ourIdentity,
                recipient,
                Date(),  // current time
                false,
                UniqueIdentifier())
        return subFlow(IssueTokens(listOf(assertion)))

    }
}
