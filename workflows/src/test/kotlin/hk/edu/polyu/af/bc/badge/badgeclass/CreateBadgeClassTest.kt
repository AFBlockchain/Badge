package hk.edu.polyu.af.bc.badge.badgeclass

import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.flows.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBadgeClassTest: UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CreateBadgeClassTest::class.java)
    }

    @Test
    fun `can create a BadgeClass`() {
        val tx = instA.startFlow(CreateBadgeClass("Test Badge", "Just for testing", BufferedImage(1,1,1))).getOrThrow(network)

        // assert flow output
        val badgeClass = tx.output(BadgeClass::class.java)
        assertEquals("Test Badge", badgeClass.name)

        // assert vault status
        instA.assertHaveState(badgeClass) {
            s1, s2 -> s1.linearId == s2.linearId
        }
    }
}