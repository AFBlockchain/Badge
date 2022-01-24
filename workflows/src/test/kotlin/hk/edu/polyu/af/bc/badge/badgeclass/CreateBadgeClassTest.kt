package hk.edu.polyu.af.bc.badge.badgeclass

import com.google.common.io.ByteStreams.copy
import hk.edu.polyu.af.bc.badge.UnitTestBase
import hk.edu.polyu.af.bc.badge.assertHaveState
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClassByND
import hk.edu.polyu.af.bc.badge.getOrThrow
import hk.edu.polyu.af.bc.badge.output
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBadgeClassTest: UnitTestBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CreateBadgeClassTest::class.java)
    }

    @Test
    fun `can create a BadgeClass`() {

        //create the image byte
        val filePath = "images/ob_flows.png"
        val fileIn:InputStream? = this.javaClass.classLoader.getResourceAsStream(filePath)
        val imageStream = ByteArrayOutputStream()
        val fileOut = BufferedOutputStream(imageStream,1024)
        copy(fileIn,fileOut)
        fileOut.flush()
        val imageByte = imageStream.toByteArray()

        val tx = instA.startFlow(CreateBadgeClass("Test Badge", "Just for testing", imageByte)).getOrThrow(network)

        // assert flow output
        val badgeClass = tx.output(BadgeClass::class.java)
        assertEquals("Test Badge", badgeClass.name)


        // assert vault status
        instA.assertHaveState(badgeClass) {
            s1, s2 -> s1.linearId == s2.linearId
        }
    }

    @Test
    fun `can create BadgeClass By name and description`() {
        val tx = instA.startFlow(CreateBadgeClassByND("Test Badge", "Just for testing")).getOrThrow(network)

        // assert flow output
        val badgeClass = tx.output(BadgeClass::class.java)
        assertEquals("Test Badge", badgeClass.name)

        // assert vault status
        instA.assertHaveState(badgeClass) {
                s1, s2 -> s1.linearId == s2.linearId
        }
    }
}