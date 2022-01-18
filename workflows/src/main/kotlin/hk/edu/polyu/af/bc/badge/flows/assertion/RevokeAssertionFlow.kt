package hk.edu.polyu.af.bc.badge.flows.assertion


import com.r3.corda.lib.tokens.contracts.NonFungibleTokenContract
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import com.r3.corda.lib.tokens.contracts.types.TokenType
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import jdk.nashorn.internal.ir.annotations.Immutable
import net.corda.core.contracts.*
import net.corda.core.flows.CollectSignatureFlow
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
        val queryCriteria=QueryCriteria.LinearStateQueryCriteria(null, listOf(uuid),null, Vault.StateStatus.UNCONSUMED,null)
        val assertionRef =serviceHub.vaultService.queryBy<Assertion>(queryCriteria).states.get(0)
        val inputAssertion=assertionRef.state.data
        val notary  = serviceHub.networkMapCache.notaryIdentities[0]
        val stateAndRef = StateAndRef(TransactionState(inputAssertion,notary=notary), StateRef(inputAssertion.hash(),0))
        val txBuilder=TransactionBuilder(notary)
                .addInputState(stateAndRef)
        txBuilder.verify(serviceHub)
        val signedTx=serviceHub.signInitialTransaction(txBuilder)
        return subFlow(FinalityFlow(signedTx))
    }
}

