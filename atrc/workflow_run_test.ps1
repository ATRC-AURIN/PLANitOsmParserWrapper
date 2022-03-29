# This test script simulates the steps that the
# workflow runner will perform when running a 
# particular operation in Windows powershell 

# Build the container image: (this can be commented out for speedier test runs if the image is current)
docker build -t osmparserwrapper-atrc:latest -f ./atrc/Dockerfile .

# Create folders for data input and output within ATRC sub dir:
mkdir -Force ./atrc_data
mkdir -Force ./atrc_data/inputs
mkdir -Force ./atrc_data/outputs

# Copy over test parameters and files:
cp ./atrc/test_inputs/* ./atrc_data/inputs/
cp ./atrc/test_parameters.yaml ./atrc_data/parameters.yaml

# Run the container:
docker run --mount type=bind,source="${PWD}"/atrc_data,target=/atrc_data osmparserwrapper-atrc:latest
    