function parse_yaml { 
  local s='[[:space:]]*' w='[a-zA-Z0-9_]*' fs=$(echo @|tr @ '\034')
  sed -ne "s|^\($s\):|\1|" -e "s|^\($s\)\($w\)$s:$s[\"']\(.*\)[\"']$s\$|\1$fs\2$fs\3|p" -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p"  $1 |
  awk -F$fs '{indent = length($1)/2; vname[indent] = $2; for (i in vname) {if (i > indent) {delete vname[i]}} if (length($3) > 0) {vn=""; for (i=0; i<indent; i++) {vn=(vn)(vname[i])("_")} printf("%s%s=\"%s\"\n", vn, $2, $3);}}'
}
eval $(parse_yaml test_parameters.yaml)

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

PARAM_STR=""
[ ! -z "$INPUT" ] && PARAM_STR+="--input ${INPUT} "
[[ ! -z "$COUNTRY" ]] && PARAM_STR+="--country ${COUNTRY} "
[[ ! -z "$FIDELITY" ]] && PARAM_STR+="--fidelity ${FIDELITY} "
[[ ! -z "$CLEAN" ]] && PARAM_STR+="--CLEAN ${CLEAN} "
[[ ! -z "$OUTPUT" ]] && PARAM_STR+="--OUTPUT ${OUTPUT} "
[[ ! -z "$BBOX" ]] && PARAM_STR+="--BBOX ${BBOX} "
[[ ! -z "$RAIL" ]] && PARAM_STR+="--RAIL ${RAIL} "
[[ ! -z "$PTINFRA" ]] && PARAM_STR+="--PTINFRA ${PTINFRA} "
[[ ! -z "$RMMODE" ]] && PARAM_STR+="--RMMODE ${RMMODE} "
[[ ! -z "$ADDMODE" ]] && PARAM_STR+="--ADDMODE ${ADDMODE} "

CALL_STR="java -jar /app/jar/planit-aurin-parser-${VERSION}.jar "
CALL_STR+=$PARAM_STR

echo $CALL_STR
eval $CALL_STR