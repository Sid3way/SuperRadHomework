package PokemonsDataStore.PokemonModel

import Common.PokemonAddedToSystem
import PokemonsDataStore.TypeIndexer
import akka.actor.{Actor, ActorRef, Props}

import scala.collection.mutable

/**
  * Created by damie on 23/05/2017.
  */
class TypeRouter extends Actor{
  var typeIndexers : mutable.Map[String, ActorRef] = mutable.Map[String, ActorRef]()
  override def receive: Receive = {
    case notif : PokemonAddedToSystem =>
      notif.pokemonModel.types.foreach(pokemonType => {
        if (!typeIndexers.contains(pokemonType.pokemonType)) {
            val newIndexerRef = context.actorOf(Props(new TypeIndexer(pokemonType.pokemonType)), pokemonType.pokemonType + "Indexer")
            typeIndexers += (pokemonType.pokemonType -> newIndexerRef)
          }
          typeIndexers(pokemonType.pokemonType).forward(notif)
      })
  }
}
