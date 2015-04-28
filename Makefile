SNAKEYAML_JAR=./lib/snakeyaml-1.15.jar
GUAVA_JAR=./lib/guava-18.0.jar
TOSCA_YAML_PARSER_DIR=/Users/mat/gits/tosca-yaml-parser/src/main/java
TOSCA_YAML_PARSER_EXAMPLES_DIR=/Users/mat/gits/tosca-yaml-parser/examples/src/main/java
JSON_SIMPLE_JAR=./lib/json-simple-1.1.1.jar

all: service

service: src/seaclouds/planner/WebServiceLayer.java
	javac -cp ./src:$SNAKEYAML_JAR:$GUAVA_JAR:$TOSCA_YAML_PARSER_DIR:$TOSCA_YAML_PARSER_EXAMPLES:$JSON_SIMPLE_JAR:${TOMCAT_LIB_DIR}/servlet-api.jar src/seaclouds/planner/WebServiceLayer.java -d build/

clean:
	rm -rf build