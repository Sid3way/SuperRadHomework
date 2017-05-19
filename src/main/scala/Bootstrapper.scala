import Common._
import PokeApiRequester.PokeApiClient
import PokemonsDataStore.{Pokedex, PokemonBase}
import PokemonsDataStore.PokemonModel.PokemonStats
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created by damie on 13/05/2017.
  */
object Bootstrapper extends App {

  override def main(a: Array[String]) = Initialize()

  def Initialize(): Unit ={
    // Do stuff
    val actorSystem = ActorSystem("Starburst")
    val pokedex = actorSystem.actorOf(Props[Pokedex], name="pokedex")
    val pokeApiClient = actorSystem.actorOf(Props[PokeApiClient], name = "pokeApiClient")
    val consoleWriter = actorSystem.actorOf(Props[ConsoleWriter], name = "consoleWriter")

    pokedex.tell(GetStatsRequest("pikachu"), consoleWriter)
    pokedex.tell(GetStatsRequest("cha*"), consoleWriter)
    pokeApiClient.tell(FetchPokemonByIdRequest(13), consoleWriter)

  }
}

class ConsoleWriter extends Actor {
  override def receive: Receive =  {
    case GetStatsResponse(request : RequestBase, stats : PokemonStats) =>
      println("Received stats for pokemon of type: Primary: " + stats.Type.PrimaryType + " and Secondary: " + stats.Type.SecondaryType )
  }
}

