package hk.edu.polyu.af.bc.badge.assertion

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.workflows.flows.rpc.RedeemNonFungibleTokens
import com.r3.corda.lib.tokens.workflows.utilities.heldTokensByToken
import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.badgeclass.CreateBadgeClassTest
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertion
import hk.edu.polyu.af.bc.badge.flows.assertion.RedeemAssertionFlow
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.assertj.core.api.Assertions.assertThat


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedeemAssertionFlowTest: UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CreateBadgeClassTest::class.java)
    }

    @Test
    fun `can redeem assertion`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass")
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test badgeClass1", "test", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertion = assertionTx.output(Assertion::class.java)
        val uuid = assertion.linearId.id

        //redeem the assertion
        learnerA.startFlow(RedeemAssertionFlow(uuid))

        // Checking if the tokens are redeemed.
        assertThat(learnerA.services.vaultService.heldTokensByToken(badgeClass.toPointer<EvolvableTokenType>()).states).isEmpty()

    }
}