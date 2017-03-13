package com.qingstor.sdk.steps

import cucumber.api.CucumberOptions
import org.junit.runner.RunWith
import cucumber.api.junit.Cucumber

@RunWith(classOf[Cucumber])
@CucumberOptions(features = Array("src/test/resources/features"))
class TestRunner
