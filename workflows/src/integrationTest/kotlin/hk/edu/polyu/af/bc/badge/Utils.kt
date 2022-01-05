package hk.edu.polyu.af.bc.badge

import com.github.manosbatsis.corda.testacles.nodedriver.NodeParamsHelper
import com.github.manosbatsis.corda.testacles.nodedriver.config.NodeDriverNodesConfig
import com.github.manosbatsis.corda.testacles.nodedriver.config.SimpleNodeDriverNodesConfig
import net.corda.testing.core.ALICE_NAME
import net.corda.testing.core.BOB_NAME


val nodeParamsHelper = NodeParamsHelper()

val customNodeDriverConfig: NodeDriverNodesConfig =
        SimpleNodeDriverNodesConfig (
                cordappPackages = listOf(
                        "hk.edu.polyu.af.bc.badge.contracts",
                        "hk.edu.polyu.af.bc.badge.flows",
                        "com.r3.corda.lib.tokens.workflows.flows",
                        "com.r3.corda.lib.tokens.contracts"
                ),
                nodes = mapOf("partyA" to nodeParamsHelper.toNodeParams(ALICE_NAME), "partyB" to nodeParamsHelper.toNodeParams(BOB_NAME)),
                minimumPlatformVersion = 4,
                debug = false
        )