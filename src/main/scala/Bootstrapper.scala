import Common.{FetchPokemonByIdRequest, GetStatsRequest, GetStatsResponse, RequestBase}
import PokeApiRequester.PokeApiClient
import PokemonsDataStore.PokemonBase
import PokemonsDataStore.PokemonTraits.PokemonStats
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created by damie on 13/05/2017.
  */
object Bootstrapper extends App {

  override def main(a: Array[String]) = Initialize()

  def Initialize(): Unit ={
    // Do stuff
    val actorSystem = ActorSystem("Starburst")
    val waterActor = actorSystem.actorOf(Props(new PokemonBase(new PokemonStats("Water"))), name = "WateryThing")
    val fireActor = actorSystem.actorOf(Props(new PokemonBase(new PokemonStats("Fire"))), name = "FieryThing")
    val pokeApiClient = actorSystem.actorOf(Props[PokeApiClient], name = "pokeApiClient")
    val consoleWriter = actorSystem.actorOf(Props[ConsoleWriter], name = "consoleWriter")

    waterActor.tell(GetStatsRequest(), consoleWriter)
    fireActor.tell(GetStatsRequest(), consoleWriter)
    pokeApiClient.tell(FetchPokemonByIdRequest(13), consoleWriter)

  }
}

class ConsoleWriter extends Actor {
  override def receive: Receive =  {
    case GetStatsResponse(request : RequestBase, stats : PokemonStats) =>
      println("Received stats for pokemon of type: Primary: " + stats.Type.PrimaryType + " and Secondary: " + stats.Type.SecondaryType )
  }
}

