package converter

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
object Main {
  val config = ConfigFactory.load()
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val temp = config.getString("ngsConvert.originalFilePath")
    println(s"$temp")
  }
}