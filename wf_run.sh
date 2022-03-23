#!/bin/sh
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
    osmparserwrapper-test
    