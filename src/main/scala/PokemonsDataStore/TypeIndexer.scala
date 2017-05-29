package PokemonsDataStore

import Common._
import PokemonsDataStore.PokemonModel.{PokemonStat, PokemonStatsDiff}
import akka.actor.Actor

/**
  * Created by damie on 23/05/2017.
  */
class TypeIndexer(indexType : String) extends Actor {

  var indexedPokemons : Map[Int, List[PokemonStat]] = Map[Int, List[PokemonStat]]()
  var averageStats : List[PokemonStat] = List[PokemonStat]()

  def onComparisonRequestReceived(request : GetStatsComparisonRequest): Unit = {
    val pokemonStats = indexedPokemons(request.id)
    // todo: do stat comparions and add it to pokemon diff
    sender() ! GetStatsComparisonResponse(request, PokemonStatsDiff(averageStats, averageStats), RequestStatus.Success)
  }

  override def receive: Receive = {
    case request : GetStatsComparisonRequest =>
      onComparisonRequestReceived(request)
    case PokemonAddedToSystem(pokemonModel) =>
      println(s"Adding pokemon ${pokemonModel.name} to index of type $indexType")
      indexedPokemons += (pokemonModel.id -> pokemonModel.pokemonStats)
      // todo: compute average and store it
  }
}
