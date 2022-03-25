#!/bin/bash

function parse_yaml { 
  local s='[[:space:]]*' w='[a-zA-Z0-9_]*' fs=$(echo @|tr @ '\034')
  sed -ne "s|^\($s\):|\1|" -e "s|^\($s\)\($w\)$s:$s[\"']\(.*\)[\"']$s\$|\1$fs\2$fs\3|p" -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p"  $1 | awk -F$fs '{indent = length($1)/2; vname[indent] = $2; for (i in vname) {if (i > indent) {delete vname[i]}} if (length($3) > 0) {vn=""; for (i=0; i<indent; i++) {vn=(vn)(vname[i])("_")} printf("%s%s=\"%s\"\n", vn, $2, $3);}}'
}
eval $(parse_yaml /atrc_data/parameters.yaml)

VERSION=0.0.1a1
INPUT=$inputs_INPUT_path
COUNTRY=$inputs_COUNTRY_value
FIDELITY=$inputs_FIDELITY_value
CLEAN=$inputs_CLEAN_value
OUTPUT=$inputs_OUTPUT_path
# Probably want to add some other test cases / other parameters.yamls to fill these out
# BBOX = 
# RAIL =
# PTINFRA =
# RMMODE =
# ADDMODE =

PARAMS=""
[ ! -z "$INPUT" ] && PARAMS="${PARAMS} --input ${INPUT}"
[ ! -z "$COUNTRY" ] && PARAMS="${PARAMS} --country ${COUNTRY}"
[ ! -z "$FIDELITY" ] && PARAMS="${PARAMS} --fidelity ${FIDELITY}"
[ ! -z "$CLEAN" ] && PARAMS="${PARAMS} --CLEAN ${CLEAN}"
[ ! -z "$OUTPUT" ] && PARAMS="${PARAMS} --OUTPUT ${OUTPUT}"
[ ! -z "$BBOX" ] && PARAMS="${PARAMS} --BBOX ${BBOX}"
[ ! -z "$RAIL" ] && PARAMS="${PARAMS} --RAIL ${RAIL}"
[ ! -z "$PTINFRA" ] && PARAMS="${PARAMS} --PTINFRA ${PTINFRA}"
[ ! -z "$RMMODE" ] && PARAMS="${PARAMS} --RMMODE ${RMMODE}"
[ ! -z "$ADDMODE" ] && PARAMS="${PARAMS} --ADDMODE ${ADDMODE}"

CALL_STR="java -jar /app/jar/planit-aurin-parser-${VERSION}.jar ${PARAMS}"
echo $CALL_STR
eval $CALL_STR