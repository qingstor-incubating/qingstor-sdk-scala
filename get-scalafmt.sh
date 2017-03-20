#!/bin/bash

# Get scalafmt tool
coursier bootstrap com.geirsson:scalafmt-cli_2.11:0.6.3 --main org.scalafmt.cli.Cli -o scalafmt
