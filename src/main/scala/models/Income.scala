package models

case class Income(id: Id, `type`: Type, value: Long, timestamp: Long) extends Transaction