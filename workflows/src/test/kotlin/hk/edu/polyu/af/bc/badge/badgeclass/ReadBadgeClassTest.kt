package hk.edu.polyu.af.bc.badge.badgeclass

import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadAllBadgeClass
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadBadgeClassById
import hk.edu.polyu.af.bc.badge.flows.badgeclass.ReadBadgeClassByName
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.StateAndRef
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadBadgeClassTest : UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(ReadBadgeClassTest::class.java)
    }

    @Test
    fun `can read by Id`() {

        val tx = instA.startFlow(CreateBadgeClass("Test Badge", "Just for testing", ByteArray(1))).getOrThrow(network)

        val badgeClass = tx.output(BadgeClass::class.java)

        // assert flow output
        assertEquals("Test Badge", badgeClass.name)

        // assert vault status
        instA.assertHaveState(badgeClass) {
                s1, s2 -> s1.linearId == s2.linearId
        }


        //read the BadgeClass by uuid
        val badgeClassRef: StateAndRef<BadgeClass> = instA.startFlow(ReadBadgeClassById(badgeClass.linearId)).getOrThrow(network)

        // assert equal of this two badgeClass
        assertEquals(badgeClass.name, badgeClassRef.state.data.name)
    }

    @Test
    fun `can read all the BadgeClass`() {
        instA.startFlow(CreateBadgeClass("Test Badge1", "Just for testing", null)).getOrThrow(network)
        instA.startFlow(CreateBadgeClass("Test Badge2", "Just for testing", null)).getOrThrow(network)

        //read all the BadgeClass
        val allBadgeClassRef = instA.startFlow(ReadAllBadgeClass()).getOrThrow(network)
        val allNames = allBadgeClassRef.map { it.state.data.name }

        //assert the result
        assertTrue(allNames.contains("Test Badge1"))
        assertTrue(allNames.contains("Test Badge2"))
    }

    @Test
    fun `can read the BadgeClass by name`() {
        instA.startFlow(CreateBadgeClass("Test Badge1", "Just for testing", null)).getOrThrow(network)

        //read BadgeClass by name
        val badgeClassRef = instA.startFlow(ReadBadgeClassByName("Test Badge1")).getOrThrow(network)

        //assert the result
        assertTrue(badgeClassRef.state.data.name == "Test Badge1")
    }
}