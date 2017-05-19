package PokemonsDataStore

import Common.GetStatsRequest
import PokemonsDataStore.PokemonModel.PokemonStats
import akka.actor.{Actor, Props}

/**
  * Created by damie on 19/05/2017.
  */
class Pokedex extends Actor{

  override def preStart(): Unit = {
    super.preStart()
    context.actorOf(Props(new PokemonBase(new PokemonStats("Electric"))), "pikachu")
    context.actorOf(Props(new PokemonBase(new PokemonStats("Fire"))), "charizard")
  }

  override def receive: Receive = {
    case request : GetStatsRequest =>
      val path = context.self.path + "/" + request.pokemonName
      println("Received request for pokemon named " + request.pokemonName + ". Searching at path: " + path)
      context.actorSelection(path).forward(request)
  }
}
