/**
 *
 * Copyright (c) 2019 Cisco and/or its affiliates.
 *
 * This software is licensed to you under the terms of the Cisco Sample
 * Code License, Version 1.0 (the "License"). You may obtain a copy of the
 * License at
 *
 *                https://developer.cisco.com/docs/licenses
 *
 * All use of the material herein must be in accordance with the terms of
 * the License. All rights not expressly granted by the License are
 * reserved. Unless required by applicable law or agreed to separately in
 * writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.example

import org.dsa.iot.dslink.node.Permission
import org.dsa.iot.dslink.node.actions.table.Row
import org.dsa.iot.dslink.node.actions.{Action, ActionResult, Parameter}
import org.dsa.iot.dslink.node.value.{Value, ValueType}
import org.dsa.iot.dslink.{DSLink, DSLinkFactory, DSLinkHandler}
import org.slf4j.LoggerFactory

object DeleteNodeTest {
  def main(args: Array[String]): Unit = {
    DSLinkFactory.start(args, new DeleteNodeTestDSLinkHandler())
  }
}


class DeleteNodeTestDSLinkHandler() extends DSLinkHandler {
  private val log = LoggerFactory.getLogger(getClass)
  override val isResponder = true

  override def onResponderInitialized(link: DSLink): Unit = {
    val superRoot = link.getNodeManager.getSuperRoot
    superRoot
      .createChild("addNode", true)
      .setDisplayName("Add Node")
      .setAction(new Action(Permission.CONFIG, (event: ActionResult) => {
        val name = event.getParameter("Name").getString
        if (superRoot.getChild(name, true) != null) {
          event.getTable.addRow(Row.make(new Value(s"Fail: Node $name already exists.")))
        } else {
          val newNode = superRoot.createChild(name, true).build()
          newNode.createChild("delete", true)
            .setDisplayName("Delete")
            .setAction(new Action(Permission.CONFIG, (_: ActionResult) => {
              newNode.getParent.removeChild(newNode, true)
            })).build()
        }
      }).addParameter(new Parameter("Name", ValueType.STRING).setDescription("Name of new Node"))
      )
      .build()

    log.info("DeleteNodeTest initialized")
  }

  override def onResponderConnected(link: DSLink): Unit = {
    log.info("DeleteNodeTest connected")
  }
}
