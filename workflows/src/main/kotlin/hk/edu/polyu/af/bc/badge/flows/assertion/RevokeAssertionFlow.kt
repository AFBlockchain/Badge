package hk.edu.polyu.af.bc.badge.flows.assertion

import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.*
import net.corda.core.flows.CollectSignatureFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

class RevokeAssertionFlow (
        private var badgeClassPointer: TokenPointer<BadgeClass>,
        private var recipient: AbstractParty,
        private var inputAssertion: Assertion
        ): FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        val issuer: Party = ourIdentity
        val calendar:Calendar=Calendar.getInstance()
        val assertion= Assertion(badgeClassPointer, issuer, recipient, Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DATE)),false, UniqueIdentifier())
        val notary  = serviceHub.networkMapCache.notaryIdentities[0]
        val stateAndRef = StateAndRef(TransactionState(inputAssertion,notary=notary), StateRef(inputAssertion.hash(),0))
        val txBuilder=TransactionBuilder(notary)
                .addInputState(stateAndRef)
                .addOutputState(assertion)
        txBuilder.verify(serviceHub)
        val signedTx=serviceHub.signInitialTransaction(txBuilder)
        val session=initiateFlow(recipient)
        return subFlow(FinalityFlow(signedTx,session))
    }
}

