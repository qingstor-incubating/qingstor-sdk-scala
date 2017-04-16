package com.qingstor.sdk.config

import java.io._
import java.util

import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.util.{ConversionUtil, QSLogger}
import org.yaml.snakeyaml.{DumperOptions, Yaml}

case class QSConfig(var accessKeyId: String = "",
                    var secretAccessKey: String = "",
                    var host: String = "qingstor.com",
                    var port: Int = 443,
                    var protocol: String = "https",
                    var connectionRetries: Int = 3,
                    var logLevel: String = QSConstants.LogWarn
                   )

object QSConfig {
  private val dumpOption = new DumperOptions()
  dumpOption.setPrettyFlow(true)
  private val yaml = new Yaml(dumpOption)
  private val defaultConfigFile = "~/.qingstor/config.yaml"

  // loadUserConfig loads user configuration in ~/.qingstor/config.yaml for Config.
  def loadUserConfig(): QSConfig = {
    try {
      loadConfigFromFile(defaultConfigFile)
    } catch {
      case fnd: FileNotFoundException =>
        QSLogger.warn(
          "Installing default config file to %s".format(
            convertToRealPath(defaultConfigFile)))
        installDefaultUserConfig()
        QSConfig()
    }
  }

  // loadConfigFromFile loads configuration from a specified local path.
  @throws[FileNotFoundException]
  def loadConfigFromFile(filepath: String): QSConfig = {
    val ins = new FileInputStream(new File(convertToRealPath(filepath)))
    val map = yaml.load(ins).asInstanceOf[java.util.Map[String, AnyRef]]
    constructQSConfig(map)
  }

  // loadConfigFromContent loads configuration from given string.
  def loadConfigFromContent(content: String): QSConfig = {
    val map = yaml.load(content).asInstanceOf[java.util.Map[String, AnyRef]]
    constructQSConfig(map)
  }

  // installDefaultUserConfig install default configuration file to ~/.qingstor/
  private def installDefaultUserConfig() = {
    val configFile = new File(convertToRealPath(defaultConfigFile))
    val configDir = new File(configFile.getParent)
    if (!configDir.exists())
      configDir.mkdirs()
    val writer = new BufferedWriter(new FileWriter(configFile))
    val defaultContent = yaml.dumpAsMap(configToMap(QSConfig()))
    writer.write(defaultContent)
    writer.close()
  }

  private val userHome = scala.util.Properties.userHome

  // convertToRealPath convert '~/xxx' to real path
  private def convertToRealPath(filepath: String): String =
    filepath.replaceFirst("~/", userHome + "/")

  private def underscoreToCamelCase(originString: String): String = {
    val words = originString.split("_")
    val newWords = for (w <- words) yield if (w.equals(words.head)) w else w.capitalize
    newWords.mkString
  }

  private def camelToUnderscore(name: String) = "[A-Z\\d]".r.replaceAllIn(name, {m =>
    "_" + m.group(0).toLowerCase()
  })

  private def constructQSConfig(map: util.Map[String, AnyRef]): QSConfig = {
    val scalaMap = ConversionUtil.jMapAsScalaMap(map)
    val config = QSConfig()
    scalaMap.keySet.foreach{ key =>
      val field = classOf[QSConfig].getDeclaredField(underscoreToCamelCase(key))
      val method = classOf[QSConfig].getDeclaredMethod(field.getName + "_$eq", field.getType)
      method.invoke(config, scalaMap(key))
    }
    config
  }

  private def configToMap(config: QSConfig): util.Map[String, AnyRef] = {
    val map = new util.HashMap[String, AnyRef]()
    val fields = classOf[QSConfig].getDeclaredFields.map(_.getName)
    fields.foreach { name =>
      val method = classOf[QSConfig].getDeclaredMethod(name)
      val value = method.invoke(config)
      map.put(camelToUnderscore(name), value)
    }
    map
  }
}
