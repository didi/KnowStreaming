package kafka.server

/**
 * Created by IntelliJ IDEA.
 * User: didi
 * Date: 2021/12/30
 */
trait ConfigListener {
  def onConfigChanged(rootEntityType:String,entity:String) : Unit
}
