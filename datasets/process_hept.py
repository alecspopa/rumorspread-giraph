#!/usr/local/bin/python

import random
import json

in_file = open("HEPT_edge_list.csv", "r")
out_file = open("HEPT_edge_list.json", "w")

# the probability for which each node is infected or not
infection_percentage = 0.35

# the probability a node can infect its neighbor
edge_value = 0.01

nodes = {}

for line in in_file:
    words = line.split(',')

    node_id = words[0]
    node_infected = 0 if random.random() > infection_percentage else 1

    if node_id not in nodes:
        nodes[node_id] = {'infected': node_infected, 'neighbors': [[int(words[1]), edge_value]]}
    else:
        nodes[node_id]['infected'] = node_infected
        nodes[node_id]['neighbors'].append([int(words[1]), edge_value])

for key, value in nodes.items():
    node_output = [int(key), value['infected'], value['neighbors']]
    out_file.write(json.dumps(node_output) + '\n')

in_file.close()
out_file.close()
