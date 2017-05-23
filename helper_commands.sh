ssh -i ~/.ssh/id_rsa_uvt bogdan@35.187.107.244
scp -i ~/.ssh/id_rsa_uvt ~/Downloads/HEPT_edge_list.json bogdan@35.187.107.244:/tmp/HEPT_edge_list.json

cp /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/hadoopuser/hadoop/share/hadoop/yarn/lib/

hdfs dfs -copyFromLocal /tmp/twitter_mention.json /user/hadoopuser/input/twitter_mention.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_100k.json /user/hadoopuser/input/twitter_mention_100k.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_250k.json /user/hadoopuser/input/twitter_mention_250k.json
hdfs dfs -copyFromLocal /tmp/twitter_mention_500k.json /user/hadoopuser/input/twitter_mention_500k.json

hdfs dfs -mkdir -p /user/hadoopuser/input
hdfs dfs -mkdir -p /user/hadoopuser/output

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.apache.giraph.GiraphRunner -D \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation \
    -vif org.school.rumorspread.RumorSpreadInputFormat \
    -vip /user/hadoopuser/input/twitter_mention.json \
    -vof org.school.rumorspread.RumorSpreadOutputFormat \
    -w 3 \
    -op /user/hadoopuser/output/rumorspread-full-dataset

hdfs dfs -copyFromLocal /tmp/HEPT_edge_list.json /user/hadoopuser/input/HEPT_edge_list.json

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.apache.giraph.GiraphRunner -D \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation \
    -vif org.school.rumorspread.RumorSpreadInputFormat \
    -vip /user/hadoopuser/input/HEPT_edge_list.json \
    -vof org.school.rumorspread.RumorSpreadOutputFormat \
    -w 1 \
    -op /user/hadoopuser/output/hept-full-dataset

hdfs dfs -copyFromLocal /tmp/twitter_mention_50k.json /user/hadoopuser/input/twitter_mention_50k.json

# --------

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/HEPT_edge_list.json \
    -out /user/hadoopuser/output/hept-full-dataset \
    -w 1 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/HEPT_edge_list_75_percent.json \
    -out /user/hadoopuser/output/hept-75-percent-dataset \
    -w 1 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/twitter_mention_50k.json \
    -out /user/hadoopuser/output/twitter-mention-50k \
    -w 1 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    -in /user/hadoopuser/input/twitter_mention_250k.json \
    -out /user/hadoopuser/output/twitter-mention-250k \
    -w 8 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181,datanode3:2181,datanode4:2181,datanode5:2181,datanode6:2181,datanode7:2181" \
    org.school.rumorspread.RumorSpreadComputation

