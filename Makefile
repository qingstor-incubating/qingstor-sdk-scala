SHELL := /bin/bash

PREFIX="qingstor-sdk-scala"
VERSION=$(shell cat build.sbt |grep "version\ :=" |sed -e s/version\ :=\ //g |sed s/\"//g)

.PHONY: help
help:
	@echo "Please use \`make <target>\` where <target> is one of"
	@echo "  all               to update, generate, unit, test and release this SDK"
	@echo "  update            to update git submodules"
	@echo "  generate          to generate service code"
	@echo "  unit              to run all sort of unit tests except runtime"
	@echo "  test              to run service test"
	@echo "  release           to build and release current version"

.PHONY: all
all: update generate unit test release

.PHONY: update
update:
	git submodule init
	git submodule update --remote
	@echo "ok"

.PHONY: generate
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

.PHONY: unit
unit:
	@echo "run unit test"
	sbt test
	@echo "ok"

.PHONY: test
test:
	@echo "run service test"
	@if [[ ! -f "$$(which scalac)" ]]; then \
		echo "ERROR: Command \"scalac\" not found."; \
	fi
	sbt assembly
	scalac -cp release/${PREFIX}-${VERSION}-full.jar src/test/scala/com/qingstor/sdk/steps/*.scala
	scala -cp "release/${PREFIX}-${VERSION}-full.jar:com/qingstor/sdk/steps/*:." org.junit.runner.JUnitCore com.qingstor.sdk.steps.TestRunner
	rm -rf com release
	@echo "ok"

.PHONY: release
release:
	@echo "pack the source code"
	sbt assembly
	@echo "ok"
