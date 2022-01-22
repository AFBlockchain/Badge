package hk.edu.polyu.af.bc.badge.badgeclass

import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.assertion.IssueAssertionTest
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertion
import hk.edu.polyu.af.bc.badge.flows.assertion.ReadAssertionByID
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadAllBadgeClass
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadBadgeClassById
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadBadgeClassByName
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.StateAndRef
import net.corda.core.utilities.getOrThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadAssertionTest : UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CreateBadgeClassTest::class.java)
    }

    @Test
    fun `can read by id`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertion = assertionTx.output(Assertion::class.java)
        val uuid=assertion.linearId.id
        val result=instA.startFlow(ReadAssertionByID(uuid)).getOrThrow(network)
        assertEquals(assertion,result)
        
    }
}