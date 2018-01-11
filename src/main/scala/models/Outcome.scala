package models

case class Outcome(id: Id, `type`: Type, value: Long, timestamp: Long) extends Transaction