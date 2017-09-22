package com.rajeev.temp.basket

import akka.actor.ActorLogging
import akka.persistence._
import BasketModels._

/**
  * Created by rajeevprasanna on 9/18/17.
  */
class BasketActor extends PersistentActor with ActorLogging {

  var itemsInBasket = Items(Nil)
  var nrOfEventsRecovered = 0

  override def persistenceId: String = "sample-persistent-basket-actor"

  //General updateState method for re-use
  //Used by both receiveRecover and receiveCommand
  //This updates the internal state using an event
  val updateState:(Event => Unit) = {
    case Added(item) => itemsInBasket = itemsInBasket.add(item)
    case ItemRemoved(productId) => itemsInBasket = itemsInBasket.removeItem(productId)
    case ItemUpdated(id, amount) => itemsInBasket = itemsInBasket.updateItem(id, amount)
    case Replaced(newItems) => itemsInBasket = newItems
    case Cleared() => itemsInBasket = itemsInBasket.clear
  }

  //Events come here (recovery phase) from database (snapshot and event)
  override def receiveRecover: Receive = {
    // recovering from event journal database
    case event:Event =>
        log.info(s"Recovering events from event journal, persistence ID: $persistenceId")
        log.info(s"Event: $event")
        nrOfEventsRecovered = nrOfEventsRecovered + 1
        updateState(event)

    // recovering from snapshot journal database
    case SnapshotOffer(metadata, basketSnapshot:BasketSnapshot) =>
      log.info(s"Recovering basket from snapshot, persistence ID: $persistenceId")
      log.info(s"Metadata: $metadata")
      log.info(s"Snapshot Data: $basketSnapshot")
      itemsInBasket = basketSnapshot.items

    case RecoveryCompleted =>
        log.info(s"Recovery complete for persistence ID: $persistenceId")
  }

  //Commands come here (active phase)
  override def receiveCommand: Receive = {

    //Turn command into event, persist it then update the internal state
    case AddItem(item, _) => persist(Added(item))(updateState)

    case RemoveItem(productId, _) =>
          if(itemsInBasket.containsProduct(productId)){
            persist(ItemRemoved(productId)){
              itemRemovedEvent =>
                  updateState(itemRemovedEvent)
                  sender ! Some(itemRemovedEvent)
            }
          }else sender ! None


    case UpdateItem(productId, amount, _) =>
          if(itemsInBasket.containsProduct(productId)){
            persist(ItemUpdated(productId, amount)) {
              itemUpdatedEvent =>
                updateState(itemUpdatedEvent)
                sender ! Some(itemUpdatedEvent)
            }
          }else{
            sender ! None
          }

    case Replace(items, _) => persist(Replaced(items))(updateState)

    //Save snapshot on basket clear
    //save snapshot will cause SaveSnapshotFailure or SaveSnapshotSuccess messages to be sent to the current actor
    case Clear(_) => persist(Cleared()){
      clearedEvent =>
        updateState(clearedEvent)
        log.info("Clear event issued: Saving snapshot")
        saveSnapshot(BasketSnapshot(itemsInBasket))
    }

    case GetItems(_) => sender ! itemsInBasket

    case PrintItems(_) =>
      log.info("Items in Basket:")
      log.info(s"$itemsInBasket")

    case CountRecoveredEvents(_) => sender ! RecoveredEventsCount(nrOfEventsRecovered)

    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Snapshot failed to save:")
      log.error(s"Metadata: $metadata")
      log.error(s"Reason: $reason")

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot saved successfully")
      log.info(s"Metadata: $metadata")
  }

}
