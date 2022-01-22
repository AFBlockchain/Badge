package hk.edu.polyu.af.bc.badge.badgeclass
import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.flows.assertion.GetAssertionByBadgeClass
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertion
import hk.edu.polyu.af.bc.badge.flows.assertion.GetAssertionByID
import hk.edu.polyu.af.bc.badge.flows.assertion.GetAssertionByRecipient
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAssertionTest : UnitTestBase() {
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
        val result=instA.startFlow(GetAssertionByID(uuid)).getOrThrow(network)
        assertEquals(assertion,result)
        
    }
    @Test
    fun `can read by recipient`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertion = assertionTx.output(Assertion::class.java)
        val recipient=assertion.participants[1]
        val result=instA.startFlow(GetAssertionByRecipient(recipient)).getOrThrow(network)
        val final=result.get(0).state.data
        assertEquals(assertion,final)

    }
    @Test
    fun `can read by badgeclass`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertion = assertionTx.output(Assertion::class.java)
        val result=instA.startFlow(GetAssertionByBadgeClass(badgeClass)).getOrThrow(network)
        val final=result.get(0).state.data
        assertEquals(assertion,final)

    }
}
