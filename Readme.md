### Run in Eclipse

*NOTE:* make sure JDK and JRE are installed

Import the proiect `org.school.rumorspread` in Eclipse as a Maven Project

Right Click `GiraphAppRunner` run as `Java Application`

It will give an error about the runtime arguments in main, so go to Run -> Run Configuration arguments and give it 2 arguments:
- the first one the input path for the data file (ex: `../datasets/rumorspread-graph.json`)
- the second one the output path for the result (ex: `../output`)

*NOTE 2:* paths are relative to `org.school.rumorspread` directory

*NOTE 3:* output directory must not exist and must be removed after each run

### Run in hadoop cluster

see (https://github.com/alecspopa/giraph-cluster) for how to install a Hadoop Giraph cluster

Run these commands `giraph-cluster` dir and make sure `rumorspread` is near the docker repo
otherwise change the paths for the volume mount.

    docker run -it --rm \
        --link=giraphcluster_namenode:giraphcluster-namenode \
        --net=giraphcluster_default \
        --volume=$(pwd)/../rumorspread/datasets:/tmp/datasets \
        --volume=$(pwd)/../rumorspread/org.school.rumorspread:/tmp/source \
        alecspopa/giraph-cluster \
        /bin/bash

*Inside the container*

Install maven in container if you need to build the .jar

    cd /tmp/source

    apt-get update
    apt-get install maven
    mvn clean install

Add data into HDFS

    cd /tmp/datasets

    hadoop fs -put /tmp/datasets/rumorspread-graph.json /rumorspread-graph.json

Run the computation

    hadoop jar /tmp/source/target/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
        org.apache.giraph.GiraphRunner org.school.rumorspread.RumorSpreadComputation \
        --yarnjars org.school.rumorspread-0.0.1-SNAPSHOT.jar \
        --workers 1 \
        -ca giraph.SplitMasterWorker=false \
        --vertexInputFormat org.school.rumorspread.RumorSpreadInputFormat \
        --vertexInputPath /rumorspread-graph.json \
        --vertexOutputFormat org.school.rumorspread.RumorSpreadOutputFormat \
        --outputPath /rumorspread-sample-output

    hadoop jar /tmp/source/target/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
        org.apache.giraph.GiraphRunner org.school.rumorspread.RumorSpreadComputation \
        --yarnjars org.school.rumorspread-0.0.1-SNAPSHOT.jar \
        --workers 1 \
        -ca giraph.SplitMasterWorker=false \
        --vertexInputFormat org.school.rumorspread.RumorSpreadInputFormat \
        --vertexInputPath /twitter_mention.json \
        --vertexOutputFormat org.school.rumorspread.RumorSpreadOutputFormat \
        --outputPath /twitter-mentions-output

Inspect the output

    hadoop fs -ls /rumorspread-sample-output
    hadoop fs -cat /rumorspread-sample-output/part-m-00000

Remove output

    hadoop fs -rm -r /rumorspread-sample-output
