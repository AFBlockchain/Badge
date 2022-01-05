package hk.edu.polyu.af.bc.badge

import com.github.manosbatsis.corda.testacles.nodedriver.NodeHandles
import com.github.manosbatsis.corda.testacles.nodedriver.config.NodeDriverNodesConfig
import com.github.manosbatsis.corda.testacles.nodedriver.jupiter.NodeDriverExtensionConfig
import com.github.manosbatsis.corda.testacles.nodedriver.jupiter.NodeDriverNetworkExtension
import hk.edu.polyu.af.bc.badge.flows.CreateBadgeClass
import hk.edu.polyu.af.bc.badge.flows.IssueAssertion
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.messaging.CordaRPCOps
import net.corda.testing.driver.NodeHandle
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@Disabled("Run fails during build")
@ExtendWith(NodeDriverNetworkExtension::class)
class DriverBasedTest {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(DriverBasedTest::class.java)

        @NodeDriverExtensionConfig
        @JvmStatic
        val nodeDriverConfig: NodeDriverNodesConfig = customNodeDriverConfig

        val badgeClasses: MutableMap<String, BadgeClass> = mutableMapOf()
    }

    @Test
    @Order(1)
    fun canRetrieveIdentity(nodeHandles: NodeHandles) {
        val nodeA: NodeHandle = nodeHandles.getNode("partyA")
        assertTrue(nodeA.nodeInfo.legalIdentities.isNotEmpty())

        logger.info("Retrieved nodeA identity: ${nodeA.nodeInfo.legalIdentities[0]}")
    }

    @Test
    @Order(2)
    fun canCreateBadgeClassAtPartyA(nodeHandles: NodeHandles) {
        val nodeA: NodeHandle = nodeHandles.getNode("partyA")
        val proxy: CordaRPCOps = nodeA.rpc

        val tx = proxy.startFlowDynamic(CreateBadgeClass::class.java, "test", "test").returnValue.get()
        logger.info("Transaction: {}", tx.toString())
        val badgeClass = tx.coreTransaction.outputsOfType(BadgeClass::class.java)[0]
        badgeClasses["a"] = badgeClass
        logger.info("BadgeClass: {}", badgeClass.toString())

        assert(proxy.vaultQuery(BadgeClass::class.java).states.stream().anyMatch { (state) -> state.data.linearId == badgeClass.linearId })
    }

    @Test
    @Order(3)
    fun canIssueAssertionFromPartyAToPartyB(nodeHandles: NodeHandles) {
        val nodeA: NodeHandle = nodeHandles.getNode("partyA")
        val nodeB: NodeHandle = nodeHandles.getNode("partyB")
        val issProxy: CordaRPCOps = nodeA.rpc
        val recProxy: CordaRPCOps = nodeB.rpc

        val createdBadgeClass = badgeClasses["a"]!!
        val tx = issProxy.startFlowDynamic(IssueAssertion::class.java, createdBadgeClass.toPointer(BadgeClass::class.java),
                recProxy.nodeInfo().legalIdentities[0]).returnValue.get()
        logger.info("Transaction: {}", tx.toString())
        val assertion = tx.coreTransaction.outputsOfType(Assertion::class.java)[0]
        logger.info("Assertion: {}", assertion.toString())

        val waitTime = 1
        logger.info("Waiting {}s for vaults to reflect changes", waitTime)
        TimeUnit.SECONDS.sleep(waitTime.toLong())

        assert(issProxy.vaultQuery(Assertion::class.java).states.stream().anyMatch { (state) -> state.data.linearId == assertion.linearId })
        assert(recProxy.vaultQuery(Assertion::class.java).states.stream().anyMatch { (state) -> state.data.linearId == assertion.linearId })
    }
}