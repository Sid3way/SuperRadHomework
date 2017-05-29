package PokemonsDataStore

import Common.{GetStatsRequest, PokemonAddedToSystem}
import PokemonsDataStore.PokemonModel.PokemonModel
import akka.actor.{Actor, ActorRef}

/**
  * Created by damie on 16/05/2017.
  */
class PokemonBase(pokemonModel: PokemonModel, typeRouter : ActorRef) extends Actor {

  override def preStart(): Unit = {
    super.preStart()
    println("Pokemon " + pokemonModel.name + " spawned with data: " + pokemonModel)
    typeRouter ! PokemonAddedToSystem(pokemonModel)
  }

  override def receive: Receive = {
    case request : GetStatsRequest =>
      println("Received get stats request")
      //sender ! GetStatsResponse(request, Stats, RequestStatus.Success)
    case _ => println("wtf")
  }
}
