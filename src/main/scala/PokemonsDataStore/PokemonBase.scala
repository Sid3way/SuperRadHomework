package PokemonsDataStore

import Common.{GetStatsRequest, GetStatsResponse}
import PokemonsDataStore.PokemonTraits.PokemonStats
import akka.actor.Actor

/**
  * Created by damie on 16/05/2017.
  */
class PokemonBase(PokemonStats: PokemonStats) extends Actor {
  val Stats = PokemonStats

  def MyMessageHandler(): GetStatsResponse =
    return new GetStatsResponse(Stats)

  override def receive: Receive = {
    case GetStatsRequest() =>
      MyMessageHandler()
  }
}
