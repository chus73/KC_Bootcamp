package com.kc.ark.config

//The model of the configurations

case class MongoConfig(uri:String, database:String, maxBatchSize:Int,collection:String,checkpointPath:String)
case class KafkaConfig(bootstrapServers:String,topics:List[String],maxOffSetsPerBatch:Int, startingOffsets:Option[String])
case class APIConfig(kafkaConfig: KafkaConfig,mongoConfig:MongoConfig)
case class AppConfig(fxcmConfig:APIConfig, calendarConfig:APIConfig,newsConfig:APIConfig,twitterConfig:APIConfig)