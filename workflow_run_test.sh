#!/bin/bash

# This test script simulates the steps that the
# workflow runner will perform when running a 
# particular operation. 

# Build the container image:
docker build -t osmparserwrapper .

# Create folders for data input and output:
mkdir data
mkdir data/inputs
mkdir data/outputs

# Copy over test parameters and files:
cp test_inputs/* data/inputs/
cp test_parameters.yaml data/parameters.yaml

# Run the container:
docker run \
    --mount type=bind,source="$(pwd)"/data,target=/data \
    osmparserwrapper
    