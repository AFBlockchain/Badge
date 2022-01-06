package hk.edu.polyu.af.bc.badge.badgeclass

import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.flows.UpdateBadgeClass
import hk.edu.polyu.af.bc.badge.flows.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.LinearPointer
import net.corda.core.contracts.StateAndRef
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BadgeClassByIdTest : UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(BadgeClassByIdTest::class.java)
    }

    @Test
    fun `can query by Id`() {

        val tx = instA.startFlow(CreateBadgeClass("Test Badge", "Just for testing", ByteArray(1))).getOrThrow(network)

        val badgeClass = tx.output(BadgeClass::class.java)

        // assert flow output
        assertEquals("Test Badge", badgeClass.name)

        // assert vault status
        instA.assertHaveState(badgeClass) {
                s1, s2 -> s1.linearId == s2.linearId
        }


        //query the BadgeClass by uuid
        val badgeClassRef: StateAndRef<BadgeClass> = LinearPointer<BadgeClass>(
            badgeClass.linearId,
            BadgeClass::class.java,
            false).resolve(instA.services)

        // assert equal of this two badgeClass
        assertEquals(badgeClass.name, badgeClassRef.state.data.name)
    }
}