## Use this image

### Building the image

	docker build --tag alecspopa/giraph-cluster .

### Starting a NodeName

	docker run -d --name hdfs_namenode \
    	-h hdfs-namenode \
    	-p 50070:50070 \
    	alecspopa/giraph-cluster /etc/bootstrap.sh -bash -namenode && \
    docker logs -f hdfs_namenode

### Starting a Secondary NameNode

	docker run -d --name hdfs_secondarynamenode \
    	-h hdfs-secondarynamenode \
    	-p 50090:50090 \
    	--link=hdfs_namenode:hdfs-namenode \
    	alecspopa/giraph-cluster /etc/bootstrap.sh -bash -secondarynamenode && \
	docker logs -f hdfs_secondarynamenode

### Starting a DataNode

    docker run -d --name hdfs_datanode_1 \
    	-h hdfs-datanode1 \
    	-p 50075:50075 \
    	--link=hdfs_namenode:hdfs-namenode \
    	alecspopa/giraph-cluster /etc/bootstrap.sh -bash -datanode && \
	docker logs -f hdfs_datanode_1

### Starting YARN

	docker run -d --name yarn \
        -h yarn \
        -p 8088:8088 \
        -p 8042:8042 \
        --link=hdfs_namenode:hdfs-namenode \
        --link=hdfs_datanode_1:hdfs-datanode1 \
        alecspopa/giraph-cluster /etc/bootstrap.sh -bash -yarn && \
	docker logs -f yarn

## Using docker-compose

	docker-compose up -d && \
    	docker-compose logs

## Test instalation

	docker exec -it hdfs_namenode /bin/bash

## Run Example

### Put some data in HDFS

	docker run --rm \
        --link=hdfs_namenode:hdfs-namenode \
        --link=hdfs_datanode_1:hdfs-datanode1 \
        alecspopa/giraph-cluster \
        hadoop fs -put /usr/local/hadoop/README.txt /README.txt

### Start wordcount example

	docker run --rm \
        --link yarn:yarn \
        --link=hdfs-namenode:hdfs-namenode \
        alecspopa/giraph-cluster \
        hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.1.jar wordcount  /README.txt /README.result

### If `word-cound.result` already exists you need to remove it prior running the map reduce job.

    docker run --rm --link=hdfs-namenode:hdfs-namenode \
        --link=hdfs-datanode1:docker_datanode_1 \
        alecspopa/giraph-cluster \
        hadoop fs -rm -R -f /word-cound.result

### Check the result

	docker run --rm --link=hdfs-namenode:hdfs-namenode \
        --link=hdfs-datanode1:hdfs-datanode1 \
        alecspopa/giraph-cluster \
        hadoop fs -cat /word-cound.result/\*

## Build Docker Swarm cluster on Digital Ocean

    <https://www.digitalocean.com/community/tutorials/how-to-create-a-cluster-of-docker-containers-with-docker-swarm-and-digitalocean-on-ubuntu-16-04>

    docker-machine create -d digitalocean \
        --digitalocean-access-token="$DIGITALOCEAN_ACCESS_TOKEN" \
        --digitalocean-image="ubuntu-16-04-x64" \
        --digitalocean-size="1gb" \
        --digitalocean-region="fra1" \
        --digitalocean-private-networking=true \
        --digitalocean-ssh-key-fingerprint="20:e3:41:d8:bb:ce:5f:0b:43:99:3e:a9:1e:41:8b:f2" \
        --digitalocean-userdata=./digitalocean-namenode-userdata.yml \
        namenode

    docker-machine create -d digitalocean \
        --digitalocean-access-token="$DIGITALOCEAN_ACCESS_TOKEN" \
        --digitalocean-image="ubuntu-16-04-x64" \
        --digitalocean-size="1gb" \
        --digitalocean-region="fra1" \
        --digitalocean-private-networking=true \
        --digitalocean-ssh-key-fingerprint="20:e3:41:d8:bb:ce:5f:0b:43:99:3e:a9:1e:41:8b:f2" \
        --digitalocean-userdata=./digitalocean-datanode-userdata.yml \
        datanode-1

    docker-machine ls

    docker-machine ssh namenode

    root@namenode:~# docker swarm init --advertise-addr $MANAGER_NODE_IP_ADDRESS

    docker-machine ssh datanode-1

    root@datanode-1:~# docker swarm join \
        --token SWMTKN-1-0gb5x2vw3z7z6qscbsp2c6k5b683fp6fykbov9nfvptrbit27n-4klu9e0t2cxtmw6352n8gujyk \
        $MANAGER_NODE_IP_ADDRESS:2377
