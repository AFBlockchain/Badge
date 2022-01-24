package hk.edu.polyu.af.bc.badge.flows.assertion

import com.r3.corda.lib.tokens.contracts.NonFungibleTokenContract
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import hk.edu.polyu.af.bc.badge.states.Assertion
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

class RevokeAssertionFlow (
        private var uuid:UUID
        ): FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        val queryCriteria= QueryCriteria.LinearStateQueryCriteria(null, listOf(uuid),null, Vault.StateStatus.UNCONSUMED,null)
        val assertionRef =serviceHub.vaultService.queryBy<Assertion>(queryCriteria).states.get(0)

        val assertion=assertionRef.state.data
        val newAssertion= Assertion(assertion.badgeClassPointer,
                assertion.issuer,
                assertion.participants[0],
                Date(),  // current time
                false,
                assertion.linearId)

        val notary=serviceHub.networkMapCache.notaryIdentities.first()

        val transactionBuilder=TransactionBuilder(notary = notary).apply {
            addInputState(assertionRef)
            addOutputState(newAssertion,NonFungibleTokenContract.contractId)
            addCommand(IssueTokenCommand(assertion.issuedTokenType,listOf(0)), listOf(assertion.participants[0]).map{it.owningKey})

        }
        transactionBuilder.verify(serviceHub)

        val fullySignedTx=serviceHub.signInitialTransaction(transactionBuilder)

        val otherParticipants=ArrayList(newAssertion.participants)
        otherParticipants.remove(ourIdentity)
        val otherPartySessions=otherParticipants.map{initiateFlow(it)}

        return subFlow(FinalityFlow(fullySignedTx,otherPartySessions))
    }
}
