ssh -i ~/.ssh/id_rsa_uvt bogdan@35.187.107.244
scp -i ~/.ssh/id_rsa_uvt ~/Downloads/HEPT_edge_list.json bogdan@35.187.107.244:/tmp/HEPT_edge_list.json

cp /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/hadoopuser/hadoop/share/hadoop/yarn/lib/

hdfs dfs -copyFromLocal /tmp/twitter_mention.json /user/hadoopuser/input/twitter_mention.json

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
    /user/hadoopuser/input/HEPT_edge_list.json \
    /user/hadoopuser/output/hept-full-dataset \
    1 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation

hadoop jar /tmp/org.school.rumorspread-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    org.school.rumorspread.GiraphAppRunner \
    /user/hadoopuser/input/twitter_mention.json \
    /user/hadoopuser/output/twitter-mention-full-dataset \
    1 \
    giraph.zkList="namenode:2181,datanode:2181,datanode1:2181,datanode2:2181" \
    org.school.rumorspread.RumorSpreadComputation

