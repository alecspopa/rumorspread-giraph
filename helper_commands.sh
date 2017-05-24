ssh -i ~/.ssh/id_rsa_uvt bogdan@35.187.107.244
scp -i ~/.ssh/id_rsa_uvt ~/Downloads/HEPT_edge_list.json bogdan@35.187.107.244:/tmp/HEPT_edge_list.json

cp /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/hadoopuser/hadoop/share/hadoop/yarn/lib/

hdfs dfs -copyFromLocal /tmp/twitter_mention.json /user/hadoopuser/input/twitter_mention.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_100k.json /user/hadoopuser/input/twitter_mention_100k.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_250k.json /user/hadoopuser/input/twitter_mention_250k.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_500k.json /user/hadoopuser/input/twitter_mention_500k.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_750k.json /user/hadoopuser/input/twitter_mention_750k.json

hdfs dfs -mkdir -p /user/hadoopuser/input
hdfs dfs -mkdir -p /user/hadoopuser/output

# --------

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/HEPT_edge_list.json \
    -out /user/hadoopuser/output/HEPT_edge_list \
    -w 2 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181,datanode3:2181,datanode4:2181,datanode5:2181,datanode6:2181,datanode7:2181" \
    org.school.rumorspread.RumorSpreadComputation


hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/twitter_mention_500k.json \
    -out /user/hadoopuser/output/twitter-mention_500k \
    -w 4 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181,datanode3:2181,datanode4:2181,datanode5:2181,datanode6:2181,datanode7:2181" \
    org.school.rumorspread.RumorSpreadComputation

