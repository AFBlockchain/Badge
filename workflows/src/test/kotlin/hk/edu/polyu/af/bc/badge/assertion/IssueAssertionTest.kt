package hk.edu.polyu.af.bc.badge.assertion

import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.badgeclass.CreateBadgeClassTest
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertion
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertionByID
import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertionByName
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IssueAssertionTest: UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CreateBadgeClassTest::class.java)
    }

    @Test
    fun `InstitutionA issues an Assertion to LearnerA`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertion = assertionTx.output(Assertion::class.java)
        logger.info("Assertion issued: {}", assertion.toString())

        instA.assertHaveState(assertion) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
        learnerA.assertHaveState(assertion) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
    }

    @Test
    fun `InstitutionA issues an Assertion to LearnerA by uuid`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClassTxB = instB.startFlow(CreateBadgeClass("test1", "test1", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        val badgeClassB = badgeClassTxB.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val uuid=badgeClassB.linearId.id
        logger.info("Issuing assertion")
        val assertionTxB = instB.startFlow(IssueAssertionByID(uuid, learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertionB = assertionTxB.output(Assertion::class.java)
        logger.info("Assertion issued: {}", assertionB.toString())
        instB.assertHaveState(assertionB) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
        learnerA.assertHaveState(assertionB) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
    }
    @Test
    fun `InstitutionA issues an Assertion to LearnerA by name`() {
        // create a BadgeClass
        logger.info("Creating BadgeClass");
        val badgeClassTx = instA.startFlow(CreateBadgeClass("test", "test", ByteArray(1))).getOrThrow(network)
        val badgeClassTxB = instB.startFlow(CreateBadgeClass("test1", "test1", ByteArray(1))).getOrThrow(network)
        val badgeClass = badgeClassTx.output(BadgeClass::class.java)
        val badgeClassB = badgeClassTxB.output(BadgeClass::class.java)
        logger.info("BadgeClass Created: {}", badgeClass);

        // issue an Assertion
        logger.info("Issuing assertion")
        val assertionTx = instA.startFlow(IssueAssertion(badgeClass.toPointer(), learnerA.info.legalIdentities[0])).getOrThrow(network)
        val name=badgeClassB.name
        logger.info("Issuing assertion")
        val assertionTxB = instB.startFlow(IssueAssertionByName(name, learnerA.info.legalIdentities[0])).getOrThrow(network)
        val assertionB = assertionTxB.output(Assertion::class.java)
        logger.info("Assertion issued: {}", assertionB.toString())
        instB.assertHaveState(assertionB) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
        learnerA.assertHaveState(assertionB) {
            s1, s2 ->
            s1.linearId == s2.linearId
        }
    }
}
