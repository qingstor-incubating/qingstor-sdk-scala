package com.qingstor.sdk.steps

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("../../src/test/resources/features/service.feature"),
  plugin = Array("pretty")
)
class TestRunner
