#!/usr/bin/env bash
spark-submit --deploy-mode cluster --jars ${uber.name}.jar ${config.streaming.hdfs.ubication}