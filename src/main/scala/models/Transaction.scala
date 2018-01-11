package models

abstract class Transaction {
  def id: Id
  def `type`: Type
  def value: Long
  def timestamp: Long
}