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
class UpdateBadgeClassTest:UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(UpdateBadgeClassTest::class.java)
    }

    @Test
    fun `can update a BadgeClass`() {

        val tx = instA.startFlow(CreateBadgeClass("Test Badge", "Just for testing", ByteArray(1))).getOrThrow(network)

        val badgeClass = tx.output(BadgeClass::class.java)

        // assert flow output
        assertEquals("Test Badge", badgeClass.name)

        // assert vault status
        instA.assertHaveState(badgeClass) {
                s1, s2 -> s1.linearId == s2.linearId
        }



        val tx2 = instA.startFlow(UpdateBadgeClass(badgeClass.linearId,"name changed", "description changed", ByteArray(1))).getOrThrow(network)

        //assert flow output
        val updateBadgeClass = tx2.output(BadgeClass::class.java)
        assertEquals("name changed",updateBadgeClass.name)

        // assert vault status
        instA.assertHaveState(updateBadgeClass) {
                s1, s2 -> s1.linearId == s2.linearId
        }



    }
}