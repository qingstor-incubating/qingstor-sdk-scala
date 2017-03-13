SHELL := /bin/bash

generate:
	@if [[ ! -f "$$(which snips)" ]]; then \
        echo "ERROR: Command \"snips\" not found."; \
    fi
	snips \
        --service=qingstor --service-api-version=latest \
        --spec="./specs" --template="./template" \
        --output="./src/main/scala/com/qingstor/sdk/service"
	./scalafmt
	@echo "OK"

test: 
	@echo "run service test"
	@if [[ ! -f "$$(which scalac)" ]]; then \
		echo "ERROR: Command \"scalac\" not found."; \
	fi
	sbt assembly
	pushd "test"
	scalac -cp steps/qingstor-fat.jar steps/*.scala
	scala -cp "./steps/*:." org.junit.runner.JUnitCore steps.TestRunner
	popd
	rm -f steps/*.class
	rm -f steps/*.jar
	@echo "ok"

