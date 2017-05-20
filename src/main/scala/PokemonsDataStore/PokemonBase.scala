package PokemonsDataStore

import Common.GetStatsRequest
import PokemonsDataStore.PokemonModel.PokemonModel
import akka.actor.Actor

/**
  * Created by damie on 16/05/2017.
  */
class PokemonBase(pokemonModel: PokemonModel) extends Actor {
  private val model = pokemonModel

  override def preStart(): Unit = {
    super.preStart()
    println("Pokemon " + pokemonModel.name + " spawned with data: " + pokemonModel)
  }

  override def receive: Receive = {
    case request : GetStatsRequest =>
      println("Received get stats request")
      //sender ! GetStatsResponse(request, Stats, RequestStatus.Success)
    case _ => println("wtf")
  }
}
