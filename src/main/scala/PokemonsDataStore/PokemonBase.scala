package PokemonsDataStore

import Common.{GetStatsRequest, GetStatsResponse}
import PokemonsDataStore.PokemonModel.PokemonStats
import akka.actor.Actor

/**
  * Created by damie on 16/05/2017.
  */
class PokemonBase(PokemonStats: PokemonStats) extends Actor {
  val Stats = PokemonStats

  override def receive: Receive = {
    case request : GetStatsRequest =>
      println("Received get stats request")
      sender ! GetStatsResponse(request, Stats)
    case _ => println("wtf")
  }
}
