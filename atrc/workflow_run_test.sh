#!/bin/bash

# This test script simulates the steps that the
# workflow runner will perform when running a 
# particular operation. 

# Build the container image: (this can be commented out for speedier test runs if the image is current)
docker build -t osmparserwrapper-atrc -f atrc/Dockerfile .

# Create folders for data input and output:
mkdir data
mkdir data/inputs
mkdir data/outputs

# Copy over test parameters and files:
cp atrc/test_inputs/* data/inputs/
cp atrc/test_parameters.yaml data/parameters.yaml

# Run the container:
docker run \
    --mount type=bind,source="$(pwd)"/data,target=/data \
    osmparserwrapper-atrc
    