package com.lsc

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import scala.jdk.CollectionConverters._

import java.io.{File, FileWriter, BufferedWriter}
import com.typesafe.config.{Config, ConfigFactory}
object CreateYaml {
  private val config = ConfigFactory.load()
  def outputYaml(result: List[String]): Unit = {
    val dumperOptions = new DumperOptions()
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    dumperOptions.setPrettyFlow(true)

    val yaml = new Yaml(dumperOptions)
    val output = result.asJava

    val writer = new BufferedWriter(new FileWriter(config.getString("Local.outputFilePath")))
    yaml.dump(output, writer)
    writer.close()
  }
}
