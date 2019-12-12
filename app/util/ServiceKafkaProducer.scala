package util

import java.util.concurrent.Future

import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.RecordMetadata
import play.api.Configuration


class ServiceKafkaProducer(topicName: String,
                           actorSystem: ActorSystem, configuration: Configuration) {

  val bootstrapServers = configuration.get[String]("kafka.bootstrap.servers")

  import akka.kafka.ProducerSettings
  import org.apache.kafka.common.serialization.StringSerializer

  val producerSettings = ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)

  val producer = producerSettings.createKafkaProducer()

  import org.apache.kafka.clients.producer.ProducerRecord
  def send(logRecordStr: String): Future[RecordMetadata] = {
    producer.send(new ProducerRecord(topicName, logRecordStr))
  }
}

