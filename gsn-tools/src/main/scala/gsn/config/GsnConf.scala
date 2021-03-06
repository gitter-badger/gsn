package gsn.config

import xml._
import com.typesafe.config.ConfigFactory


case class GsnConf(name:String,author:String,description:String,
    email:String,port:Int,timeFormat:String, 
    zmqConf:ZmqConf,accessControl:AcConf,
    storageConf:StorageConf,slidingConf:Option[StorageConf])
    
object GsnConf extends Conf {
  def create(xml:Elem)=GsnConf(
    take(xml \ "name").getOrElse(defaultGsn.name ),
    take(xml \ "author").getOrElse(defaultGsn.author ),
    take(xml \ "description").getOrElse(defaultGsn.description ),
    take(xml \ "email").getOrElse(defaultGsn.email ),
    takeInt(xml \ "port").getOrElse(defaultGsn.port ),    
    take(xml \ "time-format").getOrElse(defaultGsn.timeFormat ),
    ZmqConf.create(xml),AcConf.create(xml),
    StorageConf.create((xml \ "storage").head),
    (xml \ "sliding").headOption.map(a=>StorageConf.create((a \ "storage").head))
  )
  def load(path:String)=create(XML.load(path))
}
 
case class ZmqConf(enabled:Boolean,proxyPort:Int,metaPort:Int) 
object ZmqConf extends Conf{
  def create(xml:scala.xml.Elem)=ZmqConf(
    takeBool(xml \ "zmq-enable").getOrElse(defaultZmq.enabled),
    takeInt(xml \ "zmqproxy").getOrElse(defaultZmq .proxyPort),
    takeInt(xml \ "zmqmeta").getOrElse(defaultZmq.metaPort ) )  
}

case class AcConf(enabled:Boolean,sslPort:Int,sslKeyStorePass:String,sslKeyPass:String,sslKeyStore:String) 
object AcConf extends Conf{
  def create(xml:Elem)=AcConf(
    takeBool(xml \ "access-control").getOrElse(defaultAc.enabled ),
    takeInt(xml \ "ssl-port").getOrElse(defaultAc.sslPort),
    take(xml \ "ssl-key-store-password").getOrElse(defaultAc.sslKeyStorePass),
    take(xml \ "ssl-key-password").getOrElse(defaultAc.sslKeyPass),
    take(xml \ "ssl-key-store").getOrElse(defaultAc.sslKeyStore))
}

case class StorageConf(driver:String,url:String,
    user:String,pass:String,identifier:Option[String]) 
object StorageConf extends Conf{
  def create(xml:Node)=StorageConf(
    xml \@ "driver",
    xml \@ "url",
    xml \@ "user",
    xml \@ "password",
    xml.attribute("identifier").map(_.toString))  
}

