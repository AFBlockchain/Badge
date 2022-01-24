package hk.edu.polyu.af.bc.badge

import net.corda.core.identity.CordaX500Name
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

/**
 * This class provides default setup and configuration for unit-testing this module.
 * 
 * Test classes need to be annotated with `@TestInstance(PER_CLASS)`.
 */
abstract class UnitTestBase {
    // network
    protected lateinit var network: MockNetwork
    
    // nodes
    protected lateinit var instA: StartedMockNode
    protected lateinit var instB: StartedMockNode
    protected lateinit var learnerA: StartedMockNode
    protected lateinit var learnerB: StartedMockNode
    protected lateinit var observerA: StartedMockNode
    protected lateinit var observerB: StartedMockNode

    @BeforeAll
    fun setup() {
        network = MockNetwork(
                MockNetworkParameters(cordappsForAllNodes = listOf(
                        TestCordapp.findCordapp("hk.edu.polyu.af.bc.badge.contracts"),
                        TestCordapp.findCordapp("hk.edu.polyu.af.bc.badge.flows"),
                        TestCordapp.findCordapp("com.r3.corda.lib.tokens.workflows.flows"),
                        TestCordapp.findCordapp("com.r3.corda.lib.tokens.contracts"),
                        TestCordapp.findCordapp("com.r3.corda.lib.ci.workflows")
                ), networkParameters = testNetworkParameters(minimumPlatformVersion = 4)))

        instA = network.createPartyNode(CordaX500Name.parse("O=InstituteA, L=Athens, C=GR"))
        instB = network.createPartyNode(CordaX500Name.parse("O=InstituteB, L=Athens, C=GR"))
        learnerA = network.createPartyNode(CordaX500Name.parse("O=LearnerA, L=Athens, C=GR"))
        learnerB = network.createPartyNode(CordaX500Name.parse("O=LearnerB, L=Athens, C=GR"))
        observerA = network.createPartyNode(CordaX500Name.parse("O=ObserverA, L=Athens, C=GR"))
        observerB = network.createPartyNode(CordaX500Name.parse("O=ObserverB, L=Athens, C=GR"))

        network.runNetwork()
    }

    @AfterAll
    fun tearDown() {
        network.stopNodes()
    }
}