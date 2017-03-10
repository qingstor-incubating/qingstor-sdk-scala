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
